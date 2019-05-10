package main.java.database;

import java.util.ArrayList;
import java.util.Random;

import javax.mail.internet.*;
import org.mindrot.jbcrypt.BCrypt;
import mssql.MSSQL;
import test2.ConnectionStrings;

/**
 * This class is used to register a user in the database.
 * 
 * @version 1.0
 * @since 2019-04-14
 * @author Mattias J�nsson
 *
 */
public class Registration {
	private String username,password,email;
	private MSSQL mssql;
	private final String connectionString = ConnectionStrings.connectionString;

	/**
	 * Constructs a Registration-object containing the username, email-address and password
	 * of the user. Also connects to a MS SQL(SQL Server) database.
	 * 
	 * @param username the username of the user
	 * @param email the email address of the user
	 * @param password the password of the user
	 */
	public Registration(String username,String email, String password) {
		mssql = new MSSQL(connectionString);
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
	public ArrayList<String> register() {
		ArrayList<String> messages = new ArrayList<String>();
		Object[][] validation = {validate(username, 1),validate(email, 2),validate(password, 3)};
		int errorCount = (int)validation[0][0]+(int)validation[1][0]+(int)validation[2][0];
		if(errorCount>0) {
			messages.add((String)validation[0][1]);
			messages.add((String)validation[1][1]);
			messages.add((String)validation[2][1]);
			deleteEmptyElement(messages);
		}
		else {
			String code = generateUserCode();
			mssql.insert("Users", new String[] {"username","password","email","user_code"},new String[] {username,hashPassword(password),email,code});
			String id = mssql.select("Users", new String[] {"id"}, "username='"+username+"' AND user_code ='"+code+"'").replace("\t\t", "").trim();
			mssql.insert("Directory", new String[] {"name","user_id","type"}, new Object[] {id,Integer.parseInt(id),"Directory"});
			mssql.insert("Directory", new String[] {"name","user_id","type"}, new Object[] {"private",Integer.parseInt(id),"Directory"});
			mssql.insert("Directory", new String[] {"name","user_id","type"}, new Object[] {"public",Integer.parseInt(id),"Directory"});
			messages.add(id);
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
			if(input.isEmpty())
				inputError = "Username is required";
			else {
				if (input.length()>25)
					inputError = "Username is to long. Maximum of 25 characters.";
				else if(!isValidUsername(input))
					inputError = "Username is not valid";
				else if(usernameExists(input))
					inputError = "Username already exists";
			}
			break;
		case 2:
			if(input.isEmpty())
				inputError = "Email address is required";
			else {
				if(!isValidEmailAddress(input))
					inputError = "Email address is not valid";
				else if(emailExists(input))
					inputError = "Email address already exists";
			}
			break;
		case 3:
			if(input.isEmpty())
				inputError = "Password is required";
			else 
				if(!isValidPassword(input))
					inputError = "Password is not valid";
		}
		if(!inputError.isEmpty()) 
			errorCount++;
		return new Object[]{errorCount,inputError};
	}
	/**
	 * Check if the username already exists in the database.
	 * 
	 * @param input the username
	 * @return if the username exists in the database or not
	 */
	private boolean usernameExists(String input) {
		String usernames = mssql.select("Users", new String[] {"username"}).replace("\t\t", "");
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
		String emails = mssql.select("Users", new String[] {"username"}).replace("\t\t", "");
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
	 * Checks if the username only contains a-z, A-Z, 0-9 
	 * 
	 * @param input the username
	 * @return if the username is valid or not
	 */
	private boolean isValidUsername(String input) {
		String regex = "[a-zA-Z0-9]+";
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
		if(input.length()<8) 
			return false;
		int charCount=0;
		int numCount=0;
		for (int i=0;i<input.length();i++) {
			char ch = input.charAt(i);
			if (isNumeric(ch)) 
				numCount++;
			else if (isLetter(ch)) 
				charCount++;
		}
		return charCount>0&&numCount>0;
	}
	/**
	 * Checks if the character is a letter
	 * 
	 * @param ch the character
	 * @return if the character is a letter or not
	 */
	private boolean isLetter(char ch) {
		ch = Character.toUpperCase(ch);
		return ch>='A'&&ch<='Z';
	}
	/**
	 * Checks if the character is a number
	 * 
	 * @param ch the character
	 * @return if the character is a number or not
	 */
	private boolean isNumeric(char ch) {
		return ch>='0'&&ch<='9';
	}
	
	/**
	 * Generate a code for the user 
	 * 
	 * @return a hashed number
	 */
	private String generateUserCode() {
		return BCrypt.hashpw(Integer.toString(new Random().nextInt(1000000000)), BCrypt.gensalt(12));
	}
	
	/**
	 * Deletes all empty elements from a ArrayList
	 * 
	 * @param messages the ArrayList in which the elements will be deleted from
	 */
	private void deleteEmptyElement(ArrayList<String> messages) {
		ArrayList<String> toRemove = new ArrayList<String>();
		for (String str : messages) {
		    if (str.isEmpty()) {
		        toRemove.add(str);
		    }
		}
		messages.removeAll(toRemove);
	}
}