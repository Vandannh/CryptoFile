package main.java.server;

import java.io.*;
import java.net.*;
import main.java.Message;

public class Server {
	private ObjectInputStream inFromClient;
	private ObjectOutputStream outToClient;
	private boolean running;
	
	public static void main(String argv[]) throws Exception {
		Server server = new Server();
		server.start();
	}
	
	public void start() throws IOException, ClassNotFoundException {
		ServerSocket serverSocket = new ServerSocket(6789);
		running=true;
		while(running) {
			Socket socket = serverSocket.accept(); 
			if(!running) break;
			ClientHandler t = new ClientHandler(socket); 
			t.start();
		}
	}
	
	private static String operation(Message msg) {
		switch(msg.getType()) {
		case Message.LOGIN: return "You are logging in";
		case Message.LOGOUT: return "You are logging out";
		case Message.REGISTER: return "You are registering";
		case Message.UPLOAD: return "You are uploading";
		case Message.DOWNLOAD: return "You are downloading";
		}
		return null;
	}
	private class ClientHandler extends Thread{
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		Message msg;
		ClientHandler(Socket socket) throws ClassNotFoundException, IOException {
			System.out.println("Server started");
			while (!Thread.interrupted()) {
				inFromClient = new ObjectInputStream(socket.getInputStream());
				outToClient = new ObjectOutputStream(socket.getOutputStream());
				msg = (Message) inFromClient.readObject();
				System.out.println(operation(msg));
				outToClient.writeObject(operation(msg).toUpperCase());
			}
		}
	
	}
}