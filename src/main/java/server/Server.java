package main.java.server;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;

import main.java.Message;
import main.java.controller.*;
import main.java.session.ActiveSessions;
import main.java.session.Session;

public class Server {
	private boolean running;
	private Controller controller;
	private String userid;
	private ActiveSessions activeSessions = new ActiveSessions();

	public Server() {
		controller = new Controller();
	}
	
	public static void main(String argv[]) throws Exception {
		Server server = new Server();
		server.start();
	}
	
	public void start() throws IOException, ClassNotFoundException {
		System.out.println("Server started");
		ServerSocket serverSocket = new ServerSocket(6789);
		running=true;
		while(running) {
			Socket socket = serverSocket.accept(); 
			if(!running) break;
			ClientHandler t = new ClientHandler(socket); 
			t.start();
		}
	}
	public void stop() {
		running=false;
	}

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
					if(object instanceof Session) {
						Session s = (Session)object;
						System.out.println(s.getSessionID());
						oos.writeObject(s);
					}
					else
						oos.writeObject(object);
				}
			} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {}
		}
		private Object operation(Message msg) throws NoSuchAlgorithmException, IOException {
			switch(msg.getType()) {
			case Message.LOGIN:		
				if(controller.login(msg.getUsername(), msg.getPassword())==null) 
					return new Message(0, "Wrong username/password");
				else {
					session = new Session(msg.getUsername());
					activeSessions.addSession(session);
					return session;
				}
			case Message.LOGOUT: 	
				activeSessions.removeSession(session);
				return "You are logging out";
			case Message.REGISTER: 	
				String text="";
				for(String s : controller.register(msg.getUsername(), msg.getEmail(), msg.getPassword()))
					text+=s+"\n";
				return text;
			case Message.UPLOAD: 	
				return controller.uploadFile(msg.getFile(), msg.getDirectory(), msg.getFilename());
			case Message.DOWNLOAD: 	
				return controller.downloadFile(msg.getFilename(),msg.getDirectory());
			case Message.DELETE:
				return controller.deleteFile(msg.getFilename(), msg.getDirectory());
			}
			return null;
		}
	}
}