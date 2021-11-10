package logs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadLog {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ReadLog.class);
	static logUtil logUtil = new logUtil();
	
	private void getLogs(String filePath) {
		
		log.debug("Enter getLogs()");
		JSONParser jsonParser = new JSONParser();
		JSONArray logJsonArray = new JSONArray();
		JSONObject logObj;
		Object obj;
		String line;
		
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath)))
        {
			while((line=bufferedReader.readLine()) != null) {
	            obj = jsonParser.parse(line);
	 
	            logObj = (JSONObject) obj;
	            logJsonArray.add(logObj);
			}
			log.info("Log file read task complete");
			
			try {
				filterLogs(logJsonArray);
				logUtil.getDBData();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
 
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
		log.debug("Exit getLogs()");
	}

	private void filterLogs(JSONArray logJsonArray) throws SQLException {
		log.debug("Enter filterLogs()");
		JSONObject object1, object2;
		long time1, time2;
		double diff;
		String id;
		
		for(int i=0; i<logJsonArray.size(); i++) {
			object1 = (JSONObject)logJsonArray.get(i);
			id = (String) object1.get("id");
			
			for(int j=i+1; j<logJsonArray.size(); j++) {
				object2 = (JSONObject)logJsonArray.get(j);
				
				if (id.equals(object2.get("id"))) {
					time1 = (long) object1.get("timestamp");
					time2 = (long) object2.get("timestamp");
					
					diff = time1 - time2;
					
					dBEntry(diff, id);
				}
			}
		}
		log.debug("Exit filterLogs()");
	}

	private void dBEntry(double diff, String id) throws SQLException {
		log.debug("Enter dBEntry()");
		Connection connection = ConnectDB.getDatabase();
		
		String alert = diff <4 ? "true" : "false";
		
		Statement statement = connection.createStatement();
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS LOGTABLE (eventId VARCHAR(20), evendDuration INT, alert VARCHAR(5));");
		statement.execute("INSERT INTO LOGTABLE VALUES ('"+id+"','"+Math.abs(diff)+"','"+alert+"')");
		log.debug("Values inserted are : " + id + Math.abs(diff) + alert);
		
		statement.close();
		connection.close();
		log.debug("Exit dBEntry()");
	}
	
	public static void main(String s[]) {
		
		BasicConfigurator.configure();
		log.setLevel(Level.INFO);
		
		ReadLog readLog = new ReadLog();
		String filePath = logUtil.getPath();
		
		readLog.getLogs(filePath);
	}
}
