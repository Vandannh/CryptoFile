package design;

import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Image;
import controller.Controller;

public class UI {
	private JFrame frame;
	private JTextField usernameField,emailField;
	private JPasswordField passwordField;
	private JPanel loginPanel;
	private JPanel homePanel;
	private JPanel registerPanel;
	private JButton btnUpload;
	private JButton btnDownload;
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
	 * Initialize the contents of the frame.
	 * @param show 
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
		lblResult.setBounds(47, 158, 330, 16);
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
			}
		});
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loginPanel.setVisible(false);
				registerPage(true);
			}
		});
	}
	
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
	
	public void homePage(boolean show) {
		homePanel = new JPanel();
		homePanel.setBounds(0, 0, 432, 253);
		frame.getContentPane().add(homePanel);
		homePanel.setLayout(null);
		homePanel.setVisible(show);
		
		JLabel lblResult = new JLabel("");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(47, 200, 330, 16);
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
	
	private void initialize() {
		frame = new JFrame("CryptoFile");
		frame.setBounds(100, 100, 450, 350);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		lblCryptofile = new JLabel();
		lblCryptofile.setBounds(0, 13, 200, 80);
		lblCryptofile.setIcon(resizeImage("files/logga.png", lblCryptofile));
		lblCryptofile.setFont(new Font("Tahoma", Font.PLAIN, 43));
		lblCryptofile.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblCryptofile);
		loginPage(true);
	}
	
	private ImageIcon resizeImage(String ImagePath, JLabel lbl){
		ImageIcon MyImage = new ImageIcon(ImagePath);
		Image img = MyImage.getImage();
		Image newImg = img.getScaledInstance(lbl.getHeight(), lbl.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon image = new ImageIcon(newImg);
		return image;
	}
}
