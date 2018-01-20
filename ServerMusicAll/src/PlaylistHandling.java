import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This classes handles all interactions with the playlists such as creating, adding song , getting all the playlists from specific user, etc
 *
 *
 */
public class PlaylistHandling {

		
		/**
		 * @param parts specific user
		 * @return how many playlists an user had
		 * @throws SQLException
		 */
		protected int countPlaylists(String[] parts) throws SQLException {
			
			Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
			String query="SELECT COUNT(*) AS count FROM playlists WHERE user_id = ?";
			java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
			preparedStmt.setString(1,parts[2]);
			ResultSet myRs = preparedStmt.executeQuery();
			
			if(myRs.next()) {
				return myRs.getInt("count");
			}
			return 0;
		}
		
		/**
		 * Creates a new playlist
		 * @param name of the new playlist and corresponding user
		 * @throws SQLException
		 */
		protected void createPlaylist(String[] parts) throws SQLException {
			Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
			String query="INSERT INTO playlists (id,name,user_id)"+" values(DEFAULT,?,?)";
			java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
			preparedStmt.setString(1,parts[1]);
			preparedStmt.setString(2,parts[2]);
			preparedStmt.execute();    
		}
	  
		/**
		 * @param parts specific user 
		 * @return an ArrayList with all the playlists that belong to a certain user
		 * @throws SQLException
		 */
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
		
		/**
		 * @param parts specific playlist id 
		 * @return an ArrayList with information relative to all the songs present in a specific playlist
		 * @throws SQLException
		 */
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
		
		/**
		 * inserts a song into a playlist
		 * @param parts specific playlist and specific song
		 * @throws SQLException
		 */
		protected void insertSongPlaylist(String[] parts) throws SQLException {
			
			Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
			String query="INSERT INTO playlist_song (playlist_id,song_id)"+" VALUES(?,?)";
			java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
			preparedStmt.setString(1,parts[1]);
			preparedStmt.setString(2,parts[2]);
			preparedStmt.execute();    
		
		}
		
		/**
		 * @param parts specific playlist id
		 * @return an Arraylist with a playlist ordered by bpm
		 * @throws SQLException
		 */
		protected ArrayList<String> orderPlaylistBPM(String[] parts) throws SQLException {
			
			ArrayList <String> songs = new ArrayList<String>();
			
			Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
			String query="SELECT * FROM playlist_song JOIN playlists ON playlist_id=playlists.id JOIN song ON song_id=song.id WHERE playlists.id = ? ORDER BY bpm ASC";
			java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
			preparedStmt.setString(1,parts[1]); 
			
			ResultSet myRs=preparedStmt.executeQuery();    
			
			while(myRs.next()) {
				songs.add(myRs.getInt("id")+","+myRs.getString("song.name")+","+myRs.getString("genre")+","+myRs.getString("duration")+","+myRs.getString("bpm")+","+myRs.getString("key"));
			}
			
			return songs;
			
		}
		
		/**
		 * @param parts specific playlist id
		 * @return an Arraylist with a playlist ordered by key
		 * @throws SQLException
		 */
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
