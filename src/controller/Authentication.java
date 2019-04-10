package controller;

import org.mindrot.jbcrypt.BCrypt;
import mssql.MSSQL;

public class Authentication {
	private MSSQL mssql;
	private String database = "YOUR_DATABASE"; 	// Edit this
	private String username = "YOUR_USERNAME"; 	// Edit this
	private String password = "YOUR_PASSWORD"; 	// Edit this
	private String hostname = "YOUR_HOSTNAME"; 	// Edit this
	private String port 	= "YOUR_PORT"; 		// Edit this
	
	public Authentication() {
		mssql = new MSSQL(database,username,password,hostname,port);
	}
	
	public boolean getAuthentication(String username, String password) {
		String p1 = getPassword(username);
		System.out.println(password + " - " + p1);
		return BCrypt.checkpw(password, p1);
	}

	private String getPassword(String username) {
		return mssql.select("test", new String[] {"password"}, "name='"+username+"'").trim();
	}
	
}