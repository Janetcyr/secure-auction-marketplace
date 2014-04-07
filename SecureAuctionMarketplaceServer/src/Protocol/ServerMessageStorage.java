package Protocol;

/**
 * Store the content in the server
 * 
 * @author HaoL
 *
 */
public class ServerMessageStorage {
	private byte[] content;
	private String purpose;
	
	public ServerMessageStorage(byte[] mContent, String mPurpose)
	{
		content = mContent;
		purpose = mPurpose;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public byte[] getContent() {
		return content;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getPurpose() {
		return purpose;
	}
}
