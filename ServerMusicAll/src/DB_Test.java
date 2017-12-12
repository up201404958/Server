import java.sql.*;

public class DB_Test {
	public static void main(String[] args) {
		try {
				//get connection to database
				Connection myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?autoReconnect=true&useSSL=false", "root", "lespaul59");
				//print a statement
				Statement myStmt = myCon.createStatement();
				//execute query
				ResultSet myRs = myStmt.executeQuery("SELECT * FROM song");
				//process results
				while(myRs.next()) {
					System.out.println(myRs.getInt("id") + ","+ myRs.getString("name"));
				}
		}
		catch( Exception exc) {
			exc.getStackTrace();
		}
	}

}



