import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlaylistHandling {

	//CREATE PLAYLIST
		protected void createPlaylist(String[] parts) throws SQLException {
			
			Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
			String query="INSERT INTO playlists (id,name,user_id)"+" values(DEFAULT,?,?)";
			java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
			preparedStmt.setString(1,parts[1]);
			preparedStmt.setString(2,parts[2]);
			preparedStmt.execute();    
		}
	    //GET PLAYLISTS
		protected ArrayList<String> getPlaylist(String[] parts) throws SQLException {
			
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
		protected ArrayList<String> getPlaylistSongs(String[] parts) throws SQLException {
			
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
		protected void insertSongPlaylist(String[] parts) throws SQLException {
			
			Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
			String query="INSERT INTO playlist_song (playlist_id,song_id)"+" VALUES(?,?)";
			java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
			preparedStmt.setString(1,parts[1]);
			preparedStmt.setString(2,parts[2]);
			preparedStmt.execute();    
		
		}
		//ORDER PLAYLIST BPM
		protected ArrayList<String> orderPlaylistBPM(String[] parts) throws SQLException {
			
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
		protected ArrayList<String> orderPlaylistKey(String[] parts) throws SQLException {
			
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
}
