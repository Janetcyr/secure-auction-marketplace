package Protocol;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is to generate a symmetric key to protect private key on Server
 * 
 * @author HaoL
 * 
 */
public class PrivateKeyProtectorRSAKey {
	private static char[] superUserPassword = "CLLL".toCharArray();
	private static byte[] superUserSalt = "hl692".getBytes();
	private static final int KEYLENGTH = 128;
	private static final int ITERATION = 20;
	private static final String CIPHERALGORITHM = "AES/CBC/PKCS5Padding";
	private static byte[] iv = null;
	private static SecretKey secretKey = null;

	byte[] getCipher(byte[] privateKey) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeySpecException,
			InvalidKeyException, InvalidParameterSpecException,
			IllegalBlockSizeException, BadPaddingException,
			UnsupportedEncodingException {
		Cipher encryptCipher = Cipher.getInstance(CIPHERALGORITHM);
		if (secretKey == null) {
			generateKeyBasedOnSuperUserPassword();
		}
		encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
		AlgorithmParameters params = encryptCipher.getParameters();

		if (iv == null) {
			iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		}
		return encryptCipher.doFinal(privateKey);
	}

	//The f
	public byte[] getPlainTest(byte[] cipherText) throws Exception {
		Cipher decryptCipher = Cipher.getInstance(CIPHERALGORITHM);
		if (secretKey == null && iv == null) {
			getCipher(cipherText);
		}
		decryptCipher.init(Cipher.DECRYPT_MODE, secretKey,
					new IvParameterSpec(iv));

		return decryptCipher.doFinal(cipherText);
	}

	private void generateKeyBasedOnSuperUserPassword()
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		SecretKeyFactory factory = SecretKeyFactory
				.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec pbekeySpec = new PBEKeySpec(superUserPassword,
				superUserSalt, ITERATION, KEYLENGTH);
		SecretKey tmp = factory.generateSecret(pbekeySpec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		setSecretKey(secret);
	}

	private void setSecretKey(SecretKey theSecret) {
		secretKey = theSecret;
	}

	protected SecretKey getSecretKey() throws InvalidKeySpecException,
			NoSuchAlgorithmException {
		if (secretKey == null) {
			generateKeyBasedOnSuperUserPassword();
			return secretKey;
		}
		return secretKey;
	}
	
	public static void main(String argsp[]) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, Exception
	{
		PrivateKeyProtectorRSAKey testProtector = new PrivateKeyProtectorRSAKey();
		String message = "Testing String written by Hao";
		byte[] messageByte = message.getBytes();
		System.out.println("This is the hashCode:"+ testProtector.getSecretKey().hashCode());
		System.out.println("Here is the encrypted and decrypted one:"+ 
				new String(testProtector.getPlainTest(testProtector.getCipher(messageByte))));
	}

}
