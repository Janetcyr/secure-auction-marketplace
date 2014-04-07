package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import Protocol.CommunicationProtocol;
import Protocol.LoginProtocolClient;
import Socket.TCPClient;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

public class UploadProgramBot extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JButton btnSubmit;
	private static String usrID;
	private static CommunicationProtocol cp;

	/**
	 * Launch the application.
	 */
	public static void fire() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UploadProgramBot frame = new UploadProgramBot(usrID, cp);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @param userID 
	 */
	public UploadProgramBot(final String userID, final CommunicationProtocol cp) {
		
		this.usrID = userID;
		this.cp = cp;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));
		
		JLabel lblSetAutoBot = new JLabel("Set Automation Bidding Bot");
		contentPane.add(lblSetAutoBot, "cell 1 0");
		
		JLabel lblItem = new JLabel("Item");
		contentPane.add(lblItem, "cell 0 1,alignx trailing");
		
		textField = new JTextField();
		contentPane.add(textField, "cell 1 1,growx");
		textField.setColumns(10);
		
		JLabel lblMaxPrice = new JLabel("Max Price");
		contentPane.add(lblMaxPrice, "cell 0 2,alignx trailing");
		
		textField_1 = new JTextField();
		contentPane.add(textField_1, "cell 1 2,growx");
		textField_1.setColumns(10);
		
		JLabel lblIncrement = new JLabel("Increment");
		contentPane.add(lblIncrement, "cell 0 3,alignx trailing");
		
		textField_2 = new JTextField();
		contentPane.add(textField_2, "cell 1 3,growx");
		textField_2.setColumns(10);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {				
				if(cp != null) {
				
					try {
						TCPClient.sendData("2"
								+ new String(cp.encryptMessage(userID,
										("3" + userID + "/" + textField.getText()+ "/" + textField_1.getText() + "/" + textField_2.getText()))));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//String s = TCPClient.getData();
				}
			}
		});
	
		contentPane.add(btnSubmit, "cell 1 4");
	}

}
