package main.java.server;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import main.java.Message;
import main.java.controller.*;
import main.java.session.ActiveSessions;
import main.java.session.Session;
/**
 * Server class
 * @author Mattias Jönsson
 *
 */
public class Server {
	private boolean running;
	private Controller controller;
	private String userid;
	private ActiveSessions activeSessions = new ActiveSessions();

	/**
	 * constructor that initiate controller
	 */
	public Server() {
		controller = new Controller();
	}

	/**
	 * Main method
	 * @param argv
	 * @throws Exception
	 */
	public static void main(String argv[]) throws Exception {
		Server server = new Server();
		server.start();
	}

	/**
	 * Method that are used for starting the server
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void start() throws IOException, ClassNotFoundException {
		System.out.println("Server started");
		ServerSocket serverSocket = new ServerSocket(12345);
		running=true;
		ExecutorService executor = Executors.newFixedThreadPool(10);
		while(running) {
			Socket socket = serverSocket.accept(); 
			executor.execute(new ClientHandler(socket));
			if(!running) executor.shutdown();
		}
		serverSocket.close();
	}
	public void stop() {
		running=false;

	}

	/**
	 * Inner class that handles the client
	 * @author Mattias Jönnson
	 *
	 */
	private class ClientHandler extends Thread{
		Socket socket;
		ObjectInputStream ois;
		ObjectOutputStream oos;
		Message msg;
		Session session;
		ClientHandler(Socket socket) throws ClassNotFoundException, IOException {
			this.socket=socket;
		}
		public void run() {
			System.out.println("Client connected");
			boolean running = true;
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				while (running) {
					Object obj = ois.readObject();
					if(obj instanceof Message) {
						msg = (Message)obj;
					}
					Object object = operation(msg);
					oos.writeObject(object);
				}
			} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {}
		}
		private Object operation(Message msg) throws NoSuchAlgorithmException, IOException {
			byte[][] keys = null;
			switch(msg.getType()) {
			case Message.LOGIN:	
				keys = controller.login(msg.getUsername(), msg.getPassword());
				if(keys==null)					
					return new Message(0, "Wrong username/password");
				else {
					oos.writeObject(keys);
					return new Message(0, "Logged in");
				}
			case Message.LOGOUT: 	
				activeSessions.removeSession(session);
				return new Message(0, "Logged out");
			case Message.REGISTER: 	
				String text="";
				for(String s : controller.register(msg.getUsername(), msg.getEmail(), msg.getPassword()))
					text+=s+"\n";
				keys = controller.login(msg.getUsername(), msg.getPassword());
				oos.writeObject(keys);
				return new Message(0, text);
			case Message.UPLOAD: 	
				String returnMessage = controller.uploadFile(msg.getFile(), msg.getDirectory(), msg.getFilename());
				return new Message(0, returnMessage);
			case Message.DOWNLOAD: 	
				return controller.downloadFile(msg.getFilename(),msg.getDirectory());
			case Message.DELETE:
				return new Message(0, controller.deleteFile(msg.getFilename(), msg.getDirectory()));
			case Message.FILELIST:
				return controller.getFiles(msg.getDirectory());
			case Message.UNREGISTER:
				controller.unregisterUser();
				activeSessions.removeSession(session);
				return new Message(0, "Unregistered");
			case Message.SEARCH:
				return "search"+controller.search(msg.getSearch());
			case Message.USERFILELIST:
				oos.writeObject(new Message(0, controller.getUserKey(msg.getUsername())));
				return "userfilelist"+controller.getUserFiles(msg.getUsername());
			case Message.DOWNLOADUSERFILE:
				return controller.downloadUserFile(msg.getFilename(), msg.getUsername());
			}
			
			return null;
		}
	}
}