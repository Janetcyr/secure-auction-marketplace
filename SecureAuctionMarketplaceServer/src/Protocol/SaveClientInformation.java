package Protocol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;


/**
 * File look up utilities
 * 
 * @author HaoL
 *
 */
public class SaveClientInformation {
	
	private static final String CIPHERALGORITHM = "AES/CBC/PKCS5Padding";
	
	public static boolean isUserNameExist(String directoryName, String usrname)
	{
		final String filename = usrname;
		File dir = new File(directoryName);
		File[] matchingFiles = dir.listFiles(
				new FilenameFilter()
				{
					public boolean accept(File dir, String name)
					{
						return (filename.equalsIgnoreCase(name)) ? true : false;
					}
				});
		
		return (matchingFiles.length == 0) ? false: true;
	}
	
	/**
	 * Write content to a file using encrypted format
	 * 
	 * @param directory
	 * @param content
	 * @param password
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidParameterSpecException 
	 */
	public static boolean writeFile(String directory, String content, String password) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException
	{
		boolean hasExit = true;
		String[] userarray = content.split("/");
		String username = userarray[0];
		String path = directory + "/" + username;
		PrivateKeyProtectorPBEKey protector = new PrivateKeyProtectorPBEKey(password.toCharArray());
		String ivPath = directory + "/" + username + "ivParam";

		if (!SaveClientInformation.isUserNameExist(directory, username))
		{
			Cipher encryptCipher = Cipher.getInstance(CIPHERALGORITHM);
			encryptCipher.init(Cipher.ENCRYPT_MODE, protector.getKey());
			FileOutputStream output1 = new FileOutputStream(new File(ivPath));
			output1.write(encryptCipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV());
			output1.close();
			FileOutputStream output = new FileOutputStream(new File(path));
			output.write(encryptCipher.doFinal(content.getBytes()));
			output.close();
			hasExit = false;
		}
		
		
		return hasExit;
	}
	

	/**
	 * Write content to a file using encrypted format. In this method, the content is the 
	 * 
	 * @param directory
	 * @param content
	 * @param password
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidParameterSpecException 
	 */
	public static boolean writeFile(String directory, String userName, byte[] content, String password) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException
	{
		boolean hasExit = true;
		String username = userName;
		String path = directory + "/" + username;
		PrivateKeyProtectorPBEKey protector = new PrivateKeyProtectorPBEKey(password.toCharArray());
		String ivPath = directory + "/" + username + "ivParam";

		if (!SaveClientInformation.isUserNameExist(directory, username))
		{
			Cipher encryptCipher = Cipher.getInstance(CIPHERALGORITHM);
			encryptCipher.init(Cipher.ENCRYPT_MODE, protector.getKey());
			FileOutputStream output1 = new FileOutputStream(new File(ivPath));
			output1.write(encryptCipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV());
			output1.close();
			FileOutputStream output = new FileOutputStream(new File(path));
			output.write(encryptCipher.doFinal(content));
			output.close();
			hasExit = false;
		}
		
		
		return hasExit;
	}
	
	public static boolean updateFile(String directory, String userName, byte[] content, String password) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, InvalidKeyException
	{
		String username = userName;
		String path = directory + "/" + username;
		PrivateKeyProtectorPBEKey protector = new PrivateKeyProtectorPBEKey(password.toCharArray());
		String ivPath = directory + "/" + username + "ivParam";
		
		//It must exsit
		if (SaveClientInformation.isUserNameExist(directory, username))
		{
			Cipher encryptCipher = Cipher.getInstance(CIPHERALGORITHM);
			encryptCipher.init(Cipher.ENCRYPT_MODE, protector.getKey());
			FileOutputStream output1 = new FileOutputStream(new File(ivPath),false);
			output1.write(encryptCipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV());
			output1.close();
			FileOutputStream output = new FileOutputStream(new File(path),false);
			output.write(encryptCipher.doFinal(content));
			output.close();
		}
		return true;
	}
	
	
	
	public static String getContentFromFile(String directory, String username, String password) throws Exception
	{
		byte[] content;
		String path = directory + "/" + username;
		String ivPath = directory + "/" + username + "ivParam";
		if (!SaveClientInformation.isUserNameExist(directory, username))
		{
			throw new Exception("Could not find the file");
		}else
		{
			content = ProtocolLibrary.readByteFromFile(path);
		}
		
		byte[] ivParam = ProtocolLibrary.readByteFromFile(ivPath);
		PrivateKeyProtectorPBEKey protector = new PrivateKeyProtectorPBEKey(password.toCharArray());
		
		Cipher decryptCipher = Cipher.getInstance(CIPHERALGORITHM);
		decryptCipher.init(Cipher.DECRYPT_MODE, protector.getKey(), new IvParameterSpec(ivParam));
		return new String(decryptCipher.doFinal(content));
	}
	
	public static ServerReceivedRegistrationInformationStorage getObjectFromFile(String directory, String username, String password) throws Exception
	{
		byte[] content;
		String path = directory + "/" + username;
		String ivPath = directory + "/" + username + "ivParam";
		if (!SaveClientInformation.isUserNameExist(directory, username))
		{
			throw new Exception("Could not find the file");
		}else
		{
			content = ProtocolLibrary.readByteFromFile(path);
		}
		
		byte[] ivParam = ProtocolLibrary.readByteFromFile(ivPath);
		PrivateKeyProtectorPBEKey protector = new PrivateKeyProtectorPBEKey(password.toCharArray());
		
		Cipher decryptCipher = Cipher.getInstance(CIPHERALGORITHM);
		decryptCipher.init(Cipher.DECRYPT_MODE, protector.getKey(), new IvParameterSpec(ivParam));
		Object obj = ProtocolLibrary.toObject(decryptCipher.doFinal(content));
		ServerReceivedRegistrationInformationStorage clientInfo = null;
		if (obj instanceof ServerReceivedRegistrationInformationStorage)
		{
			clientInfo = (ServerReceivedRegistrationInformationStorage)obj;
		}
		
		return clientInfo;
		
	}
	
	public static void main(String args[]) throws Exception
	{
		String q = "yurun/123456/100";
		String m = q.split("/")[0];
		SaveClientInformation.writeFile("usrInformation", q, "hao");
		//System.out.println(SaveClientInformation.getContentFromFile("usrInformation", m, "hao"));
		//System.out.println(SaveClientInformation.isUserNameExist("usrInformation", "tefseftgves"));
	}
}
