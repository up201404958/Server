import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

/**
 * JUnit Tests of Database Interactions regarding users
 *
 */
public class UserHandlingtest {
	/**
	 * Test method for testing Registration 
	 * @throws SQLException 
	 */
	@Test
	public void testRegister() throws SQLException {
		
		UserHandling test = new UserHandling();
		int before = test.numberOfUsers();
		String user = "REGS random random"; //new user needs to be inserted for testing each time
		String parts[] = user.split(" ");
		test.checkRegister(parts);
		int after = test.numberOfUsers();
		assertEquals(before+1,after);
		
	}
	/**
	 * Test method for testing Login
	 * This tests a valid(registered) login
	 * @throws SQLException 
	 */
	@Test
	public void testLogin() throws SQLException {
		UserHandling test = new UserHandling();
		String user = "LOGN teste 12345"; //registered user
		String parts[] = user.split(" ");
		int ret = test.checkLogin(parts);
		assertEquals(ret,1);
	}
	/**
	 * Test method for testing Login
	 * This tests a an invalid(not unregistered) login
	 * @throws SQLException 
	 */
	@Test
	public void testInvalidLogin() throws SQLException {
		UserHandling test = new UserHandling();
		String anon = "LOGN nowhere man"; //unregistered user
		String strap[] = anon.split(" ");
		int ter = test.checkLogin(strap);
		assertEquals(ter,0);
	}
	/**
	 * Test method for duplicated user song entry
	 * This tests if a user tries to download a music he already has
	 * User already has song with the id = 1
	 * @throws SQLException 
	 */
	@Test
	public void testDuplicate() throws SQLException {
		UserHandling test = new UserHandling();
		String nono = "INS 1 User";
		String parts[] = nono.split(" ");
		int ret = test.setUserSongs(parts);
		assertEquals(ret,0);
	}
	/**
	 * Test method for changing user password
	 * Create a new user with certain pass
	 * Change Password
	 * Try login with old pass
	 * Try login with new pass
	 * @throws SQLException 
	 */
	@Test
	public void changePass() throws SQLException {
		UserHandling test = new UserHandling();
		String nono = "REGS paul maccartney";
		String parts[] = nono.split(" ");
		test.checkRegister(parts);
		String non = "CHNG isdead paul";
		String part[] = non.split(" ");
		test.changePass(part);
		String old = "LOGN paul maccartney";
		String par[] = old.split(" ");
		String now = "LOGN paul isdead";
		String rap[] = now.split(" ");
		int ret = test.checkLogin(par);
		int ter = test.checkLogin(rap);
		assertEquals(ret,0);
		assertEquals(ter,1);
		
	}


}
