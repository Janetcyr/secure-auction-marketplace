package Protocol;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class KeyExpiretime {
	private SecretKey sessionKey; 
	private byte[] ivParameter;
    private Calendar expireTime;
    
    public void createKeyExpiretime() throws InterruptedException, NoSuchAlgorithmException, NoSuchPaddingException{
		
 
    this.expireTime=Calendar.getInstance();
	this.expireTime.add(Calendar.MINUTE, 20);
    }
    
    public void setSessionKey(SecretKey session_Key){
    	this.sessionKey = session_Key;
    	
    }
    
    public void setIvParameter(byte[] iv){
    	this.ivParameter= iv;
    }
    
    public void setExpireTime(Calendar expireTimeNow) {
		this.expireTime = expireTimeNow;
	}
    
    public SecretKey getSessionKey() {
		return this.sessionKey;
	}


	public byte[] getIvParameter() {
		return this.ivParameter;
	}


	public  String printExpiretime(){
		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//String exprireTime = df.format(this.expireTime.getTime());
		//System.out.println(expireTime);
		//return df.format(expireTime);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(this.expireTime.getTime()));
        return df.format(this.expireTime.getTime());
	}
	public boolean compareto(Calendar currentTime){
		return this.expireTime.after(currentTime);
		
	}
}
