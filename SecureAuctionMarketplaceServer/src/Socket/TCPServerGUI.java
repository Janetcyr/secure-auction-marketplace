package Socket;

//TCPServerGUI.java
// Set up a  TCP Server that will receive a connection from a client, send 
// a string to the client, and close the connection. GUI Version
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Model.BidInfo;
import Model.BidsEngine;
import Model.ProgramBots;
import Model.Serialize;
import Protocol.*;

public class TCPServerGUI extends JFrame {
	private JTextField enterField; // inputs message from user
	private JTextArea displayArea; // display information to user
	private ObjectOutputStream output; // output stream to client
	private ObjectInputStream input; // input stream from client
	private ServerSocket server; // server socket
	private Socket connection; // connection to client
	private int counter = 1; // counter of number of connections

	// set up GUI
	public TCPServerGUI() {
		super("TCP Server");

		enterField = new JTextField(); // create enterField
		enterField.setEditable(false);
		enterField.addActionListener(new ActionListener() {
			// send message to client
			public void actionPerformed(ActionEvent event) {
				sendData(event.getActionCommand());
				enterField.setText("");
			} // end method actionPerformed
		} // end anonymous inner class
				); // end call to addActionListener

		add(enterField, BorderLayout.NORTH);

		displayArea = new JTextArea(); // create displayArea
		add(new JScrollPane(displayArea), BorderLayout.CENTER);

		setSize(300, 150); // set size of window
		setVisible(true); // show window
	} // end Server constructor

	// set up and run server
	public void runServer() throws Exception {
		try // set up server to receive connections; process connections
		{
			server = new ServerSocket(5430, 100); // create ServerSocket

			while (true) {
				try {
					waitForConnection(); // wait for a connection
					getStreams(); // get input & output streams
					processConnection(); // process connection
				} // end try
				catch (EOFException eofException) {
					displayMessage("\nServer terminated connection");
				} // end catch
				finally {
					closeConnection(); // close connection
					counter++;
				} // end finally
			} // end while
		} // end try
		catch (IOException ioException) {
			ioException.printStackTrace();
		} // end catch
	} // end method runServer

	// wait for connection to arrive, then display connection info
	private void waitForConnection() throws IOException {
		displayMessage("Waiting for connection\n");
		connection = server.accept(); // allow server to accept connection
		displayMessage("Connection " + counter + " received from: "
				+ connection.getInetAddress().getHostName());
	} // end method waitForConnection

	// get streams to send and receive data
	private void getStreams() throws IOException {
		// set up output stream for objects
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush(); // flush output buffer to send header information

		// set up input stream for objects
		input = new ObjectInputStream(connection.getInputStream());

		displayMessage("\nGot I/O streams\n");
	} // end method getStreams

