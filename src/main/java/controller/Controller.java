package main.java.controller;

import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import main.java.azure.AzureFileShareIO;
import main.java.database.*;
import mssql.MSSQL;
import main.java.session.*;

/**
 * Is used to control the events in the application 
 * 
 * @version 1.0
 * @since 2019-04-17
 * @author Mattias J�nsson & Robin Andersson
 *
 */
public class Controller {
	private AzureFileShareIO azureFileShareIO = new AzureFileShareIO();
	private Authentication authentication = new Authentication();
	private ActiveSessions activeSession = new ActiveSessions();
	private Registration registration;
	private String userid;
	private MSSQL mssql;
	private File file;
	private Session session;
	private final String connectionString = "YOUR_CONNECTION_STRING"; // Edit this

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
		username = stripSlashes(escapeCharacters(username)).trim();
		password = stripSlashes(escapeCharacters(password)).trim();
		if(authentication.getAuthentication(username, password)) {
			userid=mssql.select("users", new String[] {"id"}, "username='"+username+"'").replace("\t\t", "").trim();
			azureFileShareIO.connect("user"+userid);
			session = new Session(userid);
			activeSession.addSession(session);
			new AutomaticLogout().start();
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
		username = stripSlashes(escapeCharacters(username)).trim();
		password = stripSlashes(escapeCharacters(password)).trim();
		email = stripSlashes(escapeCharacters(email)).trim();
		registration = new Registration(username, email, password);
		ArrayList<String> messages = registration.register();
		if(isNumeric(messages.get(0).toCharArray()[0])) {
			azureFileShareIO.connect("user"+messages.get(0));
			azureFileShareIO.createDirectoryInAzure("public");
			azureFileShareIO.createDirectoryInAzure("private");
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
			String choosenDirectory = chooseDirectory().toLowerCase();
			String directoryId = mssql.select("directory", new String[] {"id"}, "name='"+choosenDirectory+"'");
			azureFileShareIO.upload(choosenDirectory,file);
			mssql.insert("directory", new String[] {"name","type","user_id","parent_id"}, new String[] {file.getName(),"file",userid,directoryId});
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
		String directory = chooseDirectory().toLowerCase();
		String filename = JOptionPane.showInputDialog("Write file to download.(Including the file extension)");
		if(azureFileShareIO.download(directory,filename))
			return filename+" has been downloaded";
		return "An error occured. Download failed";
	}
	
	public String deleteFile() {
		String directory = chooseDirectory().toLowerCase();
		String filename = JOptionPane.showInputDialog("Write file to Delete.(Including the file extension)");
		if(azureFileShareIO.deleteFile(userid,directory,filename))
			return filename+" has been deleted";
		return "An error occured. Delete failed";
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
	
	private String chooseDirectory() {
		JList<String> list = new JList<String>(new String[] {"Private", "Public"});
		JOptionPane.showMessageDialog(null, list, "Choose directory", JOptionPane.PLAIN_MESSAGE);
		return list.getSelectedValue();
	}
	
	public String escapeCharacters(String input) {
		StringBuilder sb = new StringBuilder();
		for(char c : input.toCharArray()) {
			switch(c){
			case '<': sb.append("&lt;"); break;
			case '>': sb.append("&gt;"); break;
			case '\"': sb.append("&quot;"); break;
			case '&': sb.append("&amp;"); break;
			case '\'': sb.append("&apos;"); break;
			default:
				if(c>0x7e) {
					sb.append("&#"+((int)c)+";");
				}else
					sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public String stripSlashes(String input) {
		return input.replace("\\", "");
	}
	public void unregisterUser() {
		mssql.delete("users", "id="+userid);
		mssql.delete("directory", "id="+userid);
		azureFileShareIO.deleteDirectory(userid);
		logout();
	}
	/**
	 * Logs out the user
	 */
	public void logout() {
		activeSession.removeSession(session);
	}
	private class AutomaticLogout extends Thread{
		public void run(){
			while(!Thread.interrupted())
				if(session.getSecondsPassed()==session.sessionMaxTime) {
					logout();
					interrupt();
				}
		}
	}
}
