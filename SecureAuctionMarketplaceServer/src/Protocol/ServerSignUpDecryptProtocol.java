package Protocol;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class ServerSignUpDecryptProtocol {
	private Key privateKey = null;
	private static final String CIPHERALGORITHM = "AES/CBC/PKCS5Padding";
	
	public ServerMessageStorage decryptClientMessage(byte[] mMessage) throws Exception 
	{
		//----------------------- Get each byte array length ------------------------
		//Get the byte array of key length
		int encodedKeyLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(mMessage, 0, 4));
		//Get the byte array of iv length
		int ivLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(mMessage, 4, 8));
		//Get the byte array of cipher message length
		int messageLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(mMessage, 8, 12));
		//Get the hash message length
		int hashMessageLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(mMessage, 12, 16));
		
		//----------------------- Get the actual byte array of content in encrypted format
		//Get the byte array of symmetric key 
		byte[] encodedSymmetricKey = Arrays.copyOfRange(mMessage, 16, 16+encodedKeyLength);
		//Get the iv
		byte[] iv = Arrays.copyOfRange(mMessage, 16+encodedKeyLength, 16+encodedKeyLength + ivLength);
		//Get the user content byte
		byte[] mUserContent = Arrays.copyOfRange(mMessage, 16+encodedKeyLength + ivLength, 16+encodedKeyLength + ivLength + messageLength);
		//Get the hash Message
		byte[] hashMessage = Arrays.copyOfRange(mMessage, 16+encodedKeyLength + ivLength + messageLength, 16+encodedKeyLength + ivLength + messageLength + hashMessageLength);
		
		//Get the private key
		privateKey = ProtocolLibrary.getPrivateKeyFromFileProvidedWithPassword("privateKey.pub", "IVPara.txt", "hao");
		
		//Get the client symmetric key by decrypt the key byte array
		Cipher decryptCipher = Cipher.getInstance("RSA");
		decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] rawKey = decryptCipher.doFinal(encodedSymmetricKey);
		//Get the iv from ivContent
		byte[] ivPara = decryptCipher.doFinal(iv);
		SecretKey symmetricKey = new SecretKeySpec(rawKey,"AES");

		//Create cipher for symmetric key
		Cipher decryptMessageCipher = Cipher.getInstance(CIPHERALGORITHM);
		decryptMessageCipher.init(Cipher.DECRYPT_MODE, symmetricKey, new IvParameterSpec(ivPara));
		//Decrypt mClient
		byte[] message = decryptMessageCipher.doFinal(mUserContent);
		
		//Get the hasValue
		byte[] decryptedHash = decryptMessageCipher.doFinal(hashMessage);
		ServerMessageStorage storage = null;
		//Check the hash value of message
		if (!Arrays.equals(ProtocolLibrary.hashMessage(message), decryptedHash))
		{
			storage = new ServerMessageStorage(null,"Error: Hash value not matched, invalid Message");
		} else
		{
			storage = new ServerMessageStorage(message,"SignUp message");
		}
		
		return storage;
	}
	
	/**
	 * THis is a replace method for decryptClientMessage. The only difference is
	 * the return type
	 * 
	 * @param mMessage
	 * @param x
	 * @return
	 * @throws Exception
	 */
	//Testing if we just get the content from the 
	public byte[] decryptClientMessage(byte[] mMessage, String x) throws Exception 
	{
		//----------------------- Get each byte array length ------------------------
		//Get the byte array of key length
		int encodedKeyLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(mMessage, 0, 4));
		//Get the byte array of iv length
		int ivLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(mMessage, 4, 8));
		//Get the byte array of cipher message length
		int messageLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(mMessage, 8, 12));
		//Get the hash message length
		int hashMessageLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(mMessage, 12, 16));
		
		//----------------------- Get the actual byte array of content in encrypted format
		//Get the byte array of symmetric key 
		byte[] encodedSymmetricKey = Arrays.copyOfRange(mMessage, 16, 16+encodedKeyLength);
		//Get the iv
		byte[] iv = Arrays.copyOfRange(mMessage, 16+encodedKeyLength, 16+encodedKeyLength + ivLength);
		//Get the user content byte
		byte[] mUserContent = Arrays.copyOfRange(mMessage, 16+encodedKeyLength + ivLength, 16+encodedKeyLength + ivLength + messageLength);
		//Get the hash Message
		byte[] hashMessage = Arrays.copyOfRange(mMessage, 16+encodedKeyLength + ivLength + messageLength, 16+encodedKeyLength + ivLength + messageLength + hashMessageLength);
		
		//Get the private key
		privateKey = ProtocolLibrary.getPrivateKeyFromFileProvidedWithPassword("privateKey.pub", "IVPara.txt", "hao");
		
		//Get the client symmetric key by decrypt the key byte array
		Cipher decryptCipher = Cipher.getInstance("RSA");
		decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] rawKey = decryptCipher.doFinal(encodedSymmetricKey);
		//Get the iv from ivContent
		byte[] ivPara = decryptCipher.doFinal(iv);
		SecretKey symmetricKey = new SecretKeySpec(rawKey,"AES");

		//Create cipher for symmetric key
		Cipher decryptMessageCipher = Cipher.getInstance(CIPHERALGORITHM);
		decryptMessageCipher.init(Cipher.DECRYPT_MODE, symmetricKey, new IvParameterSpec(ivPara));
		//Decrypt mClient
		byte[] message = decryptMessageCipher.doFinal(mUserContent);
		
		//Get the hasValue
		byte[] decryptedHash = decryptMessageCipher.doFinal(hashMessage);
		//Check the hash value of message
		if (!Arrays.equals(ProtocolLibrary.hashMessage(message), decryptedHash))
		{
			throw new Exception("The hashvalue of the decrypted message is different than the provided hashValue");
		} 
		
		return message;
	}
	
	public void writeClientInformation(ServerMessageStorage storage, String directory, String password) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, IOException
	{
		if (storage.getContent() != null || !storage.getPurpose().equalsIgnoreCase("Error: Hash value not matched, invalid Message"))
		{
			SaveClientInformation.writeFile(directory, new String(storage.getContent()), password);
		}
	}
	
	public void writeClientInformation(String userName, byte[] objectByteArray, String directory, String password) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, IOException
	{
		SaveClientInformation.writeFile(directory, userName, objectByteArray, password);
	}
	
	public void updateClientInformationForNewPassword(String userName, String directory, byte[] objectByteArray, String password) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, IOException
	{
		SaveClientInformation.updateFile(directory, userName, objectByteArray, password);
	}
}
