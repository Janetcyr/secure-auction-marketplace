package Protocol;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class ProtocolLibrary {
	/**
	 * Convert an object to byte Array
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
		o.writeObject(o);
		return b.toByteArray();
	}
	
	/**
	 * Generate an object from byte Array
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }
	
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] readByteFromFile(String filename) throws IOException
	{
		RandomAccessFile p = new RandomAccessFile(filename,"r");
		byte[] a = new byte[(int)p.length()];
		p.read(a);
		return a;
	}
	
	/**
	 * 
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey readPublicKeyFromFile(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		RandomAccessFile f = new RandomAccessFile(filename,"r");
		byte[] b = new byte[(int)f.length()];
		f.read(b);
		
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(b);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PublicKey pubKey = factory.generatePublic(pubKeySpec);
		return pubKey;
	}
	
	public static PrivateKey decodePrivateKey(byte[] keybyte) throws Exception
	{
		PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(keybyte);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PrivateKey privkey = factory.generatePrivate(privateSpec);
		return privkey;
	}
	
	/**
	 * Combine 8 bite array to one byte array
	 * 
	 * @param array1
	 * @param array2
	 * @param array3
	 * @param array4
	 * @param array5
	 * @param array6
	 * @param array7
	 * @param array8
	 * @return
	 */
	public static final byte[] combineByteArray(byte[] array1, byte[] array2,
			byte[] array3, byte[] array4, byte[] array5, byte[] array6, byte[] array7, byte[] array8) {
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
		return combined;
	}
	
	/**
	 * Convert an int to byte array
	 */
	public static final byte[] intToByteArray(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	/**
	 * Get the hash value of a message
	 * 
	 * @param message
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] hashMessage(byte[] message) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		messageDigest.update(message);
		return messageDigest.digest();
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static int fromByteArrayToInt(byte[] bytes)
	{
		return ByteBuffer.wrap(bytes).getInt();
	}
	
	/**
	 * Get provided key from file with provided password
	 * 
	 * @param filename1
	 * @param filename2
	 * @param password
	 * @return
	 * @throws Exception
	 */
	
	/*
	public static PrivateKey getPrivateKeyFromFileProvidedWithPassword(String filename1, String filename2, String password) throws Exception
	{
		byte[] readByte = ProtocolLibrary.readByteFromFile(filename1);
		byte[] ivParam = ProtocolLibrary.readByteFromFile(filename2);
		
		//PrivateKeyProtectorPBEKey privateKeyProtector = new PrivateKeyProtectorPBEKey(password.toCharArray());
		byte[] encodedkey = privateKeyProtector.getPlainTest(readByte, ivParam);
		
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(encodedkey);
		PrivateKey privateKey = factory.generatePrivate(privateSpec);
		return privateKey;
	}*/
	@Deprecated
	public static byte[] readByteFromInputSream(ObjectInputStream stream) throws IOException
	{
		int nRead;
		byte[] data = new byte[Integer.MAX_VALUE];
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		while((nRead = stream.read(data, 0, data.length)) != -1)
		{
			buffer.write(data, 0, nRead);
		}
		
		buffer.flush();
		return buffer.toByteArray();
	}
	
	public static byte[] objectToBytes(Object object) throws IOException {
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      ObjectOutputStream os = new ObjectOutputStream(baos);
	      os.writeObject(object);
	      return baos.toByteArray();
	  }
	
	public static final byte[] combineByteArray4(byte[] array1, byte[] array2,
			byte[] array3, byte[] array4) {
		byte[] combined = new byte[array1.length + array2.length
				+ array3.length + array4.length ];
		System.arraycopy(array1, 0, combined, 0, array1.length);
		System.arraycopy(array2, 0, combined, array1.length, array2.length);
		System.arraycopy(array3, 0, combined, array1.length + array2.length,
				array3.length);
		System.arraycopy(array4, 0, combined, array1.length + array2.length
				+ array3.length, array4.length);
		return combined;
	}
}
