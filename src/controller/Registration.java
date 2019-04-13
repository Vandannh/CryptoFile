package controller;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.mindrot.jbcrypt.BCrypt;
import mssql.MSSQL;

/**
 * This class is used to register a user in the database.
 * 
 * @version 1.0
 * @since 2019-04-13
 * @author Mattias Jönsson
 *
 */
public class Registration {
	private String username,password,email;
	private MSSQL mssql;
	private String database 	= "YOUR_DATABASE"; 	// Edit this
	private String db_username 	= "YOUR_USERNAME";	// Edit this
	private String db_password 	= "YOUR_PASSWORD"; 	// Edit this
	private String hostname 	= "YOUR_HOSTNAME"; 	// Edit this
	private String port 		= "YOUR_PORT";

	/**
	 * Constructs a Registration-object containing the username, email-address and password
	 * of the user. Also connects to a MS SQL(SQL Server) database.
	 * 
	 * @param username the username of the user
	 * @param email the email address of the user
	 * @param password the password of the user
	 */
	public Registration(String username,String email, String password) {
		mssql = new MSSQL(database,db_username,db_password,hostname,port);
		this.username=username;
		this.password=password;
		this.email=email;
	}
	/**
	 * Register a user if there are no errors.
	 * 
	 * @return An array of strings with error messages if there is any error messages otherwise the 
	 * array is null 
	 */
	public String[] register() {
		String[] messages = new String[3];
		Object[][] validation = {validate(username, 1),validate(email, 2),validate(password, 3)};
		int errorCount = (int)validation[0][0]+(int)validation[1][0]+(int)validation[2][0];
		if(errorCount>0) {
			messages[0]=(String)validation[0][1];
			messages[1]=(String)validation[1][1];
			messages[2]=(String)validation[2][1];
		}
		else {
			mssql.insert("test", new String[] {"name","password","email"},new String[] {username,hashPassword(password),email});
			messages[0]="New user registered";
		}
		return messages;
	}
	/**
	 * Validates the users input.
	 * 
	 * @param input the users input
	 * @param type the type of input to be validated.
	 * <p> 
	 * 1 = Username<br>
	 * 2 = Email<br>
	 * 3 = Password<br>
	 * </p>
	 * @return an Object-array containing the number of errors and a string of the error message.   
	 */
	public Object[] validate(String input, int type) {
		int errorCount = 0;
		String inputError = "";
		switch(type) {
		case 1: 
			if(input.isEmpty()) {
				inputError = "Username is required";
				errorCount++;
			}
			else {
				if(!isValidUsername(input)) {
					inputError = "Username is not valid";
					errorCount++;
				}
				else if(usernameExists(input)) {
					inputError = "Username already exists";
					errorCount++;
				}
			}
			break;
		case 2:
			if(input.isEmpty()) {
				inputError = "Email address is required";
				errorCount++;
			}
			else {
				if(!isValidEmailAddress(input)) {
					inputError = "Email address is not valid";
					errorCount++;
				}
				else if(emailExists(input)) {
					inputError = "Email address already exists";
					errorCount++;
				}
			}
			break;
		case 3:
			if(input.isEmpty()) {
				inputError = "Password address is required";
				errorCount++;
			}
			else {
				if(!isValidPassword(input)) {
					inputError = "Password is not valid";
					errorCount++;
				}
			}	
		}
		return new Object[]{errorCount,inputError};
	}
	/**
	 * Check if the username already exists in the database.
	 * 
	 * @param input the username
	 * @return if the username exists in the database or not
	 */
	private boolean usernameExists(String input) {
		String usernames = mssql.select("test", new String[] {"name"}).replace("\t\t", "");
		for(String s : usernames.split("\n")) 
			if(s.toUpperCase().equals(input.toUpperCase())) 
				return true;
		return false;
	}
	/**
	 * Check if the email address already exists in the database
	 * 
	 * @param input the email address
	 * @return if the email address exists in the database or not
	 */
	private boolean emailExists(String input) {
		String emails = mssql.select("test", new String[] {"email"}).trim();
		for(String s : emails.split("\n"))
			if(s.toUpperCase().equals(input.toUpperCase())) 
				return true;
		return false;
	}

	/**
	 * Hashes the users password using BCrypt hashing
	 * 
	 * @param input the password
	 * @return the hashed password
	 */
	private String hashPassword(String input) {
		return BCrypt.hashpw(input, BCrypt.gensalt(12)); 
	}
	/**
	 * Check if the email address is valid
	 * 
	 * @param input the email address
	 * @return if the email address is valid or not
	 */
	private boolean isValidEmailAddress(String input) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(input);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}
	/**
	 * Checks if the username only contains a-z, A-Z, 0-9 or _ and the length of the username is greater 
	 * than 6 characters.
	 * 
	 * @param input the username
	 * @return if the username is valid or not
	 */
	private boolean isValidUsername(String input) {
		String regex = "[a-zA-Z0-9_]+";
		return input.matches(regex);
	}
	/**
	 * Checks if the passwords length is greater than 7 characters and if the password contains both 
	 * numbers and letters. 
	 * 
	 * @param input the password
	 * @return if the password is valid or not
	 */
	private boolean isValidPassword(String input) {
		if(input.length()<8) return false;
		int charCount = 0;
		int numCount = 0;
		for (int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			if (is_Numeric(ch)) 
				numCount++;
			else if (is_Letter(ch)) 
				charCount++;
			else 
				return false;
		}
		return (charCount>0&&numCount>0);
	}
	/**
	 * Checks if the character is a letter
	 * 
	 * @param ch the character
	 * @return if the character is a letter or not
	 */
	private boolean is_Letter(char ch) {
		ch = Character.toUpperCase(ch);
		return (ch >= 'A' && ch <= 'Z');
	}
	/**
	 * Checks if the character is a number
	 * 
	 * @param ch the character
	 * @return if the character is a number or not
	 */
	private boolean is_Numeric(char ch) {
		return (ch >= '0' && ch <= '9');
	}
}
