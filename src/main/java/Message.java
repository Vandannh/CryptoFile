package main.java;

import java.io.File;
import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int RETURN=0, LOGIN = 1, LOGOUT = 2, REGISTER = 3, UPLOAD = 4, DOWNLOAD = 5, DELETE = 6;
	private int type;
	private String username, email, password, directory, filename, returnMessage;
	private byte[] file;

	public Message(int type, String username, String email, String password) {
		this.type=type;
		this.username=username;
		this.email=email;
		this.password=password; 
	}
	
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
	}
	
	public Message(int type, String directory, byte[] file, String filename) {
		this.type=type;
		this.directory=directory;
		this.file=file;
		this.filename=filename;
	}

	public Message(int type, String returnMessage) {
		this.type=type;
		this.returnMessage=returnMessage;
	}

	public int getType() {
		return type;
	}
	
	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getDirectory() {
		return directory;
	}

	public byte[] getFile() {
		return file;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getReturnMessage() {
		return returnMessage;
	}
}
