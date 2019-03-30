package controller;

import java.security.NoSuchAlgorithmException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.mindrot.jbcrypt.BCrypt;
import database.MySQL;

public class Registration {
	private String username,password,email;
	private MySQL mysql;
	private String database = "YOUR_DATABASE"; // Edit this
	private String db_username = "YOUR_USERNAME"; // Edit this
	private String db_password = "YOUR_PASSWORD"; // Edit this
	private String ipAddress = "127.0.0.1"; // localhost
	private String port = "3306"; // Usual port for mysql

	public Registration(String username, String password,String email) throws NoSuchAlgorithmException {
		mysql = new MySQL(database,db_username,db_password,ipAddress,port);
		this.username=username;
		this.password=password;
		this.email=email;
	}
	public static void main(String[] args) throws NoSuchAlgorithmException {
		Registration r = new Registration("Matte5","12345","matte1@lodde.se");
		System.out.println(r.register());
	}
	public boolean register() {
		if(isValidUsername()) {
			if(!usernameExists()) {
				if(isValidEmailAddress()) {
					if(!emailExists()){
						mysql.insert("projektarbete_testning", new String[] {"username","password","email"},new String[] {username,encryptPassword(),email});
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
		String usernames = mysql.select("projektarbete_testning", new String[] {"username"}, "1").trim();
		for(String s : usernames.split("\n")) {
			if(s.toUpperCase().equals(username.toUpperCase())) return true;
		}
		return false;
	}
	private boolean emailExists() {
		String emails = mysql.select("projektarbete_testning", new String[] {"email"}, "1").trim();
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
