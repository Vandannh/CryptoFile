package controller;

import database.MySQL;
import org.mindrot.jbcrypt.BCrypt;

public class Authentication {
	private MySQL mysql = new MySQL("java_test","root","mffmff11","127.0.0.1","3306");
	
	public boolean getAuthentication(String username, String password) {
		return BCrypt.checkpw(password, getPassword(username));
	}

	private String getPassword(String username) {
		return mysql.select("projektarbete_testning", new String[] {"password"}, "username='"+username+"'").trim();
	}
	
}