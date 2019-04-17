package controller;

import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import azure.AzureFileShareIO;
import database.*;
import mssql.MSSQL;

public class Controller {
	private AzureFileShareIO azureFileShareIO = new AzureFileShareIO();
	private Authentication authentication = new Authentication();
	private String userid;
	private MSSQL mssql;
	private File file;
	private final String connectionString = "YOUR_CONNECTION_STRING";

	public Controller() {
		mssql = new MSSQL(connectionString);
	}
	public boolean login(String username, String password) {
		if(authentication.getAuthentication(username, password)) {
			azureFileShareIO.connect();
			userid=mssql.select("users", new String[] {"id"}, "username='"+username+"'").replace("\t\t", "").trim();
			return true;
		}
		else
			return false;
	}
	public ArrayList<String> register(String username, String email, String password) {
		Registration registration = new Registration(username, email, password);
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
