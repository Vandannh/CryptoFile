package controller;

import org.mindrot.jbcrypt.BCrypt;
import database.MySQL;

public class Authentication {
	private MySQL mysql;
	private String database = "Testing"; // Edit this
	private String username = "YOUR_USERNAME"; // Edit this
	private String password = "YOUR_PASSWORD"; // Edit this
	private String ipAddress = "127.0.0.1"; // localhost
	private String port = "3306"; // Usual port for mysql
	
	public Authentication() {
		mysql = new MySQL(database,username,password,ipAddress,port);
	}
	
	public boolean getAuthentication(String username, String password) {
		return BCrypt.checkpw(password, getPassword(username));
	}

	private String getPassword(String username) {
		return mysql.select("projektarbete_testning", new String[] {"password"}, "username='"+username+"'").trim();
	}
	
}