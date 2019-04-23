package Session;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SessionTestUI extends JFrame{

	private Session session = new Session("test");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame.setDefaultLookAndFeelDecorated(true);
					SessionTestUI frame = new SessionTestUI();
					frame.setTitle("Test");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//					frame.pack();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SessionTestUI() {
		setSize(new Dimension(200,80));

		JButton btnLogin = new JButton("Restart Timer");
		btnLogin.setBounds(144, 82, 116, 25);
		add(btnLogin);

		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				session.restartTimer();
			}
		});
	}

}
