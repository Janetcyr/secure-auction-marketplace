package GUI;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import Protocol.LoginProtocolClient;
import Socket.TCPClient;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class LogIn extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JFrame frame;
	
	/**
	 * Launch the application.
	 */
	public void fire() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new LogIn();
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
	public LogIn() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow,center][grow,center][grow,center]", "[43.00][72.00][80.00]"));
		
		JLabel lblSecureAucitionMarketplace = new JLabel("Secure Aucition Marketplace");
		contentPane.add(lblSecureAucitionMarketplace, "cell 0 0 3 1,alignx center");
		
		JLabel lblUserId = new JLabel("User ID:");
		contentPane.add(lblUserId, "cell 0 1,alignx trailing");
		
		textField = new JTextField();
		textField.setText("");
		contentPane.add(textField, "cell 1 1,growx");
		textField.setColumns(10);
		
		JButton btnLogOn = new JButton("Log On");
		contentPane.add(btnLogOn, "cell 2 1");
		
		JButton btnNewButton = new JButton("Register");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
				Register register;
				try {
					register = new Register();
					register.fire();	
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(btnNewButton, "flowx,cell 1 2");
		
		JButton btnNewButton_1 = new JButton("Forgot ID");
		contentPane.add(btnNewButton_1, "cell 1 2");
		
		btnLogOn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					String userID = textField.getText();
					LoginProtocolClient loginClient = new LoginProtocolClient();
					TCPClient.sendData("1" + new String(loginClient.firstStepClient(userID)));
					String s = TCPClient.getData();
					String siteID = new String(loginClient.secondStepClient(s.getBytes()));
					dispose();
					if (s.substring(0, 5).equals("ERROR")) {
					
						ErrorMessage errM = new ErrorMessage(s);
						errM.fire();
					} else {
						LogInWithSiteKey logInWithSiteKey = new LogInWithSiteKey(
								userID, loginClient, siteID);
						logInWithSiteKey.fire();
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
