package Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

//CIPHER / GENERATORS
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;

//KEY SPECIFICATIONS
import java.security.spec.KeySpec;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEParameterSpec;

//EXCEPTIONS
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.lang.reflect.Array;

public class ServerCommunicationProtocol {

	Cipher ecipher;
	Cipher dcipher;
	private SecretKey sessionKey;
	private byte[] ivParameter;
	public int n = 0;

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
	public ServerCommunicationProtocol()
			throws InvalidAlgorithmParameterException {
		try {
			// Obtain a cipher engine that can perform encryption and decryption
			// by implementing the AES/CBC/PKCS5Padding algorithm.
			ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// IvParameterSpec ivSpec=new IvParameterSpec(ivParameter);

			// Initialize the cipher to encrypt or decrypt data.
			// ecipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			// dcipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

		} catch (NoSuchPaddingException e) {
			System.out.println("EXCEPTION: NoSuchPaddingException");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("EXCEPTION: NoSuchAlgorithmException");
		}
	}

	public byte[] encryptMessage(String respondContent)
			throws UnsupportedEncodingException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		// EncryptMessageType encryptMsg= new EncryptMessageType();

		// Covert the sendMessage from String to byte Array
		byte[] respondContentByte = respondContent.getBytes("UTF8");

		int respondNumber = n;
		byte[] respondNumberByte = ProtocolLibrary
				.intToByteArray(respondNumber);

		// combine username, requestContent, n to a whole byteArray
		byte[] wholeRespondMsgByte = this.combine2ByteArray(respondContentByte,
				respondNumberByte);

		// Encrypt or decrypt the data in the input array as well as any data
		// that has been previously buffered in the cipher engine.
		// IvParameterSpec ivSpec=new IvParameterSpec(this.ivParameter);

		byte[] encWholeRespondMsg = ecipher.doFinal(wholeRespondMsgByte);

		// hash the sendMessage
		byte[] hashedWholeRespondMsg = ProtocolLibrary
				.hashMessage(wholeRespondMsgByte);

		// length
		byte[] encWholeRespondMsgLength = ProtocolLibrary
				.intToByteArray(encWholeRespondMsg.length);
		byte[] hashedWholeRespondMsgLength = ProtocolLibrary
				.intToByteArray(hashedWholeRespondMsg.length);
		byte[] respondContentLength = ProtocolLibrary
				.intToByteArray(respondContentByte.length);
		byte[] respondNumberLength = ProtocolLibrary
				.intToByteArray(respondNumberByte.length);

		return this.combine6ByteArray(encWholeRespondMsgLength,
				hashedWholeRespondMsgLength, respondContentLength,
				respondNumberLength, encWholeRespondMsg, hashedWholeRespondMsg);

	}

	public String decryptMessage(byte[] serverReceivedMsg)
			throws UnsupportedEncodingException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		// ----------------------- Get each byte array length
		// ------------------------
		// byte[] serverReceivedMsg=serverReceivedMsgString.getBytes("UTF8");
		// Get the byte array of username length
		int usernameLength = ProtocolLibrary.fromByteArrayToInt(Arrays
				.copyOfRange(serverReceivedMsg, 0, 4));
		// Get the byte array of encWholeMsg length
		int encWholeMsgLength = ProtocolLibrary.fromByteArrayToInt(Arrays
				.copyOfRange(serverReceivedMsg, 4, 8));
		// Get the byte array of hashedWholeMsg length
		int hashedWholeMsgLength = ProtocolLibrary.fromByteArrayToInt(Arrays
				.copyOfRange(serverReceivedMsg, 8, 12));
		// Get the requestContent length
		int requestContentLength = ProtocolLibrary.fromByteArrayToInt(Arrays
				.copyOfRange(serverReceivedMsg, 12, 16));
		// Get the run number length
		int runNumberLength = ProtocolLibrary.fromByteArrayToInt(Arrays
				.copyOfRange(serverReceivedMsg, 16, 20));

		// Get username plaintext
		byte[] username = Arrays.copyOfRange(serverReceivedMsg, 20,
				20 + usernameLength);

		// LoginProtocolServer.numbers

		if (LoginProtocolServer.numbers
				.containsKey(new String(username, "UTF8"))) {
			KeyExpiretime keyExtime = LoginProtocolServer.numbers
					.get(new String(username, "UTF8"));
			Calendar currentTime = Calendar.getInstance();
			if (keyExtime.compareto(currentTime)) {
				this.sessionKey = keyExtime.getSessionKey();
				this.ivParameter = keyExtime.getIvParameter();
				IvParameterSpec ivSpec = new IvParameterSpec(this.ivParameter);
				dcipher.init(Cipher.DECRYPT_MODE, this.sessionKey, ivSpec);
				ecipher.init(Cipher.ENCRYPT_MODE, this.sessionKey, ivSpec);
				// Get encWholeMsg
				byte[] encWholeMsg = Arrays.copyOfRange(serverReceivedMsg,
						20 + usernameLength, 20 + usernameLength
								+ encWholeMsgLength);

				// Get hashedWholeMsg
				byte[] hashedWholeMsg = Arrays.copyOfRange(serverReceivedMsg,
						20 + usernameLength + encWholeMsgLength, 20
								+ usernameLength + encWholeMsgLength
								+ hashedWholeMsgLength);

				// Decrypt the whole message
				byte[] plaintextWholeMsg = dcipher.doFinal(encWholeMsg);

				// hash the sendMessage
				byte[] serverHashedWholeMsg = ProtocolLibrary
						.hashMessage(plaintextWholeMsg);

				String stringHashedMsg = new String(hashedWholeMsg, "UTF8");
				String stringServerComputeHashedMsg = new String(
						serverHashedWholeMsg, "UTF8");

				// Decode using utf-8
				// String decrymsg=new String(utf8, "UTF8");

				// compute the hash of the decrypted message
				// byte[] computeHashedMsg=this.hashMessage(decrymsg);

				if (stringHashedMsg.equals(stringServerComputeHashedMsg)) {

					if (n <= ProtocolLibrary.fromByteArrayToInt(Arrays
							.copyOfRange(plaintextWholeMsg, usernameLength
									+ requestContentLength, usernameLength
									+ requestContentLength + runNumberLength))) {
						byte[] requestContent = Arrays.copyOfRange(
								plaintextWholeMsg, usernameLength,
								usernameLength + requestContentLength);
						String usernameString = new String(username, "UTF8");
						String requestContentString = new String(
								requestContent, "UTF8");
						n = n + 1;
						return requestContentString;
					} else
						return "ERROR: THE RUN NUMBER IS WRONG ";

				} else {
					return "ERROR: THE HASH IS WRONG ";
				}

			} else
				return "ERROR: THE SESSION KEY HAS EXPIRED. ";

		} else
			return "ERROR: CAN'T FIND THIS USER' SESSION KEY";

		// compare the hash of receivedMsg to the hash in the message to check
		// the intergrity of the message
		// String stringHashedMsg=new String(hashedMsg,"UTF8");
		// String stringComputeHashedMsg=new String(computeHashedMsg,"UTF8");

	}

	public byte[] combine2ByteArray(byte[] array1, byte[] array2) {
		byte[] combined = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, combined, 0, array1.length);
		System.arraycopy(array2, 0, combined, array1.length, array2.length);

		return combined;
	}

	public byte[] combine6ByteArray(byte[] array1, byte[] array2,
			byte[] array3, byte[] array4, byte[] array5, byte[] array6) {
		byte[] combined = new byte[array1.length + array2.length
				+ array3.length + array4.length + array5.length + array6.length];
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

		return combined;
	}

	// This function is used to compute the hash of message
	public byte[] hashMessage(String MessageToHash)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		messageDigest.update(MessageToHash.getBytes("UTF8"));
		byte[] MessageHashed = messageDigest.digest();
		//
		return MessageHashed;
	}

}
