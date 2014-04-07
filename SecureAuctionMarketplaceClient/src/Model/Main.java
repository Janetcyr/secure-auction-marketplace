package Model;

import java.io.IOException;

import GUI.LogIn;
import Socket.TCPClient;

public class Main {
	public static void main(String[] args) throws IOException {
		TCPClient.connectToServer();
		TCPClient.getStreams();
		
		LogIn logIn = new LogIn();
		logIn.fire();
	} // end main

}
