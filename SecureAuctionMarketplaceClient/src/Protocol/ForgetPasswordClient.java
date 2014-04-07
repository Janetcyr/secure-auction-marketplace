package Protocol;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * 
 * Forget password
 * 
 * @author HaoL
 *
 */
public class ForgetPasswordClient {
	
	 public PublicKey serverPublicKey = null;
     private SecretKey shared_Key = null;
     private byte[] ivP = null;
     
	public byte[] forgetPassword (String userID) throws Exception{
		 byte[] usernameToHash = userID.getBytes("UTF8");
		 //If shared_Key have not been generated, generate a shared key
		 if (this.shared_Key == null){
			this.sharedKeyGenerater();
		 }
		 //content of last four field to send in a package
		 byte[] encryptedUsername = sharedKeyEncryption(userID);
		 byte[] hashedUsername = hashValueOfEncryption(usernameToHash);
		 byte[] encryptedKey = publicKeyEncryption(this.shared_Key);
		 byte[] ivDeliver = publicIVEncryption(this.ivP);
		 
		 //length of last four field in a package
		 byte[] messageLength = ProtocolLibrary.intToByteArray(encryptedUsername.length);
		 byte[] hashLength = ProtocolLibrary.intToByteArray(hashedUsername.length);
		 byte[] keyLength = ProtocolLibrary.intToByteArray(encryptedKey.length);
		 byte[] ivLength = ProtocolLibrary.intToByteArray(ivDeliver.length);
		 byte[] firstStep = ProtocolLibrary.combineByteArray(keyLength,messageLength,hashLength,ivLength,encryptedKey,encryptedUsername,hashedUsername,ivDeliver);
		 return firstStep; 
	 }
	
	public byte[] ResetPassword (String userIDAndPassword) throws Exception{
		 byte[] usernameToHash = userIDAndPassword.getBytes("UTF8");
		 //If shared_Key have not been generated, generate a shared key
		 if (this.shared_Key == null){
			this.sharedKeyGenerater();
		 }
		 //content of last four field to send in a package
		 byte[] encryptedUsernameAndPassword = sharedKeyEncryption(userIDAndPassword);
		 byte[] hashedUsername = hashValueOfEncryption(usernameToHash);
		 byte[] encryptedKey = publicKeyEncryption(this.shared_Key);
		 byte[] ivDeliver = publicIVEncryption(this.ivP);
		 
		 //length of last four field in a package
		 byte[] messageLength = ProtocolLibrary.intToByteArray(encryptedUsernameAndPassword.length);
		 byte[] hashLength = ProtocolLibrary.intToByteArray(hashedUsername.length);
		 byte[] keyLength = ProtocolLibrary.intToByteArray(encryptedKey.length);
		 byte[] ivLength = ProtocolLibrary.intToByteArray(ivDeliver.length);
		 byte[] firstStep = ProtocolLibrary.combineByteArray(keyLength,messageLength,hashLength,ivLength,encryptedKey,encryptedUsernameAndPassword,hashedUsername,ivDeliver);
		 return firstStep; 
	 }
	
	/**
	 * Generate Encrypted cipherText
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public byte[] sharedKeyEncryption(String data) throws Exception{
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    //generate iv parameter of sharedKey;
	    if (this.ivP == null){
		    int blockSize=cipher.getBlockSize();
	        byte[] ivBytes=new byte[blockSize];
	        SecureRandom random = new SecureRandom();
	        random.nextBytes(ivBytes);
	        this.ivP = ivBytes;
	    }
       IvParameterSpec ivSpecC=new IvParameterSpec(ivP);
       if (shared_Key == null){
       	this.sharedKeyGenerater();
       }
       
	    cipher.init(Cipher.ENCRYPT_MODE,this.shared_Key,ivSpecC);
	    byte[] ciphertext = cipher.doFinal(data.getBytes("UTF8"));
	    return ciphertext; 
	 }
	 
	 /**
	  * Get the hashValue of Encryption
	  * 
	  * @param data
	  * @return
	  * @throws Exception
	  */
	 public byte[] hashValueOfEncryption(byte[] data) throws Exception{
		 //byte[] dataGet = data.getBytes("UTF8");
		 MessageDigest hashValue = MessageDigest.getInstance("SHA-512");
		 hashValue.update(data);
		 byte[] hashedEncryption = hashValue.digest();
		 return hashedEncryption; 
	 }
	 
