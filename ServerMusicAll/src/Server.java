/**
 * This class represents the server where requests arrive
 * 
 *
 */
import java.io.*;
import java.net.*;
import java.sql.Connection;

import java.sql.SQLException;


/**
 * This class is used for accepting all the requests that arrive at the server, creating a thread for
 *
 */
public class Server{
    
    Connection myCon;
    Socket connection = null;
   
    public static void main(String args[]) throws IOException, SQLException{
        
    	@SuppressWarnings("resource")
		ServerSocket ServerSocket = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1")); 
    	int counter=0;
    	while(true){
    		System.out.println("Waiting for connection");
    		counter++;
    		Socket ServerClient=ServerSocket.accept();
    		ServerThread socket = new ServerThread(ServerClient,counter);
        socket.start();
    }
  }
}