/**
 * This class represents the server where requests arrive
 * 
 *
 */
import java.io.*;
import java.net.*;
import java.sql.Connection;

import java.sql.SQLException;


public class Server{
    //ServerSocket ServerSocket;
    Connection myCon;
    Socket connection = null;
   
    public static void main(String args[]) throws IOException, SQLException{
        
    	//Server server = new Server();
    	@SuppressWarnings("resource")
		ServerSocket ServerSocket = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1")); //close socket when program shuts down
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