	 /**
	  * Generate a shared key
	  * 
	  * @throws Exception
	  */
	 public void sharedKeyGenerater() throws Exception {
		 // generate a 128-bit AES key
		KeyGenerator keygen = KeyGenerator.getInstance("AES");  
	    SecureRandom random = new SecureRandom(); 
	    keygen.init(128,random); 
	    SecretKey skey = keygen.generateKey();
	    this.shared_Key = skey; 
	 }
	 

	 public byte[] publicKeyEncryption(SecretKey sharedKey) throws Exception{
		 if (this.serverPublicKey == null) {
			this.serverPublicKey = ProtocolLibrary.readPublicKeyFromFile("publicKey.pub");
		 }
		 Cipher encryptKeyCipher = Cipher.getInstance("RSA");
		 encryptKeyCipher.init(Cipher.ENCRYPT_MODE,this.serverPublicKey);
		 byte[] encryptedSharedKey = encryptKeyCipher.doFinal(sharedKey.getEncoded());
		 return encryptedSharedKey;	 
	 }
	 
	 //using public key from file to encrypt iv of shared key
	 public byte[] publicIVEncryption(byte[] iv) throws Exception{
		 byte[] ivIn = iv;
		 if (this.serverPublicKey == null) {
			this.serverPublicKey = ProtocolLibrary.readPublicKeyFromFile("publicKey.pub");
		 }
		 Cipher encryptIVCipher = Cipher.getInstance("RSA");
		 encryptIVCipher.init(Cipher.ENCRYPT_MODE,this.serverPublicKey);
		 byte[] encryptedIV = encryptIVCipher.doFinal(ivIn);
		 return encryptedIV;
	 }
	 
	 public byte[] decryptQuesitonClient(byte[] dataFromServerSecond) throws Exception{
		 if (dataFromServerSecond == null){
			 return null;
		 }
		 int messageLengthServer = ProtocolLibrary.fromByteArrayToInt(Arrays
					.copyOfRange(dataFromServerSecond, 0, 4));
		 int hashLengthServer = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				 dataFromServerSecond, 4, 8));

		 // parse byte[] from input
		 byte[] messageServerEncrypted = Arrays.copyOfRange(dataFromServerSecond, 8,
					8 + messageLengthServer);
		 byte[] hashServerReceived = Arrays.copyOfRange(dataFromServerSecond, 8 + messageLengthServer,
					8 + messageLengthServer+hashLengthServer);
		 
		 // decrypted message and computer hash of message received
		 byte[] messageServerDecrypted = this.sharedKeyDecryptionClient(messageServerEncrypted);
		 byte[] hashValueClientComputed = this.hashValueOfEncryption(messageServerDecrypted);
		 
		 //check hash value
		 String hashReceived = new String (hashServerReceived,"UTF-8");
		 String hashComputed = new String (hashValueClientComputed,"UTF-8");
		 if (hashReceived.equals(hashComputed)){
			 System.out.println("Pass:Integrity Ensured: Second Step Client");
			 String question = new String(messageServerDecrypted,"UTF8");
			 System.out.println("Pass:Site_key" + question );
			 byte[] siteKeyReturn = question.getBytes("UTF8");
			 return siteKeyReturn;	 
		 }
		 else{
			 System.out.println("Error:Integrity Violated:Second Step Client");
			 return null;
		 }		 
	}
	 
	//decrypt response data from server using shared key
	 public byte[] sharedKeyDecryptionClient(byte[] data) throws Exception {
			Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivSpec2 = new IvParameterSpec(this.ivP);
			cipher2.init(Cipher.DECRYPT_MODE, this.shared_Key, ivSpec2);
			String tmp2 = new String(data, "UTF-8");
			byte[] decrypteddata = cipher2.doFinal(data);
			return decrypteddata;
		}
}
