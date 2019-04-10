package controller;

import java.security.NoSuchAlgorithmException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.mindrot.jbcrypt.BCrypt;
import mssql.MSSQL;

public class Registration {
	private String username,password,email;
	private MSSQL mssql;
	private String database = "Testing"; // Edit this
	private String db_username = "Mattias@cryptofiletesting"; // Edit this
	private String db_password = "Hejsan123"; // Edit this
	private String hostname = "cryptofiletesting.database.windows.net"; //Edit this
	private String port = "1433"; // Usual port for SQL Server

	public Registration(String username, String password,String email) throws NoSuchAlgorithmException {
		mssql = new MSSQL(database,db_username,db_password,hostname,port);
		this.username=username;
		this.password=password;
		this.email=email;
	}
	public static void main(String[] args) throws NoSuchAlgorithmException {
		Registration r = new Registration("user1","password","email@emails.se");
		System.out.println(r.register());
	}
	public boolean register() {
		if(isValidUsername()) {
			if(!usernameExists()) {
				if(isValidEmailAddress()) {
					if(!emailExists()){
						mssql.insert("Users", new String[] {"username","password","email"},new String[] {username,encryptPassword(),email});
					}
					else {
						System.out.println("Email already exists");
						return false;
					}
				}
				else {
					System.out.println("Invalid email");
					return false;
				}
			}
			else {
				System.out.println("Username already exists");
				return false;
			}
		}

		return true;
	}
	private boolean usernameExists() {
		String usernames = mssql.select("Users", new String[] {"username"}, "1=1").trim();
		for(String s : usernames.split("\n")) {
			if(s.toUpperCase().equals(username.toUpperCase())) return true;
		}
		return false;
	}
	private boolean emailExists() {
		String emails = mssql.select("Users", new String[] {"email"}, "1=1").trim();
		for(String s : emails.split("\n")) {
			if(s.toUpperCase().equals(email.toUpperCase())) return true;
		}
		return false;
	}
	private String encryptPassword() {
		return BCrypt.hashpw(password, BCrypt.gensalt(12)); 
	}
	private boolean isValidEmailAddress() {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}
	private boolean isValidUsername() {
		return true;
	}
}
