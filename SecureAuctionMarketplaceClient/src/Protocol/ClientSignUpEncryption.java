package Protocol;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * 
 * 
 * @author HaoL
 *
 */
public class ClientSignUpEncryption {
	private static final int KEYLENGTH = 128;
	private static final int ITERATION = 20;
	private static final String CIPHERALGORITHM = "AES/CBC/PKCS5Padding";
	private Key serverPublicKey = null;
	private SecretKey secretkey = null;

	/**
	 * Generate a key based on password provided by user
	 * 
	 * @param password
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	public ClientSignUpEncryption(String password)
			throws InvalidKeySpecException, NoSuchAlgorithmException {

		// generate salt
		Random r = new SecureRandom();
		byte[] salt = new byte[20];
		r.nextBytes(salt);

		SecretKeyFactory factory = SecretKeyFactory
				.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec pbekeySpec = new PBEKeySpec(password.toCharArray(), salt,
				ITERATION, KEYLENGTH);
		SecretKey tmp = factory.generateSecret(pbekeySpec);
		secretkey = new SecretKeySpec(tmp.getEncoded(), "AES");
	}
	/**
	 * Encrypt client information for sign up process
	 * 
	 * @param mClient
	 * 			The content gets from clients, presented in byte array. It contains username, password, pictId, etc.
	 * @param password
	 * 			A random password to generate a symmetric key if necessary
	 * @return
	 * @throws Exception
	 */
	public byte[] encryptUserSignupMessage(byte[] mClient)
			throws Exception {
		//Generate a symmetric key
		if (secretkey == null)
			throw new Exception("Could not generate password based secret key");
		
		//Get the publicKey
		if (serverPublicKey == null) {
			serverPublicKey = ProtocolLibrary.readPublicKeyFromFile("publicKey.pub");
		}
		
		//Create a cipher for symmetric key
		Cipher symmetricKeyEncryptCipher = Cipher.getInstance(CIPHERALGORITHM);
		symmetricKeyEncryptCipher.init(Cipher.ENCRYPT_MODE, secretkey);
		
		//Create cipher for public key
		Cipher publicKeyCipher = Cipher.getInstance("RSA");
		publicKeyCipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
		
		//Get the random iv from cipher and encrypt it with symmetric key
		byte[] iv = publicKeyCipher.doFinal(symmetricKeyEncryptCipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV());
		//Get the total length of cipher text of iv
		byte[] ivLength = ProtocolLibrary.intToByteArray(iv.length);
		
		//Use the symmetric key to encrypt client information
		byte[] encodedClientInformation = symmetricKeyEncryptCipher.doFinal(mClient);
		// Client information byte array Length
		byte[] messageLength = ProtocolLibrary.intToByteArray(encodedClientInformation.length);
		
		// Get the hash value of the mClient
		byte[] hashValueofEncodedInformation = symmetricKeyEncryptCipher.doFinal(ProtocolLibrary.hashMessage(mClient));
		// hashMessage length
		byte[] hashLength = ProtocolLibrary.intToByteArray(hashValueofEncodedInformation.length);

		
		//Encrypt private key with public key
		byte[] encodedPrivateKey = publicKeyCipher.doFinal(secretkey
				.getEncoded());
		//Get the encrypted keyLength
		byte[] encodedKeyLength = ProtocolLibrary.intToByteArray(encodedPrivateKey.length);

		return ProtocolLibrary.combineByteArray(encodedKeyLength,ivLength,
				messageLength, hashLength, encodedPrivateKey, iv,
				encodedClientInformation, hashValueofEncodedInformation);

	}
}
