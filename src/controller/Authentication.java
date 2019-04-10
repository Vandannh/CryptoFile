package controller;

import org.mindrot.jbcrypt.BCrypt;
import mssql.MSSQL;

public class Authentication {
	private MSSQL mssql;
	private String database = "Testing"; // Edit this
	private String username = "Mattias@cryptofiletesting"; // Edit this
	private String password = "Hejsan123"; // Edit this
	private String ipAddress = "cryptofiletesting.database.windows.net"; //Edit this
	private String port = "1433"; // Usual port for SQL Server
	
	public Authentication() {
		mssql = new MSSQL(database,username,password,ipAddress,port);
	}
	
	public boolean getAuthentication(String username, String password) {
		return BCrypt.checkpw(password, getPassword(username));
	}

	private String getPassword(String username) {
		return mssql.select("test", new String[] {"password"}, "username='"+username+"'").trim();
	}
	
}