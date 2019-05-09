package main.java.server;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main.java.Message;

public class Client extends JPanel implements ActionListener{
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private JButton btnSend = new JButton("Send");

	public static void main(String argv[]) throws Exception {
		JFrame frame = new JFrame( "Client" );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new Client() );
		frame.pack();
		frame.setVisible(true);
	}

	public Client() throws IOException {
		Socket clientSocket = new Socket("localhost", 6789);
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		ois = new ObjectInputStream(clientSocket.getInputStream());
		setLayout(new GridLayout(1,2));
		add(btnSend);
		btnSend.addActionListener(this);
	}
	public void sendMessage() throws IOException {
		Message msg = new Message(1,"Mattias","Hejsan1");
		System.out.println(msg.getType());
		oos.writeObject(msg);
		new ListenFromServer().start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnSend) {
			try {
				sendMessage();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	private class ListenFromServer extends Thread {
		public synchronized void run() {
			while(true) {
				try {
					Object obj = ois.readObject();
					String str = obj.toString();
					System.out.println(str);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		private String operation(Message msg) {
			switch(msg.getType()) {
			case Message.LOGIN:		return "You are logging in";
			case Message.LOGOUT: 	return "You are logging out";
			case Message.REGISTER: 	return "You are registering";
			case Message.UPLOAD: 	return "You are uploading";
			case Message.DOWNLOAD: 	return "You are downloading";
			}
			return null;
		}
	}
}
