package Socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {
	private static ObjectOutputStream output; // output stream to server
	private static ObjectInputStream input; // input stream from server
	private static String message = ""; // message from server
	private final static String chatServer = "127.0.0.1"; // host server for
															// this application
	private static Socket client; // socket to communicate with server

	// connect to server and process messages from server
	public static void runClient() {
		try // connect to server, get streams, process connection
		{
			connectToServer(); // create a Socket to make connection
			getStreams(); // get the input and output streams
			processConnection(); // process connection
		} // end try
		catch (EOFException eofException) {
			System.out.println("\nClient terminated connection");
		} // end catch
		catch (IOException ioException) {
			ioException.printStackTrace();
		} // end catch
		finally {
			closeConnection(); // close connection
		} // end finally
	} // end method runClient

	// connect to server
	public static void connectToServer() throws IOException {

		// create Socket to make connection to server
		client = new Socket(InetAddress.getByName(chatServer), 5430);
	} // end method connectToServer

	// get streams to send and receive data
	public static void getStreams() throws IOException {
		// set up output stream for objects
		output = new ObjectOutputStream(client.getOutputStream());
		output.flush(); // flush output buffer to send header information

		// set up input stream for objects
		input = new ObjectInputStream(client.getInputStream());

		System.out.println("\nGot I/O streams\n");
	} // end method getStreams

	// process connection with server
	public static void processConnection() throws IOException {
		do // process messages sent from server
		{
			try // read message and display it
			{
				message = (String) input.readObject(); // read new message
			} // end try
			catch (ClassNotFoundException classNotFoundException) {
				System.out.println("\nUnknown object type received");
			} // end catch

		} while (!message.equals("TERMINATE"));
	} // end method processConnection

	// close streams and socket
	public static void closeConnection() {
		System.out.println("\nClosing connection");
		try {
			output.close(); // close output stream
			input.close(); // close input stream
			client.close(); // close socket
		} // end try
		catch (IOException ioException) {
			ioException.printStackTrace();
		} // end catch
	} // end method closeConnection

	// send message to server
	public static void sendData(byte[] message) {
		try // send object to server
		{
			output.write(message);
			output.flush();
		} // end try
		catch (IOException ioException) {
			System.out.println("\nError writing object");
		} // end catch
	} // end method sendData

	public static void sendData(String message) {
		try // send object to server
		{
			output.writeObject(message);
			output.flush(); // flush data to output
		} // end try
		catch (IOException ioException) {
			System.out.println("\nError writing object");
		} // end catch
	} // end method sendData

	public static String getData() {
		try // read message and display it
		{
			message = (String) input.readObject(); // read new message
		} // end try
		catch (ClassNotFoundException classNotFoundException) {
			System.out.println("\nUnknown object type received");
		} // end catch
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}

} // end class TCPClientGUI
