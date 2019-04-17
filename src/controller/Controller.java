package controller;

import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import azure.AzureFileShareIO;
import database.*;
import mssql.MSSQL;

/**
 * Is used to control the events in the application 
 * 
 * @version 1.0
 * @since 2019-04-17
 * @author Mattias Jönsson & Robin Andersson
 *
 */
public class Controller {
	private AzureFileShareIO azureFileShareIO = new AzureFileShareIO();
	private Authentication authentication = new Authentication();
	private Registration registration;
	private String userid;
	private MSSQL mssql;
	private File file;
	private final String connectionString = "YOUR_CONNECTION_STRING";

	/**
	 * Connects to a MS SQL (SQL Server) database
	 */
	public Controller() {
		mssql = new MSSQL(connectionString);
	}
	/**
	 * Tries to log in the user
	 * 
	 * @param username the username the user is trying to log in with
	 * @param password the password the user is trying to log in with
	 * @return if the user can log in or not
	 */
	public boolean login(String username, String password) {
		if(authentication.getAuthentication(username, password)) {
			azureFileShareIO.connect();
			userid=mssql.select("users", new String[] {"id"}, "username='"+username+"'").replace("\t\t", "").trim();
			return true;
		}
		else
			return false;
	}
	/**
	 * Tries to register the user
	 * 
	 * @param username the username the user is trying to register
	 * @param email the email the user is trying to register 
	 * @param password the password the user is trying to register
	 * @return an ArrayList of the error messages if the user fail to register of 
	 * a message telling the user it was created
	 */
	public ArrayList<String> register(String username, String email, String password) {
		registration = new Registration(username, email, password);
		ArrayList<String> messages = registration.register();
		if(isNumeric(messages.get(0).toCharArray()[0])) {
			azureFileShareIO.connect();
			azureFileShareIO.createDirectory(messages.get(0)+"_test");
			ArrayList<String> message = new ArrayList<String>();
			message.add("User created");
			return message;
		}
		else
			return messages;
	}
	/**
	 * Gets a file and tries to upload it
	 * 
	 * @return a String telling the user of the success of the upload 
	 */
	public String uploadFile() {
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)
			file = chooser.getSelectedFile();
		if(file!=null) {
			azureFileShareIO.upload(userid+"_test",file);
			mssql.insert("directory", new String[] {"name","type","user_id","parent_id"}, new String[] {file.getName(),"file",userid,userid});
			return file.getName()+" has been uploaded";
		}
		return "";
	}
	/**
	 * Tries to download a file from Azure File Share
	 * 
	 * @return a String telling the user of the success of the download
	 */
	public String downloadFile(){
		String filename = JOptionPane.showInputDialog("Write file to download.(Including the file extension)");
		if(azureFileShareIO.download(userid,filename))
			return filename+" has been downloaded";
		return "An error occured. Download failed";
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

}
