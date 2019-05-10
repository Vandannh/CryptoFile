package main.java.server;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.java.Message;
import main.java.design.UI;

public class Client extends JPanel implements ActionListener{
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String nameOfDownloadedFile;
	private JButton btnSend = new JButton("Send");
	private UI ui;

	public static void main(String argv[]) throws Exception {
		JFrame frame = new JFrame( "Client" );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new Client(null) );
		frame.pack();
		frame.setVisible(true);
	}

	public Client(UI ui){
		this.ui=ui;
		try {
			Socket clientSocket = new Socket("localhost", 6789);
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());
//			setLayout(new GridLayout(1,2));
//			add(btnSend);
//			btnSend.addActionListener(this);
			new ListenFromServer().start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendMessage(Message message) throws IOException {
		//		Message msg = new Message(1,"test1", "Password1");
		oos.writeObject(message);
		//		nameOfDownloadedFile="Bad Gastien 1.PNG";
		//		msg = new Message(5,"private", "Bad Gastien 1.PNG");
		//		oos.writeObject(msg);
	}

	private void upload() throws IOException {
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(null);
		File file = null;
		if(returnVal == JFileChooser.APPROVE_OPTION)
			file = chooser.getSelectedFile();
		if(file!=null) {
			String choosenDirectory = chooseDirectory().toLowerCase();	

			Message msg = new Message(4,choosenDirectory, file);
			oos.writeObject(msg);
		}
	}

	private String chooseDirectory() {
		JList<String> list = new JList<String>(new String[] {"Private", "Public"});
		JOptionPane.showMessageDialog(null, list, "Choose directory", JOptionPane.PLAIN_MESSAGE);
		return list.getSelectedValue();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnSend) {
			try {
				Message msg = new Message(1,"test1", "Password1");
				sendMessage(msg);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	private class ListenFromServer extends Thread {
		public synchronized void run() {
			boolean running=true;
			while(running) {
				try {
					Object obj = ois.readObject();
					if(obj instanceof byte[]) {
						System.out.println("File downloaded");
						OutputStream os = new FileOutputStream("downloads/"+nameOfDownloadedFile); 
						os.write((byte[])obj);
					}
					String str = obj.toString();
					ui.setTextMessage(str);
//					ui.confirm(true);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
					running=false;
				}
			}
		}
	}
}
