package Protocol;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ForgetPasswordServer {
	
	private SecretKey serverSharedKey = null;
	private byte[] ivP1 = null;
	
	//RSA parameter
	private PrivateKey private_Key = null;
	

	
	public String forgetPasswordServer(byte[] dataFromClient) throws Exception {
		byte[] dataFromClient1 = dataFromClient;
		int KeyLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				dataFromClient1, 0, 4));
		int messageLength = ProtocolLibrary.fromByteArrayToInt(Arrays
				.copyOfRange(dataFromClient1, 4, 8));
		int hashLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				dataFromClient1, 8, 12));
		int ivLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				dataFromClient1, 12, 16));

		byte[] encryptedKey = Arrays.copyOfRange(dataFromClient1, 16,
				16 + KeyLength);
		byte[] encryptedMessage = Arrays.copyOfRange(dataFromClient1,
				16 + KeyLength, 16 + KeyLength + messageLength);
		byte[] encryptedHashValue = Arrays.copyOfRange(dataFromClient1, 16
				+ KeyLength + messageLength, 16 + KeyLength + messageLength
				+ hashLength);
		byte[] encryptediv = Arrays.copyOfRange(dataFromClient1, 16 + KeyLength
				+ messageLength + hashLength, 16 + KeyLength + messageLength
				+ hashLength + ivLength);

		this.ivP1 = this.privateKeyDecryptionIV(encryptediv);
		this.serverSharedKey = this.privateKeyDecryption(encryptedKey);
		byte[] decryptedMessage = this.sharedKeyDecryption(encryptedMessage);
		byte[] computedHash = this.hashValueOfReceivedData(decryptedMessage);
		String hashReceived = new String(encryptedHashValue, "UTF8");
		String hashComputed = new String(computedHash, "UTF8");
		if (hashReceived.equals(hashComputed)) {
			System.out.println("Pass: Integrity Ensured:UsrID unchanged");
			String decryptedUserIDString = new String(decryptedMessage, "UTF8");
			if (SaveClientInformation.isUserNameExist("usrInformation", decryptedUserIDString)){
				System.out.println("Pass:User File Exist In System");
				Object obj = SaveClientInformation.getObjectFromFile("usrInformation", decryptedUserIDString, "hao");
				ServerReceivedRegistrationInformationStorage clientInfo = null;
				if (obj instanceof ServerReceivedRegistrationInformationStorage)
				{
					clientInfo = (ServerReceivedRegistrationInformationStorage)obj;
				} else
				{
					throw new Exception("Can't change the obj to ServerRegistrationInformationStorage");
				}
				//String[] usern = null;
				//usern = this.checkDataInFile();
				//String[] contentInFileSplit = contentInFile.split("/");
				String usernameToCheck = clientInfo.getUserName();
				if (decryptedUserIDString.equals(usernameToCheck)) {
					System.out.println("Pass:Username Exist In File");
					System.out.println("The Username is:" + usernameToCheck);
					Iterator<String> questions = clientInfo.getQuestions();
					StringBuilder sb = new StringBuilder();
					HashMap<String,String> map = clientInfo.getQuestionBank();
					while(questions.hasNext())
					{
						String thequestion =  questions.next();
						String theanswer = map.get(thequestion);
						sb.append(thequestion + "/" + theanswer);
						sb.append("_");
					}
					String questionString = new String(sb);
					System.out.println("The question string is:"+questionString);
					return questionString;
				} else {
					String failure = "ERROR: Username does not exist";
					System.out.println("ERROR: Username: "
							+ decryptedUserIDString+  "Does Not Exist");
					return failure;
				}
		  }
			else {
				System.out.println("ERROR: User File Does Not Exist In System" );
				return "ERROR: User File Does Not Exist In System";
			}
		} else {
			System.out.println("ERROR: Integrity Violated: First Step");
			return "ERROR: Integrity Violated: First Step";
		}
	}
	
	/**
	 * 
	 * @param dataFromClient
	 * @return
	 * @throws Exception
	 */
	public String resetPasswordServer(byte[] dataFromClient) throws Exception {
		byte[] dataFromClient1 = dataFromClient;
		int KeyLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				dataFromClient1, 0, 4));
		int messageLength = ProtocolLibrary.fromByteArrayToInt(Arrays
				.copyOfRange(dataFromClient1, 4, 8));
		int hashLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				dataFromClient1, 8, 12));
		int ivLength = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				dataFromClient1, 12, 16));

		byte[] encryptedKey = Arrays.copyOfRange(dataFromClient1, 16,
				16 + KeyLength);
		byte[] encryptedMessage = Arrays.copyOfRange(dataFromClient1,
				16 + KeyLength, 16 + KeyLength + messageLength);
		byte[] encryptedHashValue = Arrays.copyOfRange(dataFromClient1, 16
				+ KeyLength + messageLength, 16 + KeyLength + messageLength
				+ hashLength);
		byte[] encryptediv = Arrays.copyOfRange(dataFromClient1, 16 + KeyLength
				+ messageLength + hashLength, 16 + KeyLength + messageLength
				+ hashLength + ivLength);

		this.ivP1 = this.privateKeyDecryptionIV(encryptediv);
		this.serverSharedKey = this.privateKeyDecryption(encryptedKey);
		byte[] decryptedMessage = this.sharedKeyDecryption(encryptedMessage);
		byte[] computedHash = this.hashValueOfReceivedData(decryptedMessage);
		String hashReceived = new String(encryptedHashValue, "UTF8");
		String hashComputed = new String(computedHash, "UTF8");
		if (hashReceived.equals(hashComputed)) {
			System.out.println("Pass: Integrity Ensured:UsrID unchanged");
			String decryptedUserIDAndPasswordString = new String(decryptedMessage, "UTF8");
			String userID = decryptedUserIDAndPasswordString.split("/")[0];
			String password = decryptedUserIDAndPasswordString.split("/")[1];
			if (SaveClientInformation.isUserNameExist("usrInformation", userID)){
				System.out.println("Pass:User File Exist In System");
				Object obj = SaveClientInformation.getObjectFromFile("usrInformation", userID, "hao");
				ServerReceivedRegistrationInformationStorage clientInfo = null;
				if (obj instanceof ServerReceivedRegistrationInformationStorage)
				{
					clientInfo = (ServerReceivedRegistrationInformationStorage)obj;
				} else
				{
					throw new Exception("Can't change the obj to ServerRegistrationInformationStorage");
				}
				//String[] usern = null;
				//usern = this.checkDataInFile();
				//String[] contentInFileSplit = contentInFile.split("/");
				String usernameToCheck = clientInfo.getUserName();
				if (userID.equals(usernameToCheck)) {
					clientInfo.setNewPasswordInEncryptedMode(password);
					SaveClientInformation.updateFile("usrInformation", userID, ProtocolLibrary.toByteArray(clientInfo), "hao");
					return new String("Success");
					//Save it to the file
				} else {
					String failure = "ERROR: Username does not exist";
					System.out.println("ERROR: Username: "
							+ userID+  "Does Not Exist");
					return failure;
				}
		  }
			else {
				System.out.println("ERROR: User File Does Not Exist In System" );
				return "ERROR: User File Does Not Exist In System";
			}
		} else {
			System.out.println("ERROR: Integrity Violated: First Step");
			return "ERROR: Integrity Violated: First Step";
		}
	}
	
	public byte[] encryptQuetionFromServer(String theplainQuestion) throws Exception {
		// change data to byte[];
		if (theplainQuestion == null){
			return null;
		}
		byte[] dataFromServerByte = theplainQuestion.getBytes("UTF8");

		// response byte[] parts
		byte[] responseMessage1 = this
				.sharedKeyEncryptionServer(theplainQuestion);
		byte[] responseHash1 = this.hashValueOfReceivedData(dataFromServerByte);
		byte[] responseMessageLength = ProtocolLibrary
				.intToByteArray(responseMessage1.length);
		byte[] responseHashLength = ProtocolLibrary
				.intToByteArray(responseHash1.length);
		byte[] secondStepFromServer = ProtocolLibrary.combineByteArray4(
				responseMessageLength, responseHashLength, responseMessage1,
				responseHash1);
		return secondStepFromServer;
	}
	
	public byte[] privateKeyDecryptionIV(byte[] data) throws Exception {
		if (this.private_Key == null) {
			this.private_Key = ProtocolLibrary
					.getPrivateKeyFromFileProvidedWithPassword(
							"privateKey.pub", "IVPara.txt", "hao");
		}
		Cipher decryptIVCipher = Cipher.getInstance("RSA");
		decryptIVCipher.init(Cipher.DECRYPT_MODE, this.private_Key);
		byte[] decryptedIV = decryptIVCipher.doFinal(data);
		return decryptedIV;
	}
	
	public SecretKey privateKeyDecryption(byte[] data) throws Exception {
		if (this.private_Key == null) {
			this.private_Key = ProtocolLibrary
					.getPrivateKeyFromFileProvidedWithPassword(
							"privateKey.pub", "IVPara.txt", "hao");
		}
		Cipher decryptKeyCipher = Cipher.getInstance("RSA");
		decryptKeyCipher.init(Cipher.DECRYPT_MODE, this.private_Key);
		byte[] decryptedSharedKey = decryptKeyCipher.doFinal(data);
		SecretKey aKeySpec = new SecretKeySpec(decryptedSharedKey, "AES");
		this.serverSharedKey = aKeySpec;
		return serverSharedKey;
	}
	
	public byte[] sharedKeyDecryption(byte[] data) throws Exception {
		Cipher cipher1 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivSpec1 = new IvParameterSpec(this.ivP1);
		cipher1.init(Cipher.DECRYPT_MODE, this.serverSharedKey, ivSpec1);
		byte[] decrypteddata = cipher1.doFinal(data);
		return decrypteddata;
	}
	
	public byte[] hashValueOfReceivedData(byte[] dataDecrypted)
	throws Exception {
		MessageDigest hashValue = MessageDigest.getInstance("SHA-512");
		hashValue.update(dataDecrypted);
		byte[] hashedReceived = hashValue.digest();
		return hashedReceived;
	}
	
	public byte[] sharedKeyEncryptionServer(String data) throws Exception {
		Cipher cipher3 = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivSpecServer = new IvParameterSpec(this.ivP1);
		cipher3.init(Cipher.ENCRYPT_MODE, this.serverSharedKey, ivSpecServer);
		byte[] ciphertext = cipher3.doFinal(data.getBytes("UTF8"));
		return ciphertext;
	}
}
