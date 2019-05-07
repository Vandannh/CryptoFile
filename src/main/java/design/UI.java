package main.java.design;

import java.awt.EventQueue;
import javax.swing.*;

import main.java.controller.Controller;

import java.awt.event.*;
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
<<<<<<< HEAD
	private JButton btnDelete;
	private JButton btnRegister;
	private JLabel lblCryptofile;
	private Controller controller = new Controller();

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
	 */
	public UI() {
		initialize();
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
				String username = usernameField.getText();
				String password="";
				for(char c : passwordField.getPassword()) password+=c+"";
				if(controller.login(username, password)) {
					loginPanel.setVisible(false);
					homePage(true);
				}
				else
					lblResult.setText("Worng username/password");
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
		
		JLabel lblResult = new JLabel("");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(47, 170, 330, 16);
		registerPanel.add(lblResult);
		
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String username = usernameField.getText();
				String email = emailField.getText();
				String password=passwordField.getText();
				ArrayList<String> message = controller.register(username, email, password);
				for(String s : message) {
					System.out.println(s);
					lblResult.setText(lblResult.getText()+"\n"+s);
				}
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
		homePanel.setBounds(0, 0, 432, 253);
		frame.getContentPane().add(homePanel);
		homePanel.setLayout(null);
		homePanel.setVisible(show);
		
		JLabel lblResult = new JLabel("");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(0, 220, 500, 16);
		homePanel.add(lblResult); 
		
		btnUpload = new JButton("Upload file");
		btnUpload.setBounds(155, 114, 121, 25);
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblResult.setText(controller.uploadFile());
			}
		});
		homePanel.add(btnUpload);
		
		btnDownload = new JButton("Download file");
		btnDownload.setBounds(155, 158, 122, 25);
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblResult.setText(controller.downloadFile());
			}
		});
		homePanel.add(btnDownload);
		
		btnDelete = new JButton("Delete file");
		btnDelete.setBounds(155, 200, 122, 25);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent agr0) {
				lblResult.setText(controller.deleteFile());
			}
		});
		homePanel.add(btnDelete);
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
=======
	private JButton btnRegister;
	private JLabel lblCryptofile;
	private Controller controller = new Controller();

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
	 */
	public UI() {
		initialize();
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
				String username = usernameField.getText();
				String password="";
				for(char c : passwordField.getPassword()) password+=c+"";
				if(controller.login(username, password)) {
					loginPanel.setVisible(false);
					homePage(true);
				}
				else
					lblResult.setText("Worng username/password");
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
		
		JLabel lblResult = new JLabel("");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(47, 170, 330, 16);
		registerPanel.add(lblResult);
		
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String username = usernameField.getText();
				String email = emailField.getText();
				String password=passwordField.getText();
				ArrayList<String> message = controller.register(username, email, password);
				for(String s : message) {
					System.out.println(s);
					lblResult.setText(lblResult.getText()+"\n"+s);
				}
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
		homePanel.setBounds(0, 0, 432, 253);
		frame.getContentPane().add(homePanel);
		homePanel.setLayout(null);
		homePanel.setVisible(show);
		
		JLabel lblResult = new JLabel("");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(0, 200, 500, 16);
		homePanel.add(lblResult); 
		
		btnUpload = new JButton("Upload file");
		btnUpload.setBounds(155, 114, 121, 25);
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblResult.setText(controller.uploadFile());
			}
		});
		homePanel.add(btnUpload);
		
		btnDownload = new JButton("Download file");
		btnDownload.setBounds(155, 158, 122, 25);
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblResult.setText(controller.downloadFile());
			}
		});
		homePanel.add(btnDownload);
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
		lblCryptofile.setIcon(resizeImage("files/logga.png", lblCryptofile));
>>>>>>> refs/heads/feature/SQLInjections
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
}
