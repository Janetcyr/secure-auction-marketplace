package Protocol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Protocol.ProtocolLibrary;



public class RSAOneTimeAsymmetricKeyGenerator {

	private Key publicKey = null;
	private Key privateKey = null;
	private static final String CIPHERALGORITHM = "RSA";
	
	public RSAOneTimeAsymmetricKeyGenerator() throws NoSuchAlgorithmException
	{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.genKeyPair();
		publicKey = kp.getPublic();
		privateKey = kp.getPrivate();
	}

	@Deprecated
	protected void generateAsymmetricKeyPair() throws NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.genKeyPair();
		publicKey = kp.getPublic();
		privateKey = kp.getPrivate();
	}
	/**
	 * 
	 * @throws Exception
	 */
	protected void writePublicKey() throws Exception {
		if (publicKey == null)
			throw new Exception("RSA key does not generated successfully in constructor");
		try{
			
			FileOutputStream output = new FileOutputStream(new File("publicKey.pub"));
			//Based on the description the Encoded Method is X509. In order to decrypt an encoded key
			//X509EncodedKeySpec kspec = new X509EncodedKeySpec(encodedKey);
			//System.out.println("Public key generator public key hash value:"+ publicKey.hashCode());
			output.write(publicKey.getEncoded());
			output.close();
		} catch (IOException e)
		{
			throw new Exception("Write public Key into file failed");
		}
	}
	
	protected void writePrivateKey() throws Exception{
		if (privateKey == null)
		{
			throw new Exception("RSA key does not generated successfully in constructor");
		}
		
		PrivateKeyProtectorPBEKey privateKeyProtector = new PrivateKeyProtectorPBEKey("hao".toCharArray());
		PBEStorage privateKeyStorage = privateKeyProtector.getCipher(privateKey.getEncoded());
		
		
		try
		{
			//System.out.println("Private key generator hash value:"+ privateKey.hashCode());
			FileOutputStream output = new FileOutputStream(new File("privateKey.pub"));
			FileOutputStream outputIV = new FileOutputStream(new File("IVPara.txt"));
			if (privateKeyStorage == null)
			{
				throw new Exception("Unable to encrypt the private Key");
			}
			output.write(privateKeyStorage.getCiphertext());
			outputIV.write(privateKeyStorage.getIV());
			output.close();
			outputIV.close();
		} catch(IOException e)
		{
			e.getStackTrace();
		}
	}
	

	public static void main(String args[]) throws Exception {
		RSAOneTimeAsymmetricKeyGenerator testPublic = new RSAOneTimeAsymmetricKeyGenerator();
		testPublic.writePublicKey();
		testPublic.writePrivateKey();	
		
		String testingMessage = "My name is Hao";
		//For public key part
		PublicKey pubKey = ProtocolLibrary.readPublicKeyFromFile("publicKey.pub");
		System.out.println("Public key reading from file hash value:"+pubKey.hashCode());
		
		//For private key part
		byte[] readByte = ProtocolLibrary.readByteFromFile("privateKey.pub");
		byte[] ivParam = ProtocolLibrary.readByteFromFile("IVPara.txt");
		PrivateKeyProtectorPBEKey privateKeyProtector = new PrivateKeyProtectorPBEKey("hao".toCharArray());
		byte[] encodedkey = privateKeyProtector.getPlainTest(readByte,ivParam);

		PrivateKey privateKey = ProtocolLibrary.decodePrivateKey(encodedkey);
		System.out.println("Private key reading from file hash value:"+privateKey.hashCode());	
		
		Cipher encryptCipher = Cipher.getInstance(CIPHERALGORITHM);
		encryptCipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] encrypted = encryptCipher.doFinal(testingMessage.getBytes());
		//byte[] encryptedKey = encryptCipher.doFinal(privateKey.getEncoded());
		
		Cipher deCipher = Cipher.getInstance(CIPHERALGORITHM);
		deCipher.init(Cipher.DECRYPT_MODE, pubKey);
		String result = new String(deCipher.doFinal(encrypted));
		System.out.println(result);
	}
}
