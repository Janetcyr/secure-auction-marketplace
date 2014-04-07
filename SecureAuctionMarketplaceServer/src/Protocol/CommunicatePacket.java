package Protocol;

import java.io.Serializable;

/**
 * All communication class should inherit this class to contain the communication code
 * 
 * @author HaoL
 *
 */
public abstract class CommunicatePacket implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5352041752113350809L;
	/**
	 * A code(int) to distingusih the purpose of the message.Like 0 is for registration
	 * , 1 is for Sign in, 3 for log out and 4 for error message.
	 */
	private int communicationCode;
	
	/**
	 * Set the code of communication
	 * 
	 * @param code
	 * 		The code to set for communication
	 */
	public CommunicatePacket(int code)
	{
		setCommunicationCode(code);
	}
	
	/**
	 * Setter of code
	 * 
	 * @param code
	 */
	public void setCommunicationCode(int code)
	{
		communicationCode = code;
	}
	
	/**
	 * Enter a password to get the communication code. If the return value of code is -1
	 * , then the password is not correct.
	 * 
	 * @param authenticationPassword
	 * @return
	 */
	public int getCommunicationCode(String authenticationPassword)
	{
		if (authenticationPassword.compareTo("hao") == 0 )
		{
			return this.communicationCode;
		}
		
		return -1;
	}
	
	
}
