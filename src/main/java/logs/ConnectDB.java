package logs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class ConnectDB {
	protected static Connection getDatabase() {
		Connection connection = null;
		ResultSet result = null;
		
		try {
	         Class.forName("org.hsqldb.jdbc.JDBCDriver");
	         connection = DriverManager.getConnection("jdbc:hsqldb:C:\\Users\\Public\\db", "sa", "");
	         
	      }  catch (Exception e) {
	         e.printStackTrace(System.out);
	      }
		return connection;
	}
}
