package main.java.server;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;

import main.java.Message;
import main.java.design.UI;
import main.java.encryption.Encryption;
import main.java.session.Session;

public class Client {
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String nameOfDownloadedFile;
	private UI ui;
	private boolean confirm;
	private final String privateKey="temp/rsa.key", publicKey="temp/rsa.pub";

	public Client(UI ui){
		this.ui=ui;
		try {
			Socket clientSocket = new Socket("localhost", 6789);
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());
			new ListenFromServer().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void download() {
		String choosenDirectory = chooseDirectory().toLowerCase();
		nameOfDownloadedFile = JOptionPane.showInputDialog("Write file to download.(Including the file extension)");
		Message message = new Message(5, choosenDirectory, nameOfDownloadedFile);
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void upload() throws IOException {
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(null);
		File file = null;
		if(returnVal == JFileChooser.APPROVE_OPTION)
			file = chooser.getSelectedFile();
		if(file!=null) {
			String choosenDirectory = chooseDirectory().toLowerCase();	
			try {
				file = Encryption.encrypt(file, privateKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
			byte[] buffer = readFileToByteArray(file);
			Message message = new Message(4,choosenDirectory, buffer, file.getName());
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
	public void deleteFile() {
		String directory = chooseDirectory().toLowerCase();
		String filename = JOptionPane.showInputDialog("Write file to Delete.(Including the file extension)");
		Message message = new Message(6,directory,filename);
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
	private String chooseDirectory() {
		JList<String> list = new JList<String>(new String[] {"Private", "Public"});
		JOptionPane.showMessageDialog(null, list, "Choose directory", JOptionPane.PLAIN_MESSAGE);
		return list.getSelectedValue();
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
	public boolean getConfirm() {
		return confirm;
	}	
	private class ListenFromServer extends Thread {
		public synchronized void run() {
			boolean running=true;
			while(running) {
				try {
					Object obj = ois.readObject();
					if(obj instanceof byte[]) {
						downloadedFile((byte[])obj);
					}
					else if(obj instanceof byte[][]) {
						keyPair((byte[][])obj);
					}
					else if(obj instanceof Message){
						
					}
				} catch (Exception e) {
					e.printStackTrace();
					running=false;
				}
			}
		}
		private void downloadedFile(byte[] obj) throws Exception {
			System.out.println("File downloaded");
			try(OutputStream os = new FileOutputStream("downloads/"+nameOfDownloadedFile)){ 
				os.write((byte[])obj);
				Encryption.decrypt(new File("downloads/"+nameOfDownloadedFile), publicKey);	
			}finally {
				deleteFile("downloads/"+nameOfDownloadedFile);
			}
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
