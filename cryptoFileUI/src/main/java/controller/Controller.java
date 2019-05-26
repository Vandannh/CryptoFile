package main.java.controller;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import javax.swing.*;
import main.java.azure.*;
import main.java.database.*;
import main.java.encryption.*;
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
	private ActiveSessions activeSession = new ActiveSessions();
	private Registration registration;
	private String userid;
	private MSSQL mssql;
	private SafeString safeString = new SafeString();
	private Session session;
	private final String privateKey="temp/rsa.key", publicKey="temp/rsa.pub";
	private final String connectionString = ConnectionStrings.connectionString;
	private String downloadPath;

	/**
	 * Connects to a MS SQL (SQL Server) database
	 */
	public Controller() {
		mssql = new MSSQL(connectionString);
		String home = System.getProperty("user.home");
		String separator = System.getProperty("file.separator");
		downloadPath = home + separator + "Downloads" + separator;
	}
	/**
	 * Tries to log in the user
	 *
	 * @param username the username the user is trying to log in with
	 * @param password the password the user is trying to log in with
	 * @return if the user can log in or not
	 */
	public boolean login(String username, String password) {
		username = safeString.completeSafeString(username);
		password = safeString.completeSafeString(password);
		if(authentication.getAuthentication(username, password)) {
			userid=mssql.select("users", new String[] {"id"}, "username='"+username+"'").replace("\t\t", "").trim();
			azureFileShareIO.connect("user"+userid);
			session = new Session(userid);
			activeSession.addSession(session);
			getKeyPair();
			new AutomaticLogout().start();
			return true;
		}
		else
			return false;
	}

	private void getKeyPair() {
		AzureFileShareIO temp = new AzureFileShareIO();
		temp.connect("keys");
		try {
			createDirectoryLocally("temp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		temp.download(userid, "rsa.key", "temp/");
		temp.download(userid, "rsa.pub", "temp/");
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
		if(messages.get(0).isEmpty())
			messages.set(0, " ");
		if(isNumeric(messages.get(0).toCharArray()[0])) {
			azureFileShareIO.connect("user"+messages.get(0));
			azureFileShareIO.createDirectoryInAzure("public");
			azureFileShareIO.createDirectoryInAzure("private");
			ArrayList<String> message = new ArrayList<String>();
			AzureFileShareIO temp = new AzureFileShareIO();
			temp.connect("keys");
			temp.createDirectoryInAzure(messages.get(0));
			createDirectoryLocally("temp");
			Encryption.doGenkey();
			temp.upload(messages.get(0), new File(privateKey));
			temp.upload(messages.get(0), new File(publicKey));
			deleteFile(privateKey);
			deleteFile(publicKey);
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
	public String uploadFile(File file, String directory) {
		if(file!=null) {
			if(file.length()>1048576*5)
				return "File is too big";
			String directoryId = mssql.select("directory", new String[] {"id"}, "name='"+directory+"' AND user_id='"+userid+"'").replace("\t\t", "").trim();
			try {
				file = Encryption.encrypt(file, privateKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
			azureFileShareIO.upload(directory,file);
			mssql.insert("directory", new String[] {"name","type","user_id","parent_id"}, new String[] {file.getName(),"file",userid,directoryId});
			return file.getName()+" has been uploaded";
		}
		return "Upload failed";
	}
	/**
	 * Tries to download a file from Azure File Share
	 *
	 * @return a String telling the user of the success of the download
	 */
	public String downloadFile(String directory, String filename){
		if(azureFileShareIO.download(directory,filename, downloadPath.toString())) {
			try {
				Encryption.decrypt(new File(downloadPath.toString()+filename), publicKey);
				deleteFile(downloadPath.toString()+filename);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return filename+" has been downloaded";
		}
		return "An error occured. Download failed";
	}

	public String deleteFile(String directory, String filename) {
		if(azureFileShareIO.deleteFile(directory,filename)) {
			mssql.delete("directory", "name='"+filename+"';");
			return filename+" has been deleted";
		}
		return "An error occured. Delete failed";
	}

	/**
	 * Checks if the character is a number
	 *
	 * @param ch the character
	 * @return if the character is a number or not
	 */
	public boolean isNumeric(char ch) {
		return ch>='0'&&ch<='9';
	}

	public boolean unregisterUser() {
		mssql.delete("users", "id="+userid);
		mssql.delete("directory", "user_id="+userid);
		azureFileShareIO.deleteShare("user"+userid);
		return logout();
	}

	private static void deleteFile(String filename) {
		File file = new File(filename);
		file.delete();
	}
	/**
	 * Logs out the user
	 */
	public boolean logout() {
		deleteFile(privateKey);
		deleteFile(publicKey);
		deleteFile("temp");
		activeSession.removeSession(session);
		return true;
	}

	/**
	 * Creates a directory on local computer if it doesn't exists
	 *
	 * @param directoryName the name of the directory being created
	 * @throws IOException
	 */
	public void createDirectoryLocally(String directoryName) throws IOException {
		Path path = Paths.get(directoryName);
		if (!Files.exists(path)) {
			Files.createDirectory(path);
		}
	}

	public String getFiles(String directory) {
		return mssql.select("directory", new String[]{"name", "parent_id"}, "type='file' AND user_id='"+userid+"' AND parent_id=(select id from directory where name='"+directory+"' AND user_id='"+userid+"')");
	}
	public double getAvailableSpace() {
		return azureFileShareIO.checkAvailableSpace();
	}
	public double getUsedMemoryPercentage() {
		return azureFileShareIO.usedMemoryPercentage();
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