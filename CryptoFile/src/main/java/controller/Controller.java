package main.java.controller;

import java.io.*;
import java.net.URISyntaxException;
import java.security.*;
import java.util.*;
import com.microsoft.azure.storage.StorageException;
import main.java.azure.*;
import main.java.database.*;
import main.java.encryption.Encryption;
import mssql.MSSQL;
import test2.ConnectionStrings;
import main.java.session.*;
import main.java.text.*; 

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
//	private ActiveSessions activeSession = new ActiveSessions();
	private Registration registration;
	private String userid;
	private MSSQL mssql;
	private SafeString safeString = new SafeString();
//	private Session session;
	private final String privateKey="rsa.key", publicKey="rsa.pub";

	private final String connectionString = ConnectionStrings.connectionString;

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
	public byte[][] login(String username, String password) {
		username = safeString.completeSafeString(username);
		password = safeString.completeSafeString(password);
		if(authentication.getAuthentication(username, password)) {
			userid=mssql.select("users", new String[] {"id"}, "username='"+username+"'").replace("\t\t", "").trim();
			azureFileShareIO.connect("user"+userid);
			return getKeyPair();
		}
		else
			return null;
	}
	public byte[][] getKeyPair() {
		AzureFileShareIO temp = new AzureFileShareIO();
		temp.connect("keys");
		byte[] privateKey=null;
		byte[] publicKey=null;
		try {
			privateKey = temp.downloadKeys(userid, "rsa.key");
			publicKey = temp.downloadKeys(userid, "rsa.pub");
		} catch (StorageException | URISyntaxException e) {
			e.printStackTrace();
		}
		return new byte[][] {privateKey, publicKey};
	}
	/**
	 * Tries to register the user
	 * 
	 * @param username the username the user is trying to register
	 * @param email the email the user is trying to register 
	 * @param password the password the user is trying to register
	 * @return an ArrayList of the error messages if the user fail to register of 
	 * a message telling the user it was created
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public ArrayList<String> register(String username, String email, String password) throws NoSuchAlgorithmException, IOException {
		username = safeString.completeSafeString(username);
		password = safeString.completeSafeString(password);
		email = safeString.completeSafeString(email);
		registration = new Registration(username, email, password);
		ArrayList<String> messages = registration.register();
		if(isNumeric(messages.get(0).toCharArray()[0])) {
			azureFileShareIO.connect("user"+messages.get(0));
			azureFileShareIO.createDirectoryInAzure("public");
			azureFileShareIO.createDirectoryInAzure("private");
			ArrayList<String> message = new ArrayList<String>();
			AzureFileShareIO temp = new AzureFileShareIO();
			temp.connect("keys");
			temp.createDirectoryInAzure(messages.get(0));
			Encryption.doGenkey();
			temp.upload(messages.get(0), readFileToByteArray(new File("temp/"+privateKey)), privateKey);
			temp.upload(messages.get(0), readFileToByteArray(new File("temp/"+publicKey)), publicKey);
			deleteFile("temp/"+privateKey);
			deleteFile("temp/"+publicKey);
			message.add("User created");
			return message;
		}
		else
			return messages;
	}

	/**
	 * Gets a file and tries to upload it
	 * 
	 * @param file the file to upload
	 * @param choosenDirectory the directory the file will be uploaded to
	 * @return A string confirming the upload
	 */
	public String uploadFile(byte[] buffer, String choosenDirectory, String filename) {
		if(buffer.length>1073741824*5)
			return "File is too big";
		String directoryId = mssql.select("directory", new String[] {"id"}, "name='"+choosenDirectory+"' AND user_id='"+userid+"'").replace("\n", "").trim();
		azureFileShareIO.upload(choosenDirectory, buffer, filename);
		mssql.insert("directory", new String[] {"name","type","user_id","parent_id"}, new String[] {filename,"file",userid,directoryId});
		return "File has been uploaded";
	}
	/**
	 * Tries to download a file from Azure File Share
	 * 
	 * @param filename the name of the file to be downloaded
	 * @param directory the directory of the file to be downloaded
	 * @return a String telling the user of the success of the download
	 */
	public byte[] downloadFile(String filename, String directory){
		try {
			return azureFileShareIO.downloadFile(directory,filename);
		} catch (StorageException | URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deletes a file from Azure File Share
	 * 
	 * @param filename the name of the file to be deleted
	 * @param directory the directory of the file to be deleted
	 * @return a String telling the user of the success of the deletion
	 */
	public String deleteFile(String filename, String directory) {
		if(azureFileShareIO.deleteFile(directory,filename)) {
			mssql.delete("directory", "name='"+filename+"' AND user_id='"+userid+"';");
			return "File has been deleted";
		}
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

	public void unregisterUser() {
		mssql.delete("users", "id="+userid);
		mssql.delete("directory", "user_id="+userid);
		azureFileShareIO.deleteShare("user"+userid);
	}
	
	private void deleteFile(String filename) {
		File file = new File(filename);
		file.delete();
	}
	/**
	 * Logs out the user
	 */
	public boolean logout() {
//		deleteFile(privateKey);
//		deleteFile(publicKey);
//		deleteFile("temp");
//		activeSession.removeSession(session);
		return true;
	}
	
	public byte[] readFileToByteArray(File file){
		FileInputStream fis = null;
		byte[] bArray = new byte[(int) file.length()];
		try{
			fis = new FileInputStream(file);
			fis.read(bArray);
			fis.close();           
		}catch(IOException ioExp){
			ioExp.printStackTrace();
		}
		return bArray;
	}
	public String getFiles(String directory) {
		return mssql.select("directory", new String[]{"name", "parent_id"}, "type='file' AND user_id='"+userid+"' AND parent_id=(select id from directory where name='"+directory+"' AND user_id="+userid+")");
	}
	public double getAvailableSpace() {
		return azureFileShareIO.checkAvailableSpace();
	}
	public double getUsedMemoryPercentage() {
		return azureFileShareIO.usedMemoryPercentage();
	}
	
	public void startAutomaticLogout(Session session) {
		new AutomaticLogout(session).start();
	}
	
	private class AutomaticLogout extends Thread{
		private Session session;
		public AutomaticLogout(Session session) {
			this.session=session;
		}
		public void run(){
			while(!Thread.interrupted())
				if(session.getSecondsPassed() == session.sessionMaxTime) {
					System.out.println("Logged out");
//					server.logout();
					interrupt();
				}
		}
	}
}