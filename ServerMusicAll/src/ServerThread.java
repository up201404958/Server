import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ServerThread extends Thread {
	Socket ServerClient;
	int clientNo;
	Connection myCon;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	
	public ServerThread(Socket skt,int counter) {
		this.ServerClient=skt;
		this.clientNo=counter;
	}
	
	public void run()
    {
       //2. Wait for connection
       //while(true) {
            
            System.out.println("Connection received from " + ServerClient.getInetAddress().getHostName() + "number" + this.clientNo);
            //3. get Input and Output streams
            try {
				out = new ObjectOutputStream(ServerClient.getOutputStream());
				out.flush();
				in = new ObjectInputStream(ServerClient.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
            //sendMessage("Connection successful");
            
            int check=0;
 
            //4. The two parts communicate via the input and output streams
            try{
                message = (String)in.readObject();
                System.out.println("client sent>" + message);
                String[] parts = message.split(" ");
                System.out.println(parts[0]+ parts[1]);
   
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
                }else if(parts[0].equals("ARTS")) { //getArtists
                		ArrayList <String> artists = getAlbuns();
                		System.out.println("hey im here");
                		sendMessage(artists);
                }else if(parts[0].equals("CREATE")) {
                		createPlaylist(parts);
                		sendMessage("NEW PLAYLIST");
                }else if(parts[0].equals("PLAYLST")) {
                		ArrayList <String> playlists=getPlaylist(parts);
            			sendMessage(playlists);
                }
               
              
     
            	}
            catch(ClassNotFoundException classnot){
                System.err.println("Data received in unknown format");
            }catch (IOException e) {
				e.printStackTrace();
			}catch (SQLException e) {
				e.printStackTrace();
			}
        }
	
	//CREATE PLAYLIST
	private void createPlaylist(String[] parts) throws SQLException {
		
		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		String query="INSERT INTO playlists (id,name,user_id)"+" values(DEFAULT,?,?)";
		java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
		preparedStmt.setString(1,parts[1]);
		preparedStmt.setString(2,parts[2]);
		preparedStmt.execute();    
	}
    //GET PLAYLISTS
	private ArrayList<String> getPlaylist(String[] parts) throws SQLException {
		
		ArrayList <String> playlists = new ArrayList<String>(); //result set

		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		String query="SELECT * FROM playlists WHERE user_id = ?";
		java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
		preparedStmt.setString(1,parts[1]);
		ResultSet myRs = preparedStmt.executeQuery();
		
		while(myRs.next()) {
			playlists.add(myRs.getInt("id")+","+myRs.getString("name"));
		}
		System.out.println(playlists);
		return playlists;
	
	}
	//GET ALBUMS
	private ArrayList<String> getAlbuns() throws SQLException {
    		
    		ArrayList <String> artists = new ArrayList<String>(); //result set
		
    		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
    		
    		Statement myStmt = myCon.createStatement();
    		ResultSet myRs = myStmt.executeQuery("SELECT * FROM artists");
    		while(myRs.next()) {
    			System.out.println(myRs.getInt("id")+","+myRs.getString("name")+","+myRs.getString("country"));
    			artists.add(myRs.getInt("id")+","+myRs.getString("name")+","+myRs.getString("country"));
    		}
    		
    		return artists;
    		
	}

	//CHECK REGISTER
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
    
    //CHECK VALID USER 
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
	
    //CHECK LOGIN
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
	//SEND MESSAGE
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
    //SEND MESSAGE POLYMORPHISM
	void sendMessage(ArrayList<String> array) {
		try{
            out.writeObject(array);
            out.flush();
            System.out.println("server>" + array);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
		
	}
}
