package GUI;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import Protocol.CommunicationProtocol;
import Protocol.ForgetPasswordClient;
import Protocol.LoginProtocolClient;
import Socket.TCPClient;

public class LogInWithSiteKey extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPasswordField passwordField;
	private static String userID;
	private static LoginProtocolClient loginClient;
	private static String siteKey;
	
	/**
	 * Launch the application.
	 */
	public void fire() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogInWithSiteKey frame = new LogInWithSiteKey(userID,
							loginClient, siteKey);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public LogInWithSiteKey(final String userID,
			final LoginProtocolClient loginClient, final String siteID) throws IOException {
		LogInWithSiteKey.userID = userID;
		LogInWithSiteKey.loginClient = loginClient;
		LogInWithSiteKey.siteKey = siteID;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("",
				"[115.00,center][149.00,grow,center][grow,center]",
				"[43.00][49.00][50.00][52.00]"));

		JLabel lblSecureAucitionMarketplace = new JLabel(
				"Secure Aucition Marketplace");
		contentPane.add(lblSecureAucitionMarketplace,
				"cell 0 0 3 1,alignx center");

		JLabel lblUserId = new JLabel("User ID:");
		contentPane.add(lblUserId, "cell 0 1,alignx center");

		JLabel lblNAiQiao = new JLabel(userID);
		contentPane.add(lblNAiQiao, "cell 1 1");

		JLabel lblSiteKey = new JLabel("Site Key:");
		contentPane.add(lblSiteKey, "cell 0 2,alignx center");
		System.out.println("siteID = " + siteID);
		BufferedImage myPicture = ImageIO.read(new File(
				"./SiteKeys/getSiteKey_" + siteID +".jpeg"));
		JLabel lblNewLabel = new JLabel(new ImageIcon(myPicture));
		contentPane.add(lblNewLabel, "cell 1 2");

		JLabel lblPassword = new JLabel("Password:");
		contentPane.add(lblPassword, "cell 0 3,alignx center");

		passwordField = new JPasswordField();
		contentPane.add(passwordField, "cell 1 3,growx");

		//add by cyr
		JButton btnForgetPassword = new JButton("Forget Password");
		btnForgetPassword.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				String questionString = null;
				
					ForgetPasswordClient mClient = new ForgetPasswordClient();
					try {
						TCPClient.sendData("3" + new String(mClient.forgetPassword(userID)));
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					String s = TCPClient.getData();
					//System.out.println("The get String is:" + s);
					
					try {
						questionString = new String(mClient.decryptQuesitonClient(s.getBytes()));
						System.out.println("The getted question and answer is:" + questionString);
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
			
				//ForgetPassword forgetpw = null;
				try {
					ForgetPassword forgetpw = new ForgetPassword (userID, questionString, siteKey, loginClient);
					//ForgetPassword forgetpw = new ForgetPassword ("1", "1/2/3");
					ForgetPassword.fire();
				}catch (Exception e1){
					e1.printStackTrace();
				}				
			}
		});
		contentPane.add(btnForgetPassword, "cell 1 4");
		
		JButton btnLogOn = new JButton("Log On");
		contentPane.add(btnLogOn, "cell 2 3,alignx center");	
		btnLogOn.addMouseListener(new MouseAdapter() {
			private Hall hall;

			@Override
			public void mouseClicked(MouseEvent e) {
				byte[] sendMsg = null;

				try {
					sendMsg = loginClient.thridStepClient(userID + "/"
							+ new String(passwordField.getPassword()));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				TCPClient.sendData(new String(sendMsg));
				try {

					String sessionKeyMsg = TCPClient.getData();
					dispose();
					if (sessionKeyMsg.substring(0, 5).equals("ERROR")) {
						ErrorMessage errM = new ErrorMessage(sessionKeyMsg);
						errM.fire();
					} else {
						loginClient.fourthStepClient(sessionKeyMsg.getBytes());
						CommunicationProtocol cp = new CommunicationProtocol(loginClient.getSessionKey(),
								loginClient.getIVParameterSessionKey());
						hall = new Hall(userID, cp);
						Hall.fire();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});

	}

}