	// process connection with client
	private void processConnection() throws Exception {
		String message = "Connection successful";
		// sendData( message ); // send connection successful message
		ServerSignUpDecryptProtocol decrypt = new ServerSignUpDecryptProtocol();

		// enable enterField so server user can send messages
		setTextFieldEditable(true);

		do // process messages sent from client
		{
			try // read message and display it
			{
				String receivedMsg = (String) input.readObject();
				String msgType = receivedMsg.substring(0, 1);
				String encryptedMsg = receivedMsg.substring(1);
				byte[] decryptedBytes = null;
				// Get the byte array of encryptedMessage
				byte[] receivedBytes = encryptedMsg.getBytes();
				if (msgType.equals("0")) { // register
					if (msgType.equals("0")) { // register
						decryptedBytes = decrypt.decryptClientMessage(
								receivedBytes, "1");
						System.out.println(new String(decryptedBytes));
						// decryptedBytes =
						// decrypt.decryptClientMessage(encryptedMsg.getBytes()).getContent();
						Object obj = ProtocolLibrary.toObject(decryptedBytes);
						/*
						 * int communicationCode = -2;
						 * 
						 * if(obj instanceof CommunicatePacket) {
						 * CommunicatePacket thePacket = (CommunicatePacket)obj;
						 * communicationCode =
						 * thePacket.getCommunicationCode("hao"); }else { if
						 * (obj instanceof ClientRegistrationInformation) {
						 * throw new Exception(
						 * "although it is not inherited from communcati packet, it is the CLientRegistrationInformaion class"
						 * ); } throw new Exception(
						 * "The obj is not inherited from CommunicatePacket class:"
						 * + new String(decryptedBytes)); }
						 */

						ClientRegistrationInformation registrar = null;
						if (obj instanceof ClientRegistrationInformation) {
							registrar = (ClientRegistrationInformation) obj;
						} else {
							throw new Exception(
									"It is not CientRegistrationInfor");
						}
						HashMap<String, String> questionBank = registrar
								.getQuestionbank();
						System.out.println("UserName: "
								+ registrar.getUserName() + " Password: "
								+ registrar.getPlainPassword()
								+ " EmailAddress: "
								+ registrar.getEmailAddress());
						ServerReceivedRegistrationInformationStorage storeInfo = new ServerReceivedRegistrationInformationStorage(
								registrar.getUserName(),
								registrar.getPlainPassword(),
								registrar.getEmailAddress(),
								registrar.getSiteKey(), questionBank);
						decrypt.writeClientInformation(storeInfo.getUserName(),
								ProtocolLibrary.toByteArray(storeInfo),
								"usrInformation", "hao");

						// String information = new String(decryptedBytes);
						// decrypt.writeClientInformation(new
						// ServerMessageStorage(decryptedBytes,"SignUpInformation"),
						// "usrInformation", "hao");
						System.out.println("Received Content: "
								+ new String(decryptedBytes));
						// System.out.println("File writen as: " +
						// SaveClientInformation.getContentFromFile("usrInformation",
						// information.split("/")[0], "hao"));

					}

				} else if (msgType.equals("1")) { // log in
					LoginProtocolServer lps = new LoginProtocolServer();
					decryptedBytes = lps.firstStepServer(
							encryptedMsg.getBytes()).getBytes();
					displayMessage("[server] = " + new String(decryptedBytes));
					String msg = new String(decryptedBytes);
					System.out.println("msg = " + msg);
					String encryptedSiteID = new String(
							lps.secondStepServer(msg));
					System.out.println("encrypted = " + encryptedSiteID);
					sendData(encryptedSiteID);

					if (!msg.substring(0, 5).equals("ERROR")) {

						// login with site key

						receivedMsg = (String) input.readObject();
						encryptedMsg = receivedMsg;

						// Get the firstCode of String
						String subCode = encryptedMsg.substring(0, 1);
						String actualContent = encryptedMsg.substring(1);

						if (subCode.equals("3")) { // forget password
							System.out.println("___");
							ForgetPasswordServer forgetServer = new ForgetPasswordServer();
							decryptedBytes = forgetServer.forgetPasswordServer(
									actualContent.getBytes()).getBytes();
							displayMessage("[server] = "
									+ new String(decryptedBytes));
							String msg1 = new String(decryptedBytes);
							System.out.println("msg = " + msg1);
							String encryptedQuestion = new String(
									forgetServer.encryptQuetionFromServer(msg1));
							sendData(encryptedQuestion);
							// sendData("My name is hao.Nice to meet you");
						} else if (subCode.equals("4")) {// reset password
															// It either is here
															// or other place
							ForgetPasswordServer forgetServer = new ForgetPasswordServer();
							decryptedBytes = forgetServer.resetPasswordServer(
									actualContent.getBytes()).getBytes();
							if (true) {
								throw new Exception("I am here.");
							}
							displayMessage("[server] = "
									+ new String(decryptedBytes));
							String msg2 = new String(decryptedBytes);
							System.out.println("msg = " + msg2);
							sendData(msg2);
						} else {
							decryptedBytes = lps.thridStepServer(encryptedMsg
									.getBytes());

							sendData(new String(decryptedBytes));

							System.out.println(new String(decryptedBytes));
						}
					}
				} else if (msgType.equals("2")) { // communication
					ServerCommunicationProtocol sc = new ServerCommunicationProtocol();
					String s = sc.decryptMessage(encryptedMsg.getBytes());

					String reqType = s.substring(0, 1);
					// debug
					System.out.println(reqType);
					s = s.substring(1);

					displayMessage("[Communication] " + s);
					sc.encryptMessage(s);
					sendData(s);

					if (reqType.equals("1")) { // sell item
						// debug
						System.out
								.println("enter communication protocol and reqType is equal to 1");
						String[] bidItem = s.trim().split("/");
						Calendar start = Calendar.getInstance();
						Calendar end = Calendar.getInstance();
						// change by chenxi
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd-hh.mm.ss");// SimpleDateFormat sdf =
														// new
														// SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

						try {
							start.setTime(sdf.parse(bidItem[3]));
							// System.out.println("check calender time :"+sdf.format(start.getTime()));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							end.setTime(sdf.parse(bidItem[4]));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// BidInfo bidInfo_1=new BidInfo(bidItem[2], null,
						// Double.valueOf(bidItem[3]), start,end);
						double startPriceDouble = Double.valueOf(bidItem[2]);
						BidsEngine bidsEngine = new BidsEngine();
						boolean addResult = bidsEngine.addBid(bidItem[0],
								bidItem[1], null, startPriceDouble, start, end);
						bidsEngine.updateSeller(bidItem[1], bidItem[0]);
					} else if (reqType.equals("2")) { // buy item
						// debug
						System.out
								.println("enter communication protocol and reqType is equal to 2");
						String[] bidItem = s.trim().split("/");
						// double startPriceDouble = Double.valueOf(bidItem[2]);
						BidsEngine bidsEngine = new BidsEngine();
						BidInfo bidInfo = BidsEngine.searchBid(bidItem[0]);
						Calendar start = Calendar.getInstance();
						// java.util.Date curTime = start.getTime();
						// Calendar itemBidEndtime=bidInfo.getEnd();
						// debug
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd-hh.mm.ss");
						String curTimeString = new String(sdf.format(start
								.getTime()));
						if (bidInfo.getEnd() == null) {
							System.out.println("bidinfo get end is null");
						}
						String bidEndtimeString = new String(sdf.format(bidInfo
								.getEnd().getTime()));
						System.out.println("curTime:" + curTimeString
								+ "bidEndtime:" + bidEndtimeString);
						// update buyers file
						bidsEngine.updateBuyer(bidItem[1], bidItem[0]);
						// update item file
						if (bidInfo.getEnd().after(start)) {
							System.out
									.println("cutTime should before the bid endtime");
							double bidPriceDouble = Double.valueOf(bidItem[2]);
							if (bidPriceDouble > bidInfo.getPrice()) {
								System.out
										.println("bidPriceDouble is higher than item price");
								bidsEngine.updateBid(bidItem[0], bidItem[1],
										bidItem[2]);
							}
						}
					} else if (reqType.equals("3")) { // upload bots

						String[] texts = s.trim().split("/");

						ProgramBots pb = new ProgramBots(texts[1]);
						// add new bot
						pb.addBot(texts[0], Double.valueOf(texts[2]),
								Double.valueOf(texts[3]));
					} else if (reqType.equals("4")) { // refresh all bids
						BidsEngine bids = new BidsEngine();
						sendData(Serialize.convertObjectToString(bids
								.getAllBids()));
					} else if (reqType.equals("5")) { // refresh my bids

					} else if (reqType.equals("6")) { // refresh my sells

					} else {

					}

				} else {

				}
			} // end try
			catch (ClassNotFoundException classNotFoundException) {
				displayMessage("\nUnknown object type received");
			} // end catch

		} while (!message.equals("TERMINATE"));
	} // end method processConnection

	// close streams and socket
	private void closeConnection() {
		displayMessage("\nTerminating connection\n");
		setTextFieldEditable(false); // disable enterField

		try {
			output.close(); // close output stream
			input.close(); // close input stream
			connection.close(); // close socket
		} // end try
		catch (IOException ioException) {
			ioException.printStackTrace();
		} // end catch
	} // end method closeConnection

	// send message to client
	private void sendData(String message) {
		try // send object to client
		{
			output.writeObject(message);
			output.flush(); // flush output to client
			displayMessage(message);
		} // end try
		catch (IOException ioException) {
			displayArea.append("\nError writing object");
		} // end catch
	} // end method sendData

	// manipulates displayArea in the event-dispatch thread
	private void displayMessage(final String messageToDisplay) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() // updates displayArea
			{
				displayArea.append(messageToDisplay); // append message
			} // end method run
		} // end anonymous inner class
				); // end call to SwingUtilities.invokeLater
	} // end method displayMessage

	// manipulates enterField in the event-dispatch thread
	private void setTextFieldEditable(final boolean editable) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() // sets enterField's editability
			{
				enterField.setEditable(editable);
			} // end method run
		} // end inner class
				); // end call to SwingUtilities.invokeLater
	} // end method setTextFieldEditable
} // end class TCPServerGUI

