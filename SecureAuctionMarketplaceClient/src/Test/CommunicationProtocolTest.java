package Test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Protocol.*;

public class CommunicationProtocolTest {

	@Test
	public void testBasicCommunication() throws NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchPaddingException, InterruptedException, InvalidKeyException {

		HashTableExample c = new HashTableExample();
		String username1 = "user1";
		KeyExpiretime keyExptime = HashTableExample.userSessionKey
				.get(username1);
		CommunicationProtocol usr1Client = new CommunicationProtocol(
				keyExptime.getSessionKey(), keyExptime.getIvParameter());

		ServerCommunicationProtocol server1 = new ServerCommunicationProtocol();
		// System.out.println(server1.decryptMessage(socketMsg));

		byte[] socketMsg = usr1Client.encryptMessage(username1,
				"clientSendRequestToServer");
		assertEquals(server1.decryptMessage(socketMsg),
				"user1 request is clientSendRequestToServer");

		String respondContent = "Server responds to Client";
		byte[] socketMsg2 = server1.encryptMessage(respondContent);
		// System.out.println(usr1Client.decryptMessage(socketMsg2));
		assertEquals(usr1Client.decryptMessage(socketMsg2),
				"Server responds to Client");

	}

	@Test
	public void testFindUser() throws NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchPaddingException, InterruptedException, InvalidKeyException {

		HashTableExample c = new HashTableExample();
		String username1 = "user1";
		String notuser = "notuser";
		KeyExpiretime keyExptime = HashTableExample.userSessionKey
				.get(username1);
		CommunicationProtocol usr1Client = new CommunicationProtocol(
				keyExptime.getSessionKey(), keyExptime.getIvParameter());

		ServerCommunicationProtocol server1 = new ServerCommunicationProtocol();
		// System.out.println(server1.decryptMessage(socketMsg));

		byte[] socketMsg = usr1Client.encryptMessage(notuser,
				"clientSendRequestToServer");
		assertEquals(server1.decryptMessage(socketMsg),
				"ERROR: CAN'T FIND THIS USER' SESSION KEY");

	}

	@Test
	public void testTimeExpire() throws NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchPaddingException, InterruptedException, InvalidKeyException {

		HashTableExample c = new HashTableExample();
		String username2 = "user2";
		KeyExpiretime keyExptime = HashTableExample.userSessionKey
				.get(username2);
		CommunicationProtocol usr1Client = new CommunicationProtocol(
				keyExptime.getSessionKey(), keyExptime.getIvParameter());

		ServerCommunicationProtocol server1 = new ServerCommunicationProtocol();
		// System.out.println(server1.decryptMessage(socketMsg));

		byte[] socketMsg = usr1Client.encryptMessage(username2,
				"clientSendRequestToServer");
		Calendar oldExpireTime = Calendar.getInstance();
		oldExpireTime.set(2007, 1, 1);
		keyExptime.setExpireTime(oldExpireTime);
		assertEquals(server1.decryptMessage(socketMsg),
				"ERROR: THE SESSION KEY HAS EXPIRED. ");

	}

}
