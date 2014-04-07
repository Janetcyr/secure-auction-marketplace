package GUI;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import Protocol.ForgetPasswordClient;
import Protocol.LoginProtocolClient;
import Socket.TCPClient;

public class ResetPassword extends JFrame {

	private JPanel contentPane;
	private JPasswordField passwordField;
	private JLabel lblConfirmNewPassword;
	private JButton btnNewButton;
	private static String userID;
	private JPasswordField passwordField_1;
	private JPasswordField passwordField_2;
	private static LoginProtocolClient loginClient;
	//THe three questions of the secure question
	private static String questionsandAnswers;
	private static String siteKey;
	

	/**
	 * Launch the application.
	 */
	public static void fire() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ResetPassword frame = new ResetPassword(userID, siteKey, loginClient);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ResetPassword(final String userID, final String siteKey, final LoginProtocolClient loginClient) {
		ResetPassword.userID = userID;
		ResetPassword.siteKey = siteKey;
		ResetPassword.loginClient = loginClient;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow]", "[][][][][][][]"));
		
		JLabel lblNewPassword = new JLabel("New Password:");
		contentPane.add(lblNewPassword, "cell 1 0");
		
		passwordField_1 = new JPasswordField();
		contentPane.add(passwordField_1, "cell 1 1,growx");
		
		lblConfirmNewPassword = new JLabel("Confirm New Password:");
		contentPane.add(lblConfirmNewPassword, "cell 1 2");
		
		passwordField_2 = new JPasswordField();
		contentPane.add(passwordField_2, "cell 1 3,growx");
		
		btnNewButton = new JButton("Send");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String passwordnew = String.valueOf(passwordField_1.getPassword());
				String passwordnewconfirm = String.valueOf(passwordField_2.getPassword());
				String usnerNamePassword = ResetPassword.userID + "/" + passwordnew;
				ForgetPasswordClient client = new ForgetPasswordClient();
				try {
					TCPClient.sendData("4" + new String(client.ResetPassword(usnerNamePassword)));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				LogInWithSiteKey logInWithSiteKey;
				try {
					logInWithSiteKey = new LogInWithSiteKey(
							userID, loginClient, siteKey);
					logInWithSiteKey.fire();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
			}
		});
		contentPane.add(btnNewButton, "cell 1 6,alignx center");
	}

}
