package Socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPServer
{

   private ObjectOutputStream output; // output stream to client
   private ObjectInputStream input; // input stream from client
   private ServerSocket server; // server socket
   private Socket connection; // connection to client
   private int counter = 1; // counter of number of connections

   // set up GUI
   public TCPServer()
   {

   } // end Server constructor

   // set up and run server 
   public void runServer()
   {
      try // set up server to receive connections; process connections
      {
         server = new ServerSocket( 12345, 100 ); // create ServerSocket

         while ( true ) 
         {
            try 
            {
               waitForConnection(); // wait for a connection
               getStreams(); // get input & output streams
               processConnection(); // process connection
            } // end try
            catch ( EOFException eofException ) 
            {
               // displayMessage( "\nServer terminated connection" );
            } // end catch
            finally 
            {
               closeConnection(); //  close connection
               counter++;
            } // end finally
         } // end while
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method runServer

   // wait for connection to arrive, then display connection info
   public void waitForConnection() throws IOException
   {
      // displayMessage( "Waiting for connection\n" );
      connection = server.accept(); // allow server to accept connection            
      // displayMessage( "Connection " + counter + " received from: " + connection.getInetAddress().getHostName() );
   } // end method waitForConnection

   // get streams to send and receive data
   public void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( connection.getOutputStream() );
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( connection.getInputStream() );

      // displayMessage( "\nGot I/O streams\n" );
   } // end method getStreams

   // process connection with client
   public void processConnection() throws IOException
   {
      String message = "Connection successful";
      sendData( message ); // send connection successful message

      // enable enterField so server user can send messages
      // setTextFieldEditable( true );

      do // process messages sent from client
      { 
         try // read message and display it
         {
            message = ( String ) input.readObject(); // read new message
            // displayMessage( "\n" + message ); // display message
         } // end try
         catch ( ClassNotFoundException classNotFoundException ) 
         {
          //  displayMessage( "\nUnknown object type received" );
         } // end catch

      } while ( !message.equals( "CLIENT>>> TERMINATE" ) );
   } // end method processConnection

   // close streams and socket
   public void closeConnection() 
   {
      // displayMessage( "\nTerminating connection\n" );
     // setTextFieldEditable( false ); // disable enterField

      try 
      {
         output.close(); // close output stream
         input.close(); // close input stream
         connection.close(); // close socket
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method closeConnection

   // send message to client
   public void sendData( String message )
   {
      try // send object to client
      {
         output.writeObject( "SERVER>>> " + message );
         output.flush(); // flush output to client
        // displayMessage( "\nSERVER>>> " + message );
      } // end try
      catch ( IOException ioException ) 
      {
         //displayArea.append( "\nError writing object" );
      } // end catch
   } // end method sendData
 
} // end class TCPServerGUI

