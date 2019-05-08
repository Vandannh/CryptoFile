package main.java;

import java.io.File;
import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int LOGIN = 1, LOGOUT = 2, REGISTER = 3, UPLOAD = 4, DOWNLOAD = 5;
	private int type;
	private String username, email, password, directory;
	private File file;

	public Message(int type, String username, String email, String password) {
		this.type=type;
		this.username=username;
		this.email=email;
		this.password=password;
	}
	
	public Message(int type, String username, String password) {
		this.type=type;
		this.username=username;
		this.password=password;
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

	public File getFile() {
		return file;
	}
}
