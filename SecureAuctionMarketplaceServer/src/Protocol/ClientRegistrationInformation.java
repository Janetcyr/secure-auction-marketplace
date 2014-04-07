package Protocol;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

/**
 * The class will combine all registration information into one single object, and
 * convert the object to a byte array. The class inherits from Communication class, 
 * which will have a communication code to indicate the purpose of the class.
 * 
 * @author HaoL
 *
 */
public final class ClientRegistrationInformation implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6317335396811044081L;
	//The serial number	
	private String username;
	private String plainpassword;
	private String E_mail_address;
	private int site_key;
	private HashMap<String,String> questionBank = new HashMap<String,String>();
	
	/**
	 * The constructor for storing the client registration information
	 * 
	 * @param username
	 * @param password
	 * @param E_mail_address
	 * @param site_key
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 */
	public ClientRegistrationInformation(String username, String password, String E_mail_address, int site_key) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		//super(0);
		this.username = username;
		this.plainpassword = password;
		this.E_mail_address = E_mail_address;
		this.site_key = site_key;
	}
	
	public String getUserName()
	{
		return username;
	}
	
	public String getEmailAddress()
	{
		return E_mail_address;
	}
	
	public int getSiteKey()
	{
		return site_key;
	}
	
	public String getPlainPassword()
	{
		return plainpassword;
	}
	
	/**
	 * Add secure question
	 * 
	 * @param question
	 * @param answer
	 */
	public void addQuestion(String question, String answer)
	{
		questionBank.put(question, answer);
	}
	
	public HashMap<String,String> getQuestionbank()
	{
		return questionBank;
	}
	
	public static void main(String args[]) throws Exception
	{
		ClientRegistrationInformation test = new ClientRegistrationInformation("hao", "1", "fes", 0);
		byte[] sendBytes = ProtocolLibrary.toByteArray(test);
		//System.out.println("The byte array is:" +new String(sendBytes));
		FileOutputStream output = new FileOutputStream(new File("testFile.txt"));
		output.write(sendBytes);
		output.close();
		
		byte[] result = ProtocolLibrary.readByteFromFile("testFile.txt");
		ClientRegistrationInformation testInfo = (ClientRegistrationInformation)ProtocolLibrary.toObject(result);
		System.out.println();
		System.out.println("Password is:" + testInfo.getPlainPassword());
		
		
	}
}		
