package GUI;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import Protocol.ClientSignUpEncryption;
import Socket.TCPClient;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Register extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public void fire() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new Register();
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
	public Register() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 650);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("",
				"[150.00,center][85.00,center][85.00][85]",
				"[43.00][30.00][30.00][30][30][30][30][][][][]"));

		JLabel lblSecureAucitionMarketplace = new JLabel(
				"Secure Aucition Marketplace");
		contentPane.add(lblSecureAucitionMarketplace,
				"cell 0 0 4 1,alignx center");

		JLabel lblUserId = new JLabel("User ID:");
		contentPane.add(lblUserId, "cell 0 1,alignx center");

		textField = new JTextField();
		contentPane.add(textField, "cell 1 1 2 1,growx");
		textField.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		contentPane.add(lblPassword, "cell 0 2,alignx center");

		passwordField = new JPasswordField();
		contentPane.add(passwordField, "cell 1 2 2 1,growx");

		JLabel lblRepassword = new JLabel("Verify Password:");
		contentPane.add(lblRepassword, "cell 0 3,alignx center");

		passwordField_1 = new JPasswordField();
		contentPane.add(passwordField_1, "cell 1 3 2 1,growx");

		JLabel lblEmail = new JLabel("E-mail:");
		contentPane.add(lblEmail, "cell 0 4,alignx center");

		textField_1 = new JTextField();
		contentPane.add(textField_1, "cell 1 4 2 1,growx");
		textField_1.setColumns(10);

		JLabel lblVerifyEmail = new JLabel("Verify E-mail:");
		contentPane.add(lblVerifyEmail, "cell 0 5,alignx center");

		textField_2 = new JTextField();
		contentPane.add(textField_2, "cell 1 5 2 1,growx");
		textField_2.setColumns(10);

		JLabel lblSiteKeys = new JLabel("Site Keys:");
		contentPane.add(lblSiteKeys, "cell 0 6");

		JLabel[] siteKeyLabels = new JLabel[9];
		final JRadioButton[] siteKeyButtons = new JRadioButton[9];
		ButtonGroup siteKeyGroup = new ButtonGroup();

		for (int i = 0; i < 9; i++) {
			BufferedImage myPicture = ImageIO.read(new File(
					"./SiteKeys/getSiteKey_" + (i + 1) + ".jpeg"));
			siteKeyLabels[i] = new JLabel(new ImageIcon(myPicture));
			siteKeyButtons[i] = new JRadioButton();
			siteKeyGroup.add(siteKeyButtons[i]);
			contentPane.add(siteKeyButtons[i], "cell " + (i % 3 + 1) + " "
					+ (6 + i / 3));
			contentPane.add(siteKeyLabels[i], "cell " + (i % 3 + 1) + " "
					+ (6 + i / 3));
		}

		JButton button = new JButton("Register");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String userID = textField.getText();
				String email = textField_1.getText();
				String reEmail = textField_2.getText();
				String password = String.valueOf(passwordField.getPassword());
				String rePassword = String.valueOf(passwordField_1
						.getPassword());
				
				if (password.length() > 16 || password.length() < 10) {
					dispose();
					ErrorMessage errM = new ErrorMessage(
							"Password has to be between 10 and 16.");
					errM.fire();
				} else {
					boolean hasNumbers = false;
					boolean hasLetters = false;
					boolean hasPunctuation = false;
					for (int i = 0; i < password.length(); i++) {
						if (password.charAt(i) >= '0'
								&& password.charAt(i) <= '9')
							hasNumbers = true;
						else if (((password.charAt(i) >= 'a' && password
								.charAt(i) <= 'z') || (password.charAt(i) >= 'A' && password
								.charAt(i) <= 'Z')))
							hasLetters = true;
						else if (password.charAt(i) == '!'
								|| password.charAt(i) == '_'
								|| password.charAt(i) == '*')
							hasPunctuation = true;
						else { // illegal character
							ErrorMessage errM = new ErrorMessage(
									"Password contains illegal character, please use 0-9 or a-z or A-Z or punctuations (|, _, *)");
							dispose();
							errM.fire();
							break;
						}
					}

					if (!(hasNumbers && hasLetters && hasPunctuation)) {
						ErrorMessage errM = new ErrorMessage(
								"Password has to contain all the following types, nubmer : 0-9; letter : a-z or A-Z; punctuations: |, _, *");
						dispose();
						errM.fire();
					} else {

						int keyId = 1;
						for (; keyId <= 9;) {
							if (siteKeyButtons[keyId - 1].isSelected())
								break;
							keyId++;
						}
						ClientSignUpEncryption signupEncrypt = null;
						try {
							signupEncrypt = new ClientSignUpEncryption("hao");
						} catch (InvalidKeySpecException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchAlgorithmException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						String mClient = userID + "/" + password + "/" + keyId
								+ "/";
						try {
							String encryptedMsg = new String(signupEncrypt
									.encryptUserSignupMessage(mClient
											.getBytes()));
							TCPClient.sendData("0" + encryptedMsg); // 0 for
																	// register
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						dispose();
						LogIn logIn = new LogIn();
						logIn.fire();
					}
				}
			}
		});
		contentPane.add(button, "cell 2 10");
	}

}
