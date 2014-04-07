package Controller;

import javax.swing.JFrame;

import Socket.TCPServerGUI;

public class Main {
	   public static void main( String args[] ) throws Exception
	   {
	      TCPServerGUI application = new TCPServerGUI(); // create server
	      application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	      application.runServer(); // run server application
	   } // end main
}
