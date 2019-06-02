package main.java;

import java.io.Serializable;

/**
 * @author Ramy Behnam, Mattias Jönsson, Markus Masalkovski
 * @version 2.0
 * @since 10/05-2019
 *
 */
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int RETURN=0, LOGIN = 1, LOGOUT = 2, REGISTER = 3, UPLOAD = 4, DOWNLOAD = 5, DELETE = 6, FILELIST = 7, UNREGISTER = 8, SEARCH = 9, USERFILELIST = 10, DOWNLOADUSERFILE = 11;
	private int type;
	private String username, email, password, directory, filename, search;
	private Object returnMessage;
	private byte[] file;

	/**
	 * First constructor
	 * @param type
	 * @param username
	 * @param email
	 * @param password
	 */
	public Message(int type, String username, String email, String password) {
		this.type=type;
		this.username=username;
		this.email=email;
		this.password=password; 
	}
	
	/**
	 * Second constructor with speciel functions for DOWNLOAD and DELETE
	 * @param type
	 * @param input1
	 * @param input2
	 */
	public Message(int type, String input1, String input2) {
		this.type=type;
		if(type==1) {
			this.username=input1;
			this.password=input2;
		}
		else if(type==5||type==6) {
			this.directory=input1;
			this.filename=input2;
		}
		else if(type==11) {
			this.filename=(String)input1;
			this.username=(String)input2;
		}
	}
	
	/**
	 * Third constructor 
	 * @param type
	 * @param directory
	 * @param file
	 * @param filename
	 */
	public Message(int type, String directory, byte[] file, String filename) {
		this.type=type;
		this.directory=directory;
		this.file=file;
		this.filename=filename;
	}

	/**
	 * Fourth constructor with special functions for FILELIST and SEARCH
	 * @param type
	 * @param input
	 */
	public Message(int type, Object input) {
		this.type=type;
		if(type==0)
			this.returnMessage=input;
		else if(type==7)
			this.directory=(String)input;
		else if(type==9)
			this.search=(String)input;
		else if(type==10)
			this.username=(String)input;
	}
	
	/**
	 * Fifth constructor
	 * @param type
	 */
	public Message(int type) {
		this.type=type;
	}

	/**
	 * @return type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @return the file
	 */
	public byte[] getFile() {
		return file;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * @return the message
	 */
	public Object getReturnMessage() {
		return returnMessage;
	}
	
	/**
	 * @return the search
	 */
	public String getSearch() {
		return search;
	}
}
