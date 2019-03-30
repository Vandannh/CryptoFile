package controller;

import java.security.NoSuchAlgorithmException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.mindrot.jbcrypt.BCrypt;
import database.MySQL;

public class Registration {
	private String username,password,email;
	private MySQL mysql = new MySQL("java_test","root","mffmff11","127.0.0.1","3306");

	public Registration(String username, String password,String email) throws NoSuchAlgorithmException {
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
