
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

public class Server{
    ServerSocket ServerSocket;
    Connection myCon;
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    Server(){}
    void run() throws IOException, SQLException
    {
    	   //1. creating a server socket
       ServerSocket = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
       //2. Wait for connection
       while(true) {
            
    	   		System.out.println("Waiting for connection");
            connection = ServerSocket.accept();
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());
            //3. get Input and Output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            //sendMessage("Connection successful");
            int check=0;
 
            //4. The two parts communicate via the input and output streams
            try{
                message = (String)in.readObject();
                System.out.println("client sent>" + message);
                String[] parts = message.split(" ");
   
                if (parts[0].equals("LOGN")) {
                		check=checkLogin(parts);
                		 if(check==0)
                     	sendMessage("INVALID LOGIN");
                     else
                     	sendMessage("VALID LOGIN");
                		
                }else if(parts[0].equals("REGS")) {
                		check=checkRegister(parts);
                		if(check==0)
                         sendMessage("USERNAME ALREADY EXISTS");
                     else
                         sendMessage("REGISTED");
                }
     
            	}
            catch(ClassNotFoundException classnot){
                  System.err.println("Data received in unknown format");
              }
         }
    	}
   
    private int checkRegister(String[] parts) throws SQLException {
    		
    		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
    		
    		if(checkValidUser(parts[1])==1){
    			String query="INSERT INTO users (name,password)"+" values(?,?)";
    			java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
    			preparedStmt.setString(1,parts[1]);
    			preparedStmt.setString(2,parts[2]);
    			preparedStmt.execute();    
    			return 1;
    		}
  				
    		return 0;
	}
    
    private int checkValidUser(String name) throws SQLException {
    	
    		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		Statement myStmt = myCon.createStatement();
		ResultSet myRs = myStmt.executeQuery("SELECT name FROM users");
		
		while(myRs.next()) {
			if(name.equals(myRs.getString("name"))) {
				System.out.print("same");	
				return 0;
			}
		}
    		
		return 1;
    
    }
	
    private int checkLogin(String[] parts) throws SQLException {
    		
    	 	Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
    		Statement myStmt = myCon.createStatement();
		ResultSet myRs = myStmt.executeQuery("SELECT * FROM users");
		
		while(myRs.next()) {
			if( parts[1].equals(myRs.getString("name")) && parts[2].equals(myRs.getString("password")))
					return 1;
		}
		
		return 0;
		
	}
	
    void sendMessage(String msg){
        
    		try{
            out.writeObject(msg);
            out.flush();
            System.out.println("server>" + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    
    public static void main(String args[]) throws IOException, SQLException{
        
    	Server server = new Server();
        while(true){
            server.run();
        }
    }
}