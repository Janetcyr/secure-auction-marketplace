package Protocol;
import java.io.*;  
import java.nio.ByteBuffer;
import java.security.*; 
import java.util.Arrays;

import javax.crypto.*;  
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
 
public class LoginProtocolClient {
	
	/**
	 * @param args
	 * @author Yurun(Janet)
	 * @exception
	 */
	
	 public PublicKey serverPublicKey = null;
	 private SecretKey session_Key = null;
     private SecretKey shared_Key = null;
     private byte[] ivP = null;
     private byte[] ivSessionKey = null;

     
     //get SessionKey for communication
     public SecretKey getSessionKey() {
    	 return this.session_Key;
     }
     
     
     //get SessionKey IV parameter for communication
     public byte[] getIVParameterSessionKey(){
    	 return this.ivSessionKey;
     }
     
     
	 // generate 128-bit shared key using AES;using secure-random
	 public void sharedKeyGenerater() throws Exception {
		 // generate a 128-bit AES key
		KeyGenerator keygen = KeyGenerator.getInstance("AES");  
	    SecureRandom random = new SecureRandom(); 
	    keygen.init(128,random); 
	    SecretKey skey = keygen.generateKey();
	    this.shared_Key = skey; 
	 }

	 
	 // using the generated shared key to encrypt data,using CBC and PKCS5Padding
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
	 
	 
	 //generate hash value of encrypted data
	 public byte[] hashValueOfEncryption(byte[] data) throws Exception{
		 //byte[] dataGet = data.getBytes("UTF8");
		 MessageDigest hashValue = MessageDigest.getInstance("SHA-512");
		 hashValue.update(data);
		 byte[] hashedEncryption = hashValue.digest();
		 return hashedEncryption; 
	 }
	 
	 
	 // using public key from file to encrypt the shared key
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
	 
		 
	 //decrypt response data from server using shared key
	 public byte[] sharedKeyDecryptionClient(byte[] data) throws Exception {
			Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivSpec2 = new IvParameterSpec(this.ivP);
			cipher2.init(Cipher.DECRYPT_MODE, this.shared_Key, ivSpec2);
			String tmp2 = new String(data, "UTF-8");
			byte[] decrypteddata = cipher2.doFinal(data);
			return decrypteddata;
		}
	
	 
	 //client send {username} to server,along with Hash value and shared key
	 public byte[] firstStepClient(String username) throws Exception{
		 String username1 = username;
		 System.out.println("username="+username1);
		 byte[] usernameToHash = username1.getBytes("UTF8");
		 //If shared_Key have not been generated, generate a shared key
		 if (this.shared_Key == null){
			this.sharedKeyGenerater();
		 }
		 //content of last four field to send in a package
		 byte[] encryptedUsername = this.sharedKeyEncryption(username1);
		 String c1 = new String(encryptedUsername,"UTF8");
		 byte[] hashedUsername = this.hashValueOfEncryption(usernameToHash);
		 String c2 = new String(hashedUsername,"UTF8");
		 byte[] encryptedKey = this.publicKeyEncryption(this.shared_Key);
		 String c3 = new String(encryptedKey,"UTF8");
		 byte[] ivDeliver = this.publicIVEncryption(this.ivP);
		 String c4 = new String(ivDeliver,"UTF8");
		 
		 //length of last four field in a package
		 byte[] messageLength = ProtocolLibrary.intToByteArray(encryptedUsername.length);
		 byte[] hashLength = ProtocolLibrary.intToByteArray(hashedUsername.length);
		 byte[] keyLength = ProtocolLibrary.intToByteArray(encryptedKey.length);
		 byte[] ivLength = ProtocolLibrary.intToByteArray(ivDeliver.length);
		 byte[] firstStep = ProtocolLibrary.combineByteArray(keyLength,messageLength,hashLength,ivLength,encryptedKey,encryptedUsername,hashedUsername,ivDeliver);
		 return firstStep; 
	 }
	 
	 
	 //client return {site-key} string if the username is correct
	 public byte[] secondStepClient(byte[] dataFromServerSecond) throws Exception{
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
		 String hashReceived = new String (hashServerReceived,"UTF8");
		 String hashComputed = new String (hashValueClientComputed,"UTF8");
		 if (hashReceived.equals(hashComputed)){
			 System.out.println("Pass:Integrity Ensured: Second Step Client");
			 String messageToSplit = new String(messageServerDecrypted,"UTF8");
			 String[] messageSplit = messageToSplit.split("/");
			 String site_key =  messageSplit[1];
			 System.out.println("Pass:Site_key" + site_key );
			 byte[] siteKeyReturn = site_key.getBytes("UTF8");
			 return siteKeyReturn;	 
		 }
		 else{
			 System.out.println("Error:Integrity Violated:Second Step Client");
			 return null;
		 }		 
		}
	     
	 
	 //client send password to server
	 public byte[] thridStepClient (String usernamepassword) throws Exception{
		 if (usernamepassword == null) {
			 return null;
		 }
		 byte[] usernamepasswordByte = usernamepassword.getBytes("UTF8");
		 
		 //encrypt data
		 byte[] encryptedUserPassword = this.sharedKeyEncryption(usernamepassword);
		 byte[] hashedValue = this.hashValueOfEncryption(usernamepasswordByte);
		 
		 byte[] messageLength = ProtocolLibrary.intToByteArray(encryptedUserPassword.length);
		 byte[] hashLength = ProtocolLibrary.intToByteArray(hashedValue.length);
		 
		 byte[] clientSentToServer = ProtocolLibrary.combineByteArray4(messageLength, hashLength, encryptedUserPassword, hashedValue);
		 return clientSentToServer;
	 }
	 
	 
	 //Client receive SessionKey and IV
	 public void fourthStepClient (byte[] dataFromServer3 ) throws Exception {
		 int sessionKeyLengthServer = ProtocolLibrary.fromByteArrayToInt(Arrays
					.copyOfRange(dataFromServer3, 0, 4));
		 int IVLengthServer = ProtocolLibrary.fromByteArrayToInt(Arrays.copyOfRange(
				 dataFromServer3, 4, 8));
		 
		 byte[] sessionKeyEncrypted =  Arrays.copyOfRange(dataFromServer3, 8,
					8 +sessionKeyLengthServer);
		 byte[] ivSessionKeyEncrypted = Arrays.copyOfRange(dataFromServer3, 8 +sessionKeyLengthServer,
					8 +sessionKeyLengthServer+IVLengthServer);
		 
		 byte[] sessionKeyDecrypted = this.sharedKeyDecryptionClient(sessionKeyEncrypted);
		 byte[] ivSessionKeyDecrypted = this.sharedKeyDecryptionClient(ivSessionKeyEncrypted);
		 this.session_Key = new SecretKeySpec(sessionKeyDecrypted, "AES");
		 this.ivSessionKey = ivSessionKeyDecrypted;
	 }
	 
}
