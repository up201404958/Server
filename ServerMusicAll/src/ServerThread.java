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
                		ArrayList <String> artists = getArtists();
                		//System.out.println("hey im here");
                		sendMessage(artists);
                	}else if(parts[0].equals("ALBS")){
                		ArrayList <String> albums = getAlbums();
                		sendMessage(albums);
                	}else if(parts[0].equals("SNGS")) {
                		ArrayList <String> songs = getSongs();
                		sendMessage(songs);
                	}else if(parts[0].equals("SNGPLST")) {         		
                		ArrayList <String> playlists=getPlaylist(parts);
                		sendMessage(playlists);
                	}else if(parts[0].equals("CREATE")) {
                		createPlaylist(parts);
                		sendMessage("NEW PLAYLIST");
                }else if(parts[0].equals("PLAYLST")) {
                		ArrayList <String> playlists=getPlaylist(parts);
            			sendMessage(playlists);
                }else if(parts[0].equals("ADDSNG")) {
                		insertSongPlaylist(parts);
                		sendMessage("ADDED SONG");
                }else if(parts[0].equals("GOTOTA")) { //specific album
                		ArrayList <String> album=getAlbumSongs(parts);
                		sendMessage(album);
                }else if(parts[0].equals("PLSTSNG")) {
                		ArrayList <String> songs=getPlaylistSongs(parts);
                		sendMessage(songs);
                }else if(parts[0].equals("KPLSTSNG")) {
            			ArrayList <String> songs=orderPlaylistKey(parts);
            			sendMessage(songs);
                }else if(parts[0].equals("BPLSTSNG")) {
            			ArrayList <String> songs=orderPlaylistBPM(parts);
            			sendMessage(songs);
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
	//GET PLAYLISTS SONGS
	private ArrayList<String> getPlaylistSongs(String[] parts) throws SQLException {
		
		ArrayList <String> songs = new ArrayList<String>(); //result set

		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		String query="SELECT * FROM playlist_song JOIN song ON song_id = song.id WHERE playlist_id = ?";
		java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
		preparedStmt.setString(1,parts[1]);
		ResultSet myRs = preparedStmt.executeQuery();
		
		while(myRs.next()) {
			songs.add(myRs.getInt("id")+","+myRs.getString("name")+","+myRs.getString("genre")+","+myRs.getString("duration")+","+myRs.getString("bpm")+","+myRs.getString("key"));
		}
		System.out.println(songs);
		return songs;
	
	}
	//INSERT SONG INTO PLAYLIST
	private void insertSongPlaylist(String[] parts) throws SQLException {
		
		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		String query="INSERT INTO playlist_song (playlist_id,song_id)"+" VALUES(?,?)";
		java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
		preparedStmt.setString(1,parts[1]);
		preparedStmt.setString(2,parts[2]);
		preparedStmt.execute();    
	
	}
	//ORDER PLAYLIST BPM
	private ArrayList<String> orderPlaylistBPM(String[] parts) throws SQLException {
		
		ArrayList <String> songs = new ArrayList<String>();
		
		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		String query="SELECT * FROM playlist_song JOIN playlists ON playlist_id=playlists.id JOIN song ON song_id=song.id WHERE playlists.id = ? ORDER BY bpm ASC";
		java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
		preparedStmt.setString(1,parts[1]); 
		System.out.println("hey");
		
		ResultSet myRs=preparedStmt.executeQuery();    
		
		while(myRs.next()) {
			songs.add(myRs.getInt("id")+","+myRs.getString("song.name")+","+myRs.getString("genre")+","+myRs.getString("duration")+","+myRs.getString("bpm")+","+myRs.getString("key"));
		}
		
		return songs;
		
	}
	//ORDER PLAYLIST KEY
private ArrayList<String> orderPlaylistKey(String[] parts) throws SQLException {
		
		ArrayList <String> songs = new ArrayList<String>();
		
		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		String query="SELECT * FROM playlist_song "+
					 "JOIN playlists ON playlist_id=playlists.id "+
					 "JOIN song ON song_id=song.id "+
					 "JOIN key_c ON song.key = key_c.id "+
					 "WHERE playlists.id = ? "+
					 "ORDER BY value ASC";
		java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
		preparedStmt.setString(1,parts[1]);
		ResultSet myRs=preparedStmt.executeQuery();    
		
		while(myRs.next()) {
			songs.add(myRs.getInt("song.id")+","+myRs.getString("song.name")+","+myRs.getString("genre")+","+myRs.getString("duration")+","+myRs.getString("bpm")+","+myRs.getString("key"));
		}
		
		
		return songs;
		
	}
	//GET ARTISTS
	private ArrayList<String> getArtists() throws SQLException {
    		
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
	//GET ALBUMS
	private ArrayList<String> getAlbums() throws SQLException {
	    		
	    		ArrayList <String> albums = new ArrayList<String>(); //result set
			
	    		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
	    		
	    		Statement myStmt = myCon.createStatement();
	    		ResultSet myRs = myStmt.executeQuery("SELECT * FROM albuns JOIN artists WHERE artist_id = artists.id");
	    		while(myRs.next()) {
	    			System.out.println(myRs.getInt("id")+","+myRs.getString("albuns.name")+","+myRs.getInt("year")+myRs.getString("genre")+myRs.getString("artists.name"));
	    			albums.add(myRs.getInt("id")+","+myRs.getString("albuns.name")+","+myRs.getInt("year")+","+myRs.getString("genre")+","+myRs.getString("artists.name"));
	    		}
	    		
	    		return albums;
	    		
	}
	private ArrayList<String> getAlbumSongs(String[] parts) throws SQLException {
		
		ArrayList <String> album = new ArrayList<String>(); //result set
		
		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		
		String query="SELECT * FROM song WHERE album_id = ?";
		java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
		preparedStmt.setString(1,parts[1]);
		ResultSet myRs=preparedStmt.executeQuery();    
		
		while(myRs.next()) {
			album.add(myRs.getInt("id")+","+myRs.getString("song.name")+","+myRs.getString("song.genre")+","+myRs.getString("duration")+","+myRs.getString("key")+","+myRs.getString("bpm"));
		}
		
		return album;
	}
	//GET SONGS
	private ArrayList<String> getSongs() throws SQLException {
		
		ArrayList <String> songs = new ArrayList<String>(); //result set
	
		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		
		Statement myStmt = myCon.createStatement();
		ResultSet myRs = myStmt.executeQuery("SELECT * FROM song JOIN artists ON artist_id = artists.id JOIN albuns ON album_id = albuns.id");
		while(myRs.next()) {
			songs.add(myRs.getInt("id")+","+myRs.getString("song.name")+","+myRs.getString("song.genre")+","+myRs.getInt("bpm")+","+myRs.getString("key")+","+myRs.getString("duration")+","+myRs.getString("artists.name")+","+myRs.getString("albuns.name"));
		}
		
		return songs;
		
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
