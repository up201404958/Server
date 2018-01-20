import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Test;

/**
 * 
 */

/**
 * JUnit Tests of Database Interactions regarding playlists
 *
 */
public class PlaylistHandlingtest {

	protected PlaylistHandling test;
	/**
	 * Test method for Creating a New Playlist
	 * @throws SQLException 
	 */
	
	@Test
	public void testCreatePlaylist() throws SQLException {
		test = new PlaylistHandling();
		String toSend = "PLST villans teste";
		String []parts = toSend.split(" ");
		int before = test.countPlaylists(parts);
		test.createPlaylist(parts);
		int after = test.countPlaylists(parts);
		assertEquals(before+1,after);
	
	}

	/**
	 * Test method for Inserting a Song into a playlist
	 * @throws SQLException 
	 */
	@Test
	public void testInsertSong() throws SQLException {
		
		test = new PlaylistHandling();
		ArrayList <String> before = new ArrayList<String>();
		ArrayList <String> after = new ArrayList<String>();
		String part = "PLST 1";
		String []parts = part.split(" ");
		before = test.getPlaylistSongs(parts);
		String trap = "INS 1 10";
		String []partz = trap.split(" ");
		test.insertSongPlaylist(partz);
		after = test.getPlaylistSongs(parts);
		assertEquals(after.size(),before.size()+1);
		
		
	}


}
