
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

public class Server{
    //ServerSocket ServerSocket;
    Connection myCon;
    Socket connection = null;
   
    public static void main(String args[]) throws IOException, SQLException{
        
    	//Server server = new Server();
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