import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * This classes handles all interactions that require to get Database data such as songs,albums,artists
 *
 */
public class DatabaseHandling {

		
		/**
		 * @return information about all the artists available in the database
		 * @throws SQLException
		 */
		protected ArrayList<String> getArtists() throws SQLException {
	    		
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
		/**
		 * @return an ArrayList with information every album available in the database
		 * @throws SQLException
		 */
		protected ArrayList<String> getAlbums() throws SQLException {
		    		
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
		/**
		 * @param parts - specific album id
		 * @return songs that are part of a certain album 
		 * @throws SQLException
		 */
		protected ArrayList<String> getAlbumSongs(String[] parts) throws SQLException {
			
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
		/**
		 * @return an ArrayList with information about every song available in the database
		 * @throws SQLException
		 */
		protected ArrayList<String> getSongs() throws SQLException {
			
			ArrayList <String> songs = new ArrayList<String>(); //result set
		
			Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
			
			Statement myStmt = myCon.createStatement();
			ResultSet myRs = myStmt.executeQuery("SELECT * FROM song JOIN artists ON artist_id = artists.id JOIN albuns ON album_id = albuns.id");
			while(myRs.next()) {
				songs.add(myRs.getInt("id")+","+myRs.getString("song.name")+","+myRs.getString("song.genre")+","+myRs.getInt("bpm")+","+myRs.getString("key")+","+myRs.getString("duration")+","+myRs.getString("artists.name")+","+myRs.getString("albuns.name"));
			}
			
			return songs;
		}
}
