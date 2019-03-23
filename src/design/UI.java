package design;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Font;

public class UI {

	private JFrame frame;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPanel loginPanel;
	private JPanel homePanel;
	private JButton btnUpload;
	private JButton btnDowload;
	private JButton btnRegister;
	private JLabel lblCryptofile;

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
		
		usernameField = new JTextField();
		usernameField.setBounds(144, 13, 116, 22);
		loginPanel.add(usernameField);
		usernameField.setColumns(10);
		
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
				if(usernameField.getText().equals("Mattias") && passwordField.getText().equals("hejsan")){
					lblResult.setText("You successfully logged in");
					loginPanel.setVisible(false);
					homePage(true);
				}
				else {
					lblResult.setText("Wrong password/username");
				}
			}
		});
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
	}
	
	public void homePage(boolean show) {
		homePanel = new JPanel();
		homePanel.setBounds(0, 0, 432, 253);
		frame.getContentPane().add(homePanel);
		homePanel.setLayout(null);
		homePanel.setVisible(show);
		
		btnUpload = new JButton("Upload file");
		btnUpload.setBounds(155, 114, 121, 25);
		homePanel.add(btnUpload);
		
		btnDowload = new JButton("Download file");
		btnDowload.setBounds(155, 158, 122, 25);
		homePanel.add(btnDowload);
	}
	
	private void initialize() {
		frame = new JFrame("CryptoFile");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		lblCryptofile = new JLabel("CryptoFile");
		lblCryptofile.setFont(new Font("Tahoma", Font.PLAIN, 43));
		lblCryptofile.setHorizontalAlignment(SwingConstants.CENTER);
		lblCryptofile.setBounds(93, 13, 230, 54);
		frame.getContentPane().add(lblCryptofile);
		loginPage(true);
		homePage(false);
	}
}
