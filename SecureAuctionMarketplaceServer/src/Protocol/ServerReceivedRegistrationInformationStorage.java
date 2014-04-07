package Protocol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Iterator;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * 
 * The information that is going to store on the file would be a convertion from
 * object to byte array.
 * 
 * @author HaoL
 *
 */
public final class ServerReceivedRegistrationInformationStorage implements Serializable{
	/**
	 * Auto generated serialVersionUID
	 */
	private static final long serialVersionUID = 6445933219576277407L;
	private String userName = null;
	private byte[] encryptedPassword = null;
	private byte[] salt = null;
	private int siteKey = -1;
	private String mailAddress = null;
	private HashMap<String,String> questionBank = new HashMap<String,String>();
	//PlainPassword for testing only
	private String password;
	
	public String getPassword()
	{
		return this.password;
	}
	public ServerReceivedRegistrationInformationStorage(String userName, String password, String EmailAddress, int siteKey) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		this.salt = generateSalt();
		this.encryptedPassword = getEncryptedPassword(password, salt);
		this.userName = userName;
		this.siteKey = siteKey;
		this.mailAddress = EmailAddress;
		this.password = password;
	}
	
	public ServerReceivedRegistrationInformationStorage(String userName, String password, String EmailAddress, int siteKey, HashMap<String,String> questionbank) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		this(userName, password, EmailAddress, siteKey);
		this.addQuesitionSet(questionbank);
	}

	/**
	 * create a secure randomly salt
	 * 
	 * @return
	 * 		the random generated salt
	 * @throws NoSuchAlgorithmException
	 */
	private final byte[] generateSalt() throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		return salt;
	}
	
	/**
	 * Change the plain password to encrypted password
	 * 
	 * @param password
	 * @param salt
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public final byte[] getEncryptedPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException 
	{
		String algorithm = "PBKDF2WithHmacSHA1";
		//SH1 generate 160 bit hashes, so here is what makes sense.
		int derivedKeyLength = 160;
		int iteration = 20000;
		KeySpec spec = new PBEKeySpec(password.toCharArray(),salt, iteration, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		return f.generateSecret(spec).getEncoded();
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public int getSiteKey()
	{
		return this.siteKey;
	}
	
	protected byte[] getSalt()
	{
		return this.salt;
	}
	
	protected byte[] getEncryptedPassword()
	{
		return this.encryptedPassword;
	}
	
	protected String getEmailAddress()
	{
		return this.mailAddress;
	}
	
	protected void addQeustionBank(String question,String answer)
	{
		questionBank.put(question, answer);
	}
	
	protected void addQuesitionSet(HashMap<String,String> questionSet)
	{
		this.questionBank = new HashMap<String,String>(questionSet);
	}
	
	protected HashMap<String,String> getQuestionBank()
	{
		return questionBank;
	}
	
	public Iterator<String> getQuestions()
	{
		return this.questionBank.keySet().iterator();
	}
	
	public boolean compareQuestion(HashMap<String,String> enteredQuestion)
	{
		return enteredQuestion.equals(questionBank);
	}
	
	public void setNewPasswordInEncryptedMode(String newPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		byte[] newSalt = generateSalt();
		this.salt = newSalt;
		byte[] thenewEncryptedPassword = getEncryptedPassword(newPassword, newSalt);
		this.encryptedPassword = thenewEncryptedPassword;
	}
	
	public static void main(String args[]) throws Exception
	{
		ServerReceivedRegistrationInformationStorage test = 
				new ServerReceivedRegistrationInformationStorage("hao","pwd","myEmail",1);
		
		byte[] testByte = ProtocolLibrary.toByteArray(test);
		Object obj  = ProtocolLibrary.toObject(testByte);
		if (obj instanceof CommunicatePacket)
		{
			System.out.println("It is communicatePacket clild");
		}
		System.out.println("Email is: " + ((ServerReceivedRegistrationInformationStorage)obj).getEmailAddress());
	}
}

