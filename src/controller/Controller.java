package controller;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import azure.AzureFileShareIO;
import database.*;
import mssql.MSSQL;

public class Controller {
	private AzureFileShareIO azureFileShareIO = new AzureFileShareIO();
	private Authentication authentication = new Authentication();
	private Registration registration;
	private String userid;
	private MSSQL mssql;
	private final String connectionString = "jdbc:sqlserver://cryptofiletesting.database.windows.net:1433;database=Testing;user=Mattias@cryptofiletesting;password=Hejsan123;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
	
	public Controller() {
		mssql = new MSSQL(connectionString);
	}
	public boolean login(String username, String password) {
		if(authentication.getAuthentication(username, password)) {
			azureFileShareIO.connect();
			userid=mssql.select("Users", new String[] {"*"}, "username='"+username+"'").replace("\t\t", "");
			return true;
		}
		else
			return false;
	}
	public ArrayList<String> register(String username, String email, String password) {
		Registration registration = new Registration(username, email, password);
		ArrayList<String> messages = registration.register();
		System.out.println(messages.get(0));
		if(isNumeric(messages.get(0).toCharArray()[0])) {
			azureFileShareIO.connect();
			azureFileShareIO.createDirectory(messages.get(0));
			ArrayList<String> message = new ArrayList<String>();
			message.add("User created");
			return message;
		}
		else
			return messages;
	}
	public void uploadFile() {
		azureFileShareIO.upload();
	}
	public void downloadFile(){
		String filename = JOptionPane.showInputDialog("Write file to download");
		azureFileShareIO.download(userid,filename);
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
