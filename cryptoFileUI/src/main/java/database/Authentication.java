package main.java.database;

import org.mindrot.jbcrypt.BCrypt;
import mssql.MSSQL;

/**
 * The class is used to authenticate the user
 * 
 * @version 1.0
 * @since 2019-04-13
<<<<<<< HEAD
 * @author Mattias J�nsson
=======
 * @author Mattias J�nsson
>>>>>>> refs/heads/feature/SQLInjections
 *
 */
public class Authentication {
	private MSSQL mssql;
	private final String connectionString = "jdbc:sqlserver://cryptofiletesting.database.windows.net:1433;"
			   + "database=Testing;user=db_writer_reader@cryptofiletesting;password=CryptoFileHasACoolPassword1;"
			   + "encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;"
			   + "loginTimeout=30;"; // Edit this
	/**
	 * Sets up a connection to the database.
	 */
	public Authentication() {
		mssql = new MSSQL(connectionString);
	}
	
	/**
	 * Check if the password the user is tying to login in with matches to the users password 
	 * in the database. 
	 * 
	 * @param username the username of the user trying to login.
	 * @param password the password the user tying to login with
	 * @return if the user is authenticated
	 */
	public boolean getAuthentication(String username, String password) {
		String hashedPassword = getPassword(username);
		if(hashedPassword.isEmpty()) 
			return false;
		return BCrypt.checkpw(password, hashedPassword);
	}

	/**
	 * Gets the users password from the database.
	 * 
	 * @param username the username of the user trying to login
	 * @return the password of the user
	 */
	private String getPassword(String username) {
		return mssql.select("Users", new String[] {"password"}, "username='"+username+"'").replace("\t\t", "").trim();
	}
}