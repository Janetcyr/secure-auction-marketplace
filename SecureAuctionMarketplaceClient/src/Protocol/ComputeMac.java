package Protocol;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class ComputeMac {

	public static byte[] hashMessage(byte[] message)
			throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		messageDigest.update(message);
		return messageDigest.digest();
	}

	public static byte[] GenerateMessageAuthenticationCode(String message,
			SecretKey key) {
		try {

			// get a key generator for the HMAC-MD5 keyed-hashing algorithm

			// KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");

			// generate a key from the generator

			// SecretKey key = keyGen.generateKey();

			// create a MAC and initialize with the above key
			// KeyGenerator keygen = KeyGenerator.getInstance("AES");
			// SecureRandom random = new SecureRandom();
			// keygen.init(128,random);
			// SecretKey skey = keygen.generateKey();

			Mac mac = Mac.getInstance("HmacSHA256");

			mac.init(key);

			// get the string as UTF-8 bytes

			byte[] b = message.getBytes("UTF-8");

			// create a digest from the byte array

			byte[] digest = mac.doFinal(b);

			return digest;

		}

		catch (NoSuchAlgorithmException e) {

			System.out.println("No Such Algorithm:" + e.getMessage());

			return null;

		}

		catch (UnsupportedEncodingException e) {

			System.out.println("Unsupported Encoding:" + e.getMessage());

			return null;

		}

		catch (InvalidKeyException e) {

			System.out.println("Invalid Key:" + e.getMessage());

			return null;

		}

	}

}
