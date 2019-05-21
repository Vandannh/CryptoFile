package main.java.design;

import java.awt.EventQueue;
import javax.swing.*;

import main.java.Message;
import main.java.controller.Controller;
import main.java.server.Client;
import java.awt.event.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.awt.*;

/**
 * A simple UI for the application
 * 
 * @author Mattias Jönsson
 *
 */
public class UI {
	private JFrame frame;
	private JTextField usernameField,emailField;
	private JPasswordField passwordField;
	private JPanel loginPanel;
	private JPanel homePanel;
	private JPanel registerPanel;
	private JButton btnUpload;
	private JButton btnDownload;
	private JButton btnDelete;
	private JButton btnRegister;
	private JButton btnLogout;
	private JButton btnUnregister;
	private JLabel lblCryptofile;
	private Client client;
	private JLabel lblResult = new JLabel("");
	private boolean confirm=true;
	private String textMessage;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI window = new UI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public UI() {
		initialize();
		client = new Client(this);
	}

	/**
	 * The contents of the login page.
	 * @param show if the page should be shown or not
	 */
	private void loginPage(boolean show) {

		loginPanel = new JPanel();
		loginPanel.setBounds(0, 92, 432, 161);
		frame.getContentPane().add(loginPanel);
		loginPanel.setLayout(null);
		loginPanel.setVisible(show);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(80,13,80,22);
		loginPanel.add(lblUsername);

		usernameField = new JTextField();
		usernameField.setBounds(144, 13, 116, 22);
		loginPanel.add(usernameField);
		usernameField.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(80,47,80,22);
		loginPanel.add(lblPassword);

		passwordField = new JPasswordField();
		passwordField.setBounds(144, 47, 116, 22);
		loginPanel.add(passwordField);

		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(144, 82, 116, 25);
		loginPanel.add(btnLogin);

		btnRegister = new JButton("Register");
		btnRegister.setBounds(144, 120, 116, 25);
		loginPanel.add(btnRegister);

		JLabel lblResult = new JLabel("");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(0, 145, 350, 16);
		loginPanel.add(lblResult);

		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblResult.setText("");
				String username = usernameField.getText();
				String password="";
				for(char c : passwordField.getPassword()) password+=c+"";
				client.login(username, password);
//				System.out.println(client.getConfirm());
//				if(client.getConfirm()) {
////					loginPanel.setVisible(false);
////					homePage(true);
//					lblResult.setText("logged in");
//				}
//				else
//					lblResult.setText("Worng username/password");
			}
		});
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loginPanel.setVisible(false);
				registerPage(true);
			}
		});
	}

	/**
	 * The contents of the login page.
	 * 
	 * @param show if the page should be shown or not
	 */
	private void registerPage(boolean show) {
		System.out.println("test");
		registerPanel = new JPanel();
		registerPanel.setBounds(0, 92, 450, 350);
		frame.getContentPane().add(registerPanel);
		registerPanel.setLayout(null);
		registerPanel.setVisible(show);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(80,13,80,22);
		registerPanel.add(lblUsername);

		usernameField = new JTextField();
		usernameField.setBounds(144, 13, 116, 22);
		registerPanel.add(usernameField);
		usernameField.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(80,47,80,22);
		registerPanel.add(lblPassword);

		JTextField passwordField = new JTextField();
		passwordField.setBounds(144, 47, 116, 22);
		registerPanel.add(passwordField);

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(80,82,80,22);
		registerPanel.add(lblEmail);

		emailField = new JTextField();
		emailField.setBounds(144, 82, 116, 22);
		registerPanel.add(emailField);

		btnRegister = new JButton("Register");
		btnRegister.setBounds(144, 110, 116, 25);
		registerPanel.add(btnRegister);

		JButton btnBack = new JButton("Back to login");
		btnBack.setBounds(144, 140, 116, 25);
		registerPanel.add(btnBack);

		lblResult = new JLabel("");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(47, 170, 350, 16);
		registerPanel.add(lblResult);

		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblResult.setText("");
				String username = usernameField.getText();
				String email = emailField.getText();
				String password=passwordField.getText();
				client.register(username, email, password);
				System.out.println(confirm);
				if(!confirm)
					lblResult.setText(textMessage);
				else
					lblResult.setText("User created");
			}
		});
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				registerPanel.setVisible(false);
				loginPage(true);
			}
		});
	}

	/**
	 * The contents of the home page.
	 * 
	 * @param show if the page should be shown or not
	 */
	public void homePage(boolean show) {
		homePanel = new JPanel();
		homePanel.setBounds(0, 0, 450, 300);
		frame.getContentPane().add(homePanel);
		homePanel.setLayout(null);
		homePanel.setVisible(show);

		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(0, 245, 400, 16);
		homePanel.add(lblResult); 

		btnUpload = new JButton("Upload file");
		btnUpload.setBounds(155, 94, 122, 25);
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					client.upload();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		homePanel.add(btnUpload);

		btnDownload = new JButton("Download file");
		btnDownload.setBounds(155, 124, 122, 25);
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client.download(); 
			}
		});
		homePanel.add(btnDownload);

		btnDelete = new JButton("Delete file");
		btnDelete.setBounds(155, 154, 122, 25);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent agr0) {
				client.deleteFile();
				//				lblResult.setText(controller.deleteFile());
			}
		});
		homePanel.add(btnDelete);

		btnLogout = new JButton("Logout");
		btnLogout.setBounds(155, 184, 122, 25);
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent agr0) {
//				if(controller.logout()) {
//					homePanel.setVisible(false);
//					loginPage(true);
//				}
			}
		});
		homePanel.add(btnLogout);

		btnUnregister = new JButton("Unregister");
		btnUnregister.setBounds(155, 214, 122, 25);
		btnUnregister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent agr0) {
//				if(controller.unregisterUser()) {
//					homePanel.setVisible(false);
//					loginPage(true);
//				}
			}
		});
		homePanel.add(btnUnregister);
	}

	/**
	 * Initalize the contents of the application
	 */
	private void initialize() {
		frame = new JFrame("CryptoFile");
		frame.setBounds(100, 100, 450, 350);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		lblCryptofile = new JLabel();
		lblCryptofile.setBounds(50, 0, 350, 80);
		lblCryptofile.setIcon(resizeImage("src/main/resources/images/logga.png", lblCryptofile));
		lblCryptofile.setFont(new Font("Tahoma", Font.PLAIN, 43));
		lblCryptofile.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblCryptofile);
		loginPage(true);
	}

	/**
	 * Resizes a given image of the size of a given JContent-object
	 * 
	 * @param ImagePath the path of the image
	 * @param jc the JContent the image should resize to
	 * @return the resized image
	 */
	private ImageIcon resizeImage(String ImagePath, JComponent jc){
		ImageIcon MyImage = new ImageIcon(ImagePath);
		Image img = MyImage.getImage();
		Image newImg = img.getScaledInstance(jc.getWidth(), jc.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon image = new ImageIcon(newImg);
		return image;
	}

	public void confirm(boolean confirm) {
		this.confirm=confirm;
	}

	public void setTextMessage(String textMessage) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date)+": "+textMessage.toString()+" ("+textMessage.getClass().toString()+")");
		this.textMessage=textMessage;
	}
	
	public JLabel getLblResult() {
		return lblResult;
	}
}
