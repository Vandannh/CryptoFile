package main.java.server;

import java.io.*;
import java.net.*;
import main.java.Message;

public class Server {
	private boolean running;

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

	private static String operation(Message msg) {
		switch(msg.getType()) {
		case Message.LOGIN:		return "You are logging in";
		case Message.LOGOUT: 	return "You are logging out";
		case Message.REGISTER: 	return "You are registering";
		case Message.UPLOAD: 	return "You are uploading";
		case Message.DOWNLOAD: 	return "You are downloading";
		}
		return null;
	}
	private class ClientHandler extends Thread{
		Socket socket;
		ObjectInputStream ois;
		ObjectOutputStream oos;
		Message msg;
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
					System.out.println(operation(msg));
					oos.writeObject(operation(msg).toUpperCase());
				}
			} catch (IOException | ClassNotFoundException e) {}
		}

	}
}