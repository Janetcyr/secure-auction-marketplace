package Protocol;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//CIPHER / GENERATORS
import javax.crypto.SecretKey;

//KEY SPECIFICATIONS

import javax.crypto.spec.IvParameterSpec;

//EXCEPTIONS
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import java.util.Calendar;
import java.io.UnsupportedEncodingException;

public class CommunicationProtocol {

	Cipher ecipher;
	Cipher dcipher;
	int n=0;

	/**
	 * Constructor used to create this object. Responsible for setting and
	 * initializing this object's encrypter and decrypter Chipher instances
	 * given a Secret Key and algorithm.
	 * 
	 * @param key
	 *            Secret Key used to initialize both the encrypter and decrypter
	 *            instances.
	 * @param algorithm
	 *            Which algorithm to use for creating the encrypter and
	 *            decrypter instances.
	 * @throws InvalidAlgorithmParameterException
	 */
	public CommunicationProtocol(SecretKey key, byte[] ivParameter)
			throws InvalidAlgorithmParameterException {
		try {
			// Obtain a cipher engine that can perform encryption and decryption
			// by implementing the AES/CBC/PKCS5Padding algorithm.
			ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// Use ivParameter byte array to get IvParameterSpec

			IvParameterSpec ivSpec = new IvParameterSpec(ivParameter);

			// Initialize the cipher to encrypt or decrypt data.
			ecipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

		} catch (NoSuchPaddingException e) {
			System.out.println("EXCEPTION: NoSuchPaddingException");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("EXCEPTION: NoSuchAlgorithmException");
		} catch (InvalidKeyException e) {
			System.out.println("EXCEPTION: InvalidKeyException");
		}
	}

	public byte[] encryptMessage(String username, String requestContent)
			throws UnsupportedEncodingException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException {
		n=n+1;
		
		//check if username or requestcontent is invalid
		if(requestContent == null||username == null) return null;
		
		// Covert the sendMessage from String to byte Array
		byte[] usernameByte = username.getBytes("UTF8");
		byte[] requestContentByte = requestContent.getBytes("UTF8");
		byte[] runNumberByte= ProtocolLibrary.intToByteArray(n);

		
		//combine username, requestContent, n to a whole byteArray
		byte[] wholeMsgByte=this.combine3ByteArray(usernameByte, requestContentByte, runNumberByte);
		
		
		// Encrypt or decrypt the data in the input array as well as any data
		// that has been previously buffered in the cipher engine.
		byte[] encWholeMsgByte = ecipher.doFinal(wholeMsgByte);
		
		// hash the sendMessage
		byte[] hashedWholeMsgByte = ProtocolLibrary.hashMessage(wholeMsgByte);
		
		//length
		byte[] usernameByteLength=ProtocolLibrary.intToByteArray(usernameByte.length);
		byte[] encWholeMsgLength=ProtocolLibrary.intToByteArray(encWholeMsgByte.length);
		byte[] hashedWholeMsgByteLength=ProtocolLibrary.intToByteArray(hashedWholeMsgByte.length);
		byte[] requestContentByteLength=ProtocolLibrary.intToByteArray(requestContentByte.length);
		byte[] numberByteLength=ProtocolLibrary.intToByteArray(runNumberByte.length);
		
		byte[] returnMsg=ProtocolLibrary.combineByteArray(usernameByteLength, encWholeMsgLength, hashedWholeMsgByteLength, requestContentByteLength, numberByteLength, usernameByte, encWholeMsgByte, hashedWholeMsgByte);
		//System.out.println(new String(returnMsg, "UTF8"));
		return (returnMsg);
	}

	public String decryptMessage(byte[] ClientReceivedMsg )
			throws UnsupportedEncodingException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException {
		
		 //----------------------- Get each byte array length ------------------------
	     //Get the byte array of username length
	     int encWholeMsgLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(ClientReceivedMsg, 0, 4));
	     //Get the byte array of encWholeMsg length
	     int hashedWholeMsgLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(ClientReceivedMsg, 4, 8));
	     //Get the byte array of hashedWholeMsg length
	     int respondContentLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(ClientReceivedMsg, 8, 12));
	     //Get the requestContent length
	     int respondNumberLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(ClientReceivedMsg, 12, 16));
	    
		  //Get encWholeMsg
		  byte[] encWholeMsg=Arrays.copyOfRange(ClientReceivedMsg, 16, 16+encWholeMsgLength );
				
		  //Get hashedWholeMsg
		  byte[] hashedWholeMsg=Arrays.copyOfRange(ClientReceivedMsg, 16+encWholeMsgLength, 16+encWholeMsgLength+hashedWholeMsgLength );
				
		  // Decrypt the whole message
	      byte[] plaintextWholeMsg = dcipher.doFinal(encWholeMsg);
	            
		  // hash the sendMessage
		  byte[] clientHashedWholeMsg = ProtocolLibrary.hashMessage(plaintextWholeMsg);
		
            	
          String stringHashedMsg = new String(hashedWholeMsg, "UTF8");
          String stringClientComputeHashedMsg = new String(clientHashedWholeMsg, "UTF8");

	      // Decode using utf-8
	      //String decrymsg=new String(utf8, "UTF8");
	            
	      //compute the hash of the decrypted message
	      //byte[] computeHashedMsg=this.hashMessage(decrymsg);
	            
	      if(stringHashedMsg.equals(stringClientComputeHashedMsg)){
	         if(n==ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(plaintextWholeMsg, respondContentLength, respondContentLength+respondNumberLength))){
	             byte[] respondContent=Arrays.copyOfRange(plaintextWholeMsg,0,respondContentLength);
			     String respondContentString= new String(respondContent, "UTF8");
		         return respondContentString;
	            	}
	            	else
	            		//System.out.println("runnumber");
	            		return null;
	            	
	            }
	            else
	            {
	            	//System.out.println("hashvalue");
	            	return null;
	            }
	}

	public byte[] combine3ByteArray(byte[] array1, byte[] array2,
			byte[] array3) {
		byte[] combined = new byte[array1.length + array2.length
				+ array3.length];
		System.arraycopy(array1, 0, combined, 0, array1.length);
		System.arraycopy(array2, 0, combined, array1.length, array2.length);
		System.arraycopy(array3, 0, combined, array1.length + array2.length,
				array3.length);
		return combined;
	}
	
	public byte[] combine9ByteArray(byte[] array1, byte[] array2,
			byte[] array3, byte[] array4, byte[] array5, byte[] array6, byte[] array7, byte[] array8, byte[] array9) {
		byte[] combined = new byte[array1.length + array2.length
				+ array3.length + array4.length + array5.length + array6.length + array7.length + array8.length];
		System.arraycopy(array1, 0, combined, 0, array1.length);
		System.arraycopy(array2, 0, combined, array1.length, array2.length);
		System.arraycopy(array3, 0, combined, array1.length + array2.length,
				array3.length);
		System.arraycopy(array4, 0, combined, array1.length + array2.length
				+ array3.length, array4.length);
		System.arraycopy(array5, 0, combined, array1.length + array2.length
				+ array3.length + array4.length, array5.length);
		System.arraycopy(array6, 0, combined, array1.length + array2.length
				+ array3.length + array4.length + array5.length, array6.length);
		System.arraycopy(array7, 0, combined, array1.length + array2.length
				+ array3.length + array4.length + array5.length + array6.length, array7.length);
		System.arraycopy(array8, 0, combined, array1.length + array2.length
				+ array3.length + array4.length + array5.length + array6.length + array7.length, array8.length);
		System.arraycopy(array9, 0, combined, array1.length + array2.length
				+ array3.length + array4.length + array5.length + array6.length + array7.length+array8.length, array9.length);
		return combined;
	}

}