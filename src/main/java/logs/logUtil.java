package logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class logUtil {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ReadLog.class);
	
	protected String getPath() {
		log.debug("Enter getPath()");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		
		String filePath = "";
		System.out.println("Enter path of file to be read : ");
		try {
			 filePath = bufferedReader.readLine();
			 log.debug("File path received : " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug("Exit getPath()");
		return filePath;
	}
	
	protected void getDBData() throws SQLException {
		log.debug("Enter getDBData()");
		Connection connection = ConnectDB.getDatabase();
		Statement statement = connection.createStatement();
		
		log.info("Selecting values from table");
		ResultSet result = statement.executeQuery("select * from LOGTABLE");
		while(result.next()) { 
			log.info(result.getString("eventId") + " | " + result.getInt("evendDuration") + " | " + result.getString("alert")); 
		}
		statement.close();
		connection.close();
		log.debug("Exit getDBData()");
	}
}
