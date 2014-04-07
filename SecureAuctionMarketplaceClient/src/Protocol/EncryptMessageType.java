package Protocol;

public class EncryptMessageType {

	private byte[] encryMsg;
	private byte[] hashedMsg;

	public EncryptMessageType() {
		super();
	}

	public EncryptMessageType(byte[] encryMsg, byte[] hashedMsg) {
		super();
		this.encryMsg = encryMsg;
		this.hashedMsg = hashedMsg;
	}

	public void setEncryMsg(byte[] encryMsg) {
		this.encryMsg = encryMsg;
	}

	public void setHashedMsg(byte[] hashedMsg) {
		this.hashedMsg = hashedMsg;
	}

	public byte[] getEncryMsg() {
		return encryMsg;
	}

	public byte[] getHashedMsg() {
		return hashedMsg;
	}

}