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
		String user = "REGS lpro lpro"; //brand new user
		String parts[] = user.split(" ");
		test.checkRegister(parts);
		int after = test.numberOfUsers();
		assertEquals(before+1,after);
		
	}
	/**
	 * Test method for testing Login
	 * This tests a valid(registered) and an invalid(not unregistered) login
	 * @throws SQLException 
	 */
	@Test
	public void testLogin() throws SQLException {
		UserHandling test = new UserHandling();
		String user = "LOGN teste 12345"; //registered user
		String anon = "LOGN nowhere man"; //unregistered user
		String parts[] = user.split(" ");
		String strap[] = anon.split(" ");
		int ret = test.checkLogin(parts);
		int ter = test.checkLogin(strap);
		assertEquals(ret,1);
		assertEquals(ter,0);
	}


}
