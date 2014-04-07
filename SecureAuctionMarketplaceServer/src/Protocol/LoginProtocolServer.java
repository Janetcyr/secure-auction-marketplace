package Protocol;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class LoginProtocolServer {

	/**
	 * @param args
	 * @author Yurun(Janet)
	 * @exception
	 */
	
	// serverside will have serverSharedKey parameter
	private SecretKey serverSharedKey = null;
	private byte[] ivP1 = null;
	
	//RSA parameter
	private PrivateKey private_Key = null;
	
	//session key parameter;
	private SecretKey serverSession_Key = null;
	private  Calendar expireTime; 
	private byte[] ivSessionKey = null;
	public static Map<String,KeyExpiretime> numbers;
	
	//constructor
	public LoginProtocolServer() {
		numbers = new HashMap<String,KeyExpiretime> ();
	}
	
	//create sessionKey Expiretime
	public Calendar createKeyExpiretime() {
		this.expireTime=Calendar.getInstance();
		this.expireTime.add(Calendar.MINUTE, 10);
		return this.expireTime;

	}

	
	// decrypted message received using serverSharedKey;
	public byte[] sharedKeyDecryption(byte[] data) throws Exception {
		Cipher cipher1 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivSpec1 = new IvParameterSpec(this.ivP1);
		cipher1.init(Cipher.DECRYPT_MODE, this.serverSharedKey, ivSpec1);
		String tmp = new String(data, "UTF-8");
		byte[] decrypteddata = cipher1.doFinal(data);
		return decrypteddata;
	}
	
	
	// decrypt the encrypted sharedkey using PrivateKey;
	public SecretKey privateKeyDecryption(byte[] data) throws Exception {
		if (this.private_Key == null) {
			this.private_Key = ProtocolLibrary
					.getPrivateKeyFromFileProvidedWithPassword(
							"privateKey.pub", "IVPara.txt", "hao");
		}
		Cipher decryptKeyCipher = Cipher.getInstance("RSA");
		decryptKeyCipher.init(Cipher.DECRYPT_MODE, this.private_Key);
		byte[] decryptedSharedKey = decryptKeyCipher.doFinal(data);
		SecretKey aKeySpec = new SecretKeySpec(decryptedSharedKey, "AES");
		this.serverSharedKey = aKeySpec;
		return serverSharedKey;
	}

	
	// decrypt the encrypted iv parameter using PrivateKey
	public byte[] privateKeyDecryptionIV(byte[] data) throws Exception {
		if (this.private_Key == null) {
			this.private_Key = ProtocolLibrary
					.getPrivateKeyFromFileProvidedWithPassword(
							"privateKey.pub", "IVPara.txt", "hao");
		}
		Cipher decryptIVCipher = Cipher.getInstance("RSA");
		decryptIVCipher.init(Cipher.DECRYPT_MODE, this.private_Key);
		byte[] decryptedIV = decryptIVCipher.doFinal(data);
		return decryptedIV;
	}

	
	// compute hashvalue using decrypted message
	public byte[] hashValueOfReceivedData(byte[] dataDecrypted)
			throws Exception {
		MessageDigest hashValue = MessageDigest.getInstance("SHA-512");
		hashValue.update(dataDecrypted);
		byte[] hashedReceived = hashValue.digest();
		return hashedReceived;
	}

	
	// check if username exist in file
	public String[] checkDataInFile() throws IOException {
		return new String(ProtocolLibrary.readByteFromFile("dataInServer.txt"))
				.split("/");
	}

	
	// generate a session_key;
	private SecretKey sessionKeyGenerater() throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		// generate a AES key
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		SecureRandom random = new SecureRandom();
		keygen.init(128, random);
		SecretKey skey = keygen.generateKey();
		
		Cipher cipher4 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		if (this.ivSessionKey == null){
			int blksz = cipher4.getBlockSize();
			byte[] ivBytes = new byte[blksz];
			SecureRandom random2 = new SecureRandom();
	        random2.nextBytes(ivBytes);
			this.ivSessionKey=ivBytes;
		}
		
		return skey;
	}

	
	// encrypt response data from server using serverSharedKey;
	public byte[] sharedKeyEncryptionServer(String data) throws Exception {
		Cipher cipher3 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivSpecServer = new IvParameterSpec(this.ivP1);
		cipher3.init(Cipher.ENCRYPT_MODE, this.serverSharedKey, ivSpecServer);
		byte[] ciphertext = cipher3.doFinal(data.getBytes("UTF8"));
		return ciphertext;
	}
	
	
	//encrypt session_key using sharedKey
	public byte[] sharedKeyEncryptionServerKey(SecretKey data) throws Exception {
		Cipher cipher3 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivSpecServer = new IvParameterSpec(this.ivP1);
		cipher3.init(Cipher.ENCRYPT_MODE, this.serverSharedKey, ivSpecServer);
		byte[] ciphertext = cipher3.doFinal(data.getEncoded());
		return ciphertext;
	}
	
	
	//encrypt session_iv using sharedKey
	public byte[] sharedKeyEncryptionServerIV(byte[] data) throws Exception {
		Cipher cipher3 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivSpecServer = new IvParameterSpec(this.ivP1);
		cipher3.init(Cipher.ENCRYPT_MODE, this.serverSharedKey, ivSpecServer);
		byte[] ciphertext = cipher3.doFinal(data);
		return ciphertext;
	}

	
	// Server decrypted message from client and check if username exist
	public String firstStepServer(byte[] dataFromClient) throws Exception {
		byte[] dataFromClient1 = dataFromClient;
		int KeyLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				dataFromClient1, 0, 4));
		int messageLength = ProtocolLibrary.fromByteArrayToInt(Arrays
				.copyOfRange(dataFromClient1, 4, 8));
		int hashLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				dataFromClient1, 8, 12));
		int ivLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				dataFromClient1, 12, 16));

		byte[] encryptedKey = Arrays.copyOfRange(dataFromClient1, 16,
				16 + KeyLength);
		byte[] encryptedMessage = Arrays.copyOfRange(dataFromClient1,
				16 + KeyLength, 16 + KeyLength + messageLength);
		byte[] encryptedHashValue = Arrays.copyOfRange(dataFromClient1, 16
				+ KeyLength + messageLength, 16 + KeyLength + messageLength
				+ hashLength);
		byte[] encryptediv = Arrays.copyOfRange(dataFromClient1, 16 + KeyLength
				+ messageLength + hashLength, 16 + KeyLength + messageLength
				+ hashLength + ivLength);

		this.ivP1 = this.privateKeyDecryptionIV(encryptediv);
		this.serverSharedKey = this.privateKeyDecryption(encryptedKey);
		byte[] decryptedMessage = this.sharedKeyDecryption(encryptedMessage);
		byte[] computedHash = this.hashValueOfReceivedData(decryptedMessage);
		String hashReceived = new String(encryptedHashValue, "UTF8");
		String hashComputed = new String(computedHash, "UTF8");
		if (hashReceived.equals(hashComputed)) {
			System.out.println("Pass: Integrity Ensured:Fisrt Step");
			String decryptedMessageString = new String(decryptedMessage, "UTF8");
			String[] stringReceived = decryptedMessageString.split("/");
			String usernameReceived = stringReceived[0];
			if (SaveClientInformation.isUserNameExist("usrInformation", usernameReceived)){
				System.out.println("Pass:User File Exist In System");
				String contentInFile = SaveClientInformation.getContentFromFile("usrInformation", usernameReceived, "hao");
				Object obj = SaveClientInformation.getObjectFromFile("usrInformation", usernameReceived, "hao");
				ServerReceivedRegistrationInformationStorage clientInfo = null;
				if (obj instanceof ServerReceivedRegistrationInformationStorage)
				{
					clientInfo = (ServerReceivedRegistrationInformationStorage)obj;
				} else
				{
					throw new Exception("Can't change the obj to ServerRegistrationInformationStorage");
				}
				//String[] usern = null;
				//usern = this.checkDataInFile();
				//String[] contentInFileSplit = contentInFile.split("/");
				String usernameToCheck = clientInfo.getUserName();
				if (usernameReceived.equals(usernameToCheck)) {
					System.out.println("Pass:Username Exist In File");
					System.out.println("The Username is:" + usernameToCheck);
					String newString = clientInfo.getUserName() + "/" + clientInfo.getSiteKey();
					return newString;
				} else {
					String failure = "ERROR: Username does not exist";
					System.out.println("ERROR: Username: "
							+ usernameReceived+  "Does Not Exist");
					return failure;
				}
		  }
			else {
				System.out.println("ERROR: User File Does Not Exist In System" );
				return "ERROR: User File Does Not Exist In System";
			}
		} else {
			System.out.println("ERROR: Integrity Violated: First Step");
			return "ERROR: Integrity Violated: First Step";
		}
	}

	
	//Server send site-key to client
	public byte[] secondStepServer(String dataFromServerStep1) throws Exception {
		// change data to byte[];
		if (dataFromServerStep1 == null){
			return null;
		}
		byte[] dataFromServerByte = dataFromServerStep1.getBytes("UTF8");

		// response byte[] parts
		byte[] responseMessage1 = this
				.sharedKeyEncryptionServer(dataFromServerStep1);
		byte[] responseHash1 = this.hashValueOfReceivedData(dataFromServerByte);
		byte[] responseMessageLength = ProtocolLibrary
				.intToByteArray(responseMessage1.length);
		byte[] responseHashLength = ProtocolLibrary
				.intToByteArray(responseHash1.length);
		byte[] secondStepFromServer = ProtocolLibrary.combineByteArray4(
				responseMessageLength, responseHashLength, responseMessage1,
				responseHash1);
		return secondStepFromServer;
	}
	
	
	//server check if password is correct
	public byte[] thridStepServer(byte[] dataFromClient2) throws Exception {
		if (dataFromClient2 == null){
			return null;
		}
		int messageLength2 = ProtocolLibrary.fromByteArrayToInt(Arrays
				.copyOfRange(dataFromClient2, 0, 4));
		int hashLength2 = ProtocolLibrary.fromByteArrayToInt(Arrays
				.copyOfRange(dataFromClient2, 4, 8));

		// parse byte[] from dataFromClient2
		byte[] encryptedMessage2 = Arrays.copyOfRange(dataFromClient2, 8,
				8 + messageLength2);
		byte[] encryptedHashValue2 = Arrays.copyOfRange(dataFromClient2,
				8 + messageLength2, 8 + messageLength2 + hashLength2);
		// decrypted message and compute hashValue of message
		//the decryptedMessage2 is the Username/password
		byte[] decryptedMessage2 = this.sharedKeyDecryption(encryptedMessage2);
		byte[] computedHash2 = this.hashValueOfReceivedData(decryptedMessage2);

		// check hash value
		String hashReceived2 = new String(encryptedHashValue2, "UTF8");
		String hashComputed2 = new String(computedHash2, "UTF8");
		if (hashReceived2.equals(hashComputed2)) {
			System.out.println("Pass: Integrity Ensured: Third Step Server");
			String decryptedUsernamePassword = new String(decryptedMessage2,
					"UTF8");
			String[] usernamePass = decryptedUsernamePassword.split("/");
			String usernameReceived = usernamePass[0];
			String passwordReceived = usernamePass[1];
			//String fileContent = SaveClientInformation.getContentFromFile("usrInformation",usernameReceived, "hao");
			ServerReceivedRegistrationInformationStorage clientInfo = SaveClientInformation.getObjectFromFile("usrInformation",usernameReceived, "hao");
			
			//String[] usern = fileContent.split("/");
			//String usernameToCheckAgain = usern[0];
			//String passwordToCheck = usern[1];
			String usernameToCheckAgain = clientInfo.getUserName();
			String passwordToCheck = clientInfo.getPassword();
			byte[] enteredHashValueOfPassword = clientInfo.getEncryptedPassword(passwordReceived, clientInfo.getSalt());
			boolean isPasswordCorrect = Arrays.equals(enteredHashValueOfPassword, clientInfo.getEncryptedPassword());
			//String ToCheck = usernameToCheckAgain + "/" + passwordToCheck;
			//String decryptedUP = new String(decryptedMessage2, "UTF8");
			//if (ToCheck.equals(decryptedUP)) {
			if (isPasswordCorrect && usernameToCheckAgain.equalsIgnoreCase(usernameReceived)) {
				System.out.println("Pass:Username and Password Correct:"
						+ usernameToCheckAgain + "," + passwordToCheck);
				serverSession_Key = this.sessionKeyGenerater();
				KeyExpiretime keyExpire = new KeyExpiretime();
				keyExpire.setSessionKey(serverSession_Key);
				keyExpire.setIvParameter(ivSessionKey);
				keyExpire.setExpireTime(this.createKeyExpiretime());
				keyExpire.printExpiretime();
				numbers.put(usernameToCheckAgain,keyExpire);
				System.out.println("test1:"+numbers.get(usernameToCheckAgain)); 
				
				//encrypt session key
				byte[] encryptedSessionkey = this.sharedKeyEncryptionServerKey(serverSession_Key);
				byte[] encryptedIV = this.sharedKeyEncryptionServerIV(ivSessionKey);
				
				//encrypted session key length
				byte[] keyLength = ProtocolLibrary.intToByteArray(encryptedSessionkey.length);
				byte[] IVLength = ProtocolLibrary.intToByteArray(encryptedIV.length);
				
				byte[] sentToClientSessionKey = ProtocolLibrary.combineByteArray4(keyLength, IVLength, encryptedSessionkey, encryptedIV);
				return sentToClientSessionKey;
			}
			else{
				System.out.println("ERROR:Password Error");
				return "ERROR:Password Error".getBytes();
			}

		} 
		else { 
			System.out.println("ERROR:Integrity Violated: Third Step Server");
			return "ERROR:Integrity Violated: Third Step Server".getBytes();
		}
	}
}
