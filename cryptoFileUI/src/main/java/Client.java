package main.java;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import application.*;
import main.java.Message;
import main.java.encryption.Encryption;
/**
 * Class for the client
 * @author Mattias Jönnson, Markus Masalkovski
 *
 */
public class Client {
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String nameOfDownloadedFile;
	private boolean confirm;
	private final String privateKey="temp/rsa.key", publicKey="temp/rsa.pub";
	private boolean loggedIn;
	private UserInterfaceController uic;
	private String downloadPath;
	private String searchedUser;

	/**
	 * Constructor that initiate the connection
	 * @param uic
	 */
	public Client(UserInterfaceController uic){
		this.uic=uic;
		String home = System.getProperty("user.home");
		String separator = System.getProperty("file.separator");
		downloadPath = home + separator + "Downloads" + separator;
		try {
			Socket clientSocket = new Socket("137.135.251.26", 12345);
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());
			new ListenFromServer().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setUserInterface(UserInterfaceController uic) {
		this.uic=uic;
	}
	public void setSearchedUser(String searchedUser) {
		this.searchedUser=searchedUser;
	}
	public String getSearchedUser() {
		return searchedUser;
	}
	public void download(String nameOfDownloadedFile, String directory) {
		this.nameOfDownloadedFile=nameOfDownloadedFile;
		Message message = new Message(5, directory, nameOfDownloadedFile);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void upload(File file, String directory, String dir) throws IOException {
		if(file!=null) {
			try {
				if(dir == "pub")
					file = Encryption.encrypt(file, publicKey, "pub");
				else
					file = Encryption.encrypt(file, privateKey, "pvt");
			} catch (Exception e) {
				e.printStackTrace();
			}
			byte[] buffer = readFileToByteArray(file);
			Message message = new Message(4,directory, buffer, file.getName());
			deleteFile(file.getPath());
			oos.writeObject(message);
		}
	}
	public void register(String username, String email, String password) {
		Message message = new Message(3, username, email, password);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void login(String username, String password) {
		Message message = new Message(1,username,password);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void deleteFile(String filename, String directory) {
		Message message = new Message(6,directory,filename);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void getFilelist(String directory) {
		Message message = new Message(7,directory);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private byte[] readFileToByteArray(File file){
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
	private void deleteFile(String filename) {
		File file = new File(filename);
		file.delete();
	}

	public void logout() {
		Message message = new Message(2);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void unregister() {
		Message message = new Message(8);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void search(String text) {
		Message message = new Message(9, text);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void loggedout() {
		deleteFile(privateKey);
		deleteFile(publicKey);
		deleteFile("temp");
	}

	private class ListenFromServer extends Thread {
		public synchronized void run() {
			boolean running=true;
			while(running) {
				try {
					Object obj = ois.readObject();
					if(obj instanceof byte[]) {
						boolean downloaded = downloadedFile((byte[])obj);
						System.out.println(downloaded);
						if(!downloaded)
							uic.displayMessage(3, "An error has occured with the downloading");
						else
							uic.displayMessage(3, nameOfDownloadedFile+" has been downloaded");
					}
					else if(obj instanceof byte[][]) {
						keyPair((byte[][])obj);
					}
					else if(obj instanceof Message){
						String returnMessage = (String) ((Message)obj).getReturnMessage();
						switch(returnMessage.trim()) {
						case "Logged in":
							uic.changeScene(1);
							break;
						case "Wrong username/password":
							uic.displayMessage(1,returnMessage);
							break;
						case "Logged out":
							loggedout();
							uic.changeScene(2);
							break;
						case "User created":
							uic.changeScene(3);
							break;
						case "Unregistered":
							loggedout();
							uic.changeScene(2);
							break;
						case "File is too big":
							uic.displayMessage(3, returnMessage);
							break;
						case "File has been uploaded":
							uic.changeScene(4);
							break;
						case "File has been deleted":
							uic.displayMessage(3, returnMessage);
							uic.updateFileList();
							break;
						case "An error occured. Delete failed":
							uic.displayMessage(3, returnMessage);
							break;
						default:
							uic.displayMessage(2, returnMessage);
							break;
						}
					}
					else if(obj instanceof String) {
						String str = obj.toString();
						if(str.contains("search")) {
							System.out.println(str);
										uic.setSearchList(str.replace("search", ""));
						}
						else
							uic.setFileList(str);
					}
				} catch (Exception e) {
					e.printStackTrace();
					running=false;
				}
			}
		}
		private boolean downloadedFile(byte[] obj) {
//			try(OutputStream os = new FileOutputStream(("downloads/"+nameOfDownloadedFile))){
//				os.write((byte[])obj);
//				System.out.println(obj.length);
//				Encryption.decrypt(new File("downloads/"+nameOfDownloadedFile), publicKey);
//				System.out.println("downloaded");
//			}catch(Exception e) {
//				e.printStackTrace();
//				return false;
//			}
//			deleteFile("downloads/"+nameOfDownloadedFile);
//			return true;
			try {
				FileUtils.writeByteArrayToFile(new File(downloadPath+nameOfDownloadedFile), obj);
				if(uic.isPublic()) {
					Encryption.decrypt(new File(downloadPath+nameOfDownloadedFile), privateKey, "pvt");
				} else {
					Encryption.decrypt(new File(downloadPath+nameOfDownloadedFile), publicKey, "pub");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}finally {
				deleteFile(downloadPath+nameOfDownloadedFile);
			}
			return true;
		}
		private void keyPair(byte[][] keyPair) throws FileNotFoundException, IOException {
			System.out.println("Key Pair");
			try {
				createDirectoryLocally("temp");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try(OutputStream os1 = new FileOutputStream(privateKey);
					OutputStream os2 = new FileOutputStream(publicKey)){
				os1.write(keyPair[0]);
				os2.write(keyPair[1]);
			}
		}
	}
}
