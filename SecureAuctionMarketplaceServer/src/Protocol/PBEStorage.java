package Protocol;
import java.io.Serializable;


/**
 * 
 * @author HaoL
 *
 */
public class PBEStorage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1763421718311517583L;
	private byte[] iv;
	private byte[] ciphertext;
	
	public PBEStorage(byte[] iv, byte[]ciphertext)
	{
		this.iv = iv;
		this.ciphertext = ciphertext;
	}
	
	public byte[] getCiphertext(){
		return ciphertext;
	}
	
	public byte[] getIV(){
		return iv;
	}
}
