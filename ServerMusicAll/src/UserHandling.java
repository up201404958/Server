import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UserHandling{


	//CHECK REGISTER
    protected int checkRegister(String[] parts) throws SQLException {
    		
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
    	protected int checkValidUser(String name) throws SQLException {
    	
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
    protected int checkLogin(String[] parts) throws SQLException {
    		
    	 	Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
    		Statement myStmt = myCon.createStatement();
		ResultSet myRs = myStmt.executeQuery("SELECT * FROM users");
		
		while(myRs.next()) {
			if( parts[1].equals(myRs.getString("name")) && parts[2].equals(myRs.getString("password")))
					return 1;
		}
		
		return 0;
	}
    protected void changePass(String[] parts) throws SQLException {
		
		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		String query="UPDATE users SET password = ? WHERE name = ?";
		java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
		preparedStmt.setString(1,parts[1]);
		preparedStmt.setString(2,parts[2]);
		preparedStmt.execute();
		
	}
    protected ArrayList<String> getUserSongs(String[] parts) throws SQLException {
		
		ArrayList <String> songs = new ArrayList<String>(); //result set

		Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
		String query="SELECT * FROM user_songs "
				  + "JOIN song ON user_songs.song_id = song.id "
				  + "JOIN albuns ON song.album_id=albuns.id "
				  + "JOIN artists ON song.artist_id=artists.id "
				  + "WHERE user_id = ?";
		java.sql.PreparedStatement preparedStmt = myCon.prepareStatement(query);
		preparedStmt.setString(1,parts[1]);
		ResultSet myRs = preparedStmt.executeQuery();
		
		while(myRs.next()) {
			songs.add(myRs.getInt("song.id")+","+myRs.getString("song.name")+","+myRs.getString("albuns.name")+","+myRs.getString("artists.name")+","+myRs.getString("genre")+","+myRs.getString("duration")+","+myRs.getString("bpm")+","+myRs.getString("key"));
		}
		System.out.println(songs);
		return songs;
	}

}
