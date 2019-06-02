package main.java;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import org.apache.commons.io.FileUtils;
import application.*;
import main.java.Message;
import main.java.encryption.Encryption;
/**
 * Class for the client
 * @author Mattias Jönnson
 * @version 2.0
 * @since 2019-05-30
 */
public class Client {
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String nameOfDownloadedFile;
	private final String privateKey="temp/rsa.key", publicKey="temp/rsa.pub";
	private UserInterfaceController uic;
	private String downloadPath;
	private String searchedUser;

	/**
	 * Constructor that initiate the connection
	 * @param uic
	 */

	public Client(UserInterfaceController uic){
		this.uic=uic;
		setDownloadPath();
		try {
			Socket clientSocket = new Socket("localhost", 12345);
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());
			new ListenFromServer().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setDownloadPath() {
		String home = System.getProperty("user.home");
		String separator = System.getProperty("file.separator");
		downloadPath = home + separator + "Downloads" + separator;
	}
	
	/**
	 * Method that sets the userinterface
	 * @param uic
	 */
	public void setUserInterface(UserInterfaceController uic) {
		this.uic=uic;
	}
	/**
	 * Sets the searchedUser
	 * @param searchedUser
	 */
	public void setSearchedUser(String searchedUser) {
		this.searchedUser=searchedUser;
	}
	
	/**
	 * Gets the username of the searched user
	 * 
	 * @return the searchedUser
	 */
	public String getSearchedUser() {
		return searchedUser;
	}
	/**
	 * Sends the information of the file the user wants to download, to the server 
	 * 
	 * @param nameOfDownloadedFile
	 * @param directory
	 */
	public void download(String nameOfDownloadedFile, String directory) {
		this.nameOfDownloadedFile=nameOfDownloadedFile;
		Message message = new Message(5, directory, nameOfDownloadedFile);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sends the information of the file the user wants to upload, to the server 
	 * 
	 * @param file
	 * @param directory
	 * @param dir
	 * @throws IOException
	 */
	public void upload(File file, String directory, String dir) throws IOException {
		if(file!=null) {
			try {
				if(dir == "pvt")
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
	/**
	 * Sends the information of the user wanting to register 
	 * 
	 * @param username
	 * @param email
	 * @param password
	 */
	public void register(String username, String email, String password) {
		Message message = new Message(3, username, email, password);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sends the information of the user wanting to login 
	 * 
	 * @param username
	 * @param password
	 */
	public void login(String username, String password) {
		Message message = new Message(1,username,password);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sends the information of the file the user wants to download 
	 * 
	 * @param filename
	 * @param directory
	 */
	public void deleteFile(String filename, String directory) {
		Message message = new Message(6,directory,filename);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes a directory
	 * 
	 * @param path
	 * @throws IOException
	 */
	private void deleteDirectory(Path path) throws IOException {
		  if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
		    try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
		      for (Path entry : entries) {
		        deleteDirectory(entry);
		      }
		    }
		  }
		  if(Files.exists(path))
			  Files.delete(path);
		}
	/**
	 * Sends the information of the filelist the user want to get
	 * 
	 * @param directory
	 */
	public void getFilelist(String directory) {
		Message message = new Message(7,directory);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Converts a File-object to a byte array
	 * 
	 * @param file the file to convert
	 * @return the file as byte array
	 */
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
	/**
	 * Deletes a file
	 * 
	 * @param filename the name of the file being deleted
	 */
	private void deleteFile(String filename) {
		File file = new File(filename);
		file.delete();
	}

	/**
	 * Sends to the server that the user want to logout 
	 * 
	 * @throws IOException
	 */
	public void logout() throws IOException {
		loggedout();
		Message message = new Message(2);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Method that lets the client unregister as a user
	 */
	public void unregister() {
		Message message = new Message(8);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets all the files from another users public directory
	 * 
	 * @param username  
	 */
	public void getUserFiles(String username) {
		Message message = new Message(10, username);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Downloads a file from another users public directory
	 * 
	 * @param filename
	 * @param username
	 */
	public void downloadUserFile(String filename, String username) {
		this.nameOfDownloadedFile=filename;
		Message message = new Message(11, filename, username);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the information of username the user have searched after to the server
	 *
	 * @param username
	 */
	public void search(String username) {
		Message message = new Message(9, username);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Deletes all necessary methods when a user logs out
	 * 
	 * @throws IOException
	 */
	private void loggedout() throws IOException  {
		deleteFile(privateKey);
		deleteFile(publicKey);
		deleteDirectory(Paths.get(new File("temp").getAbsolutePath()));
	}

	/**
	 * Inner class that listens on the server
	 * 
	 * @author Mattias Jönsson
	 *
	 */
	private class ListenFromServer extends Thread {
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public synchronized void run() {
			boolean running=true;
			while(running) {
				try {
					Object obj = ois.readObject();
					if(obj instanceof byte[]) {
						boolean downloaded = downloadedFile((byte[])obj);
						if(!downloaded)
							uic.displayMessage(3, "An error has occured with the downloading");
						else
							uic.displayMessage(3, nameOfDownloadedFile+" has been downloaded");
					}
					else if(obj instanceof byte[][]) {
						keyPair((byte[][])obj);
					}
					else if(obj instanceof Message){
						Message msg = (Message)obj;
						if(msg.getReturnMessage() instanceof String) {
							String returnMessage = msg.getReturnMessage().toString().trim();
							switch(returnMessage) {
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
						else if (msg.getReturnMessage() instanceof byte[]) {
							byte[] key = (byte[]) msg.getReturnMessage();
							try(OutputStream os = new FileOutputStream("temp/userKey.pub")){
								os.write(key);
							}
						}
					}
					else if(obj instanceof String) {
						String str = obj.toString();
						if(str.contains("search")) {
							uic.setSearchList(str.replace("search", ""));
						}
						else if(str.contains("userfilelist")) {
							uic.setFileList(str.replace("userfilelist", ""));
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
		/**
		 * Decrypt and writes downloaded file to the harddrive 
		 *
		 * @param byteArray the byteArray of the file
		 * @return true if the file is downloaded
		 */
		private boolean downloadedFile(byte[] byteArray) {
			try {
				FileUtils.writeByteArrayToFile(new File(downloadPath+nameOfDownloadedFile), byteArray);
				if(uic.isPublic())
					Encryption.decrypt(new File(downloadPath+nameOfDownloadedFile), publicKey, "pub");
				else if(uic.isPrivate())
					Encryption.decrypt(new File(downloadPath+nameOfDownloadedFile), privateKey, "pvt");
				else if(uic.isOtherUser())
					Encryption.decrypt(new File(downloadPath+nameOfDownloadedFile), "temp/userKey.pub", "pub");

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}finally {
				deleteFile(downloadPath+nameOfDownloadedFile);
			}
			return true;
		}

		/**
		 * Writes a keypair to the harddrive
		 * 
		 * @param keyPair the encryption keyPair in byte arrays
		 * @throws FileNotFoundException
		 * @throws IOException
		 */
		private void keyPair(byte[][] keyPair) throws FileNotFoundException, IOException {
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
