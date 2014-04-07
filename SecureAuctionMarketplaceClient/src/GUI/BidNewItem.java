package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Protocol.CommunicationProtocol;
import Socket.TCPClient;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class BidNewItem extends JFrame {

	private JPanel contentPane;
	private static String userID;
	private static CommunicationProtocol cp;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public void fire() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BidNewItem frame = new BidNewItem(userID, cp);
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
	public BidNewItem(final String userID, final CommunicationProtocol cp) {
		this.userID = userID;
		this.cp = cp;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][]"));
		
		JLabel lblBidANew = new JLabel("Bid a new item");
		lblBidANew.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		contentPane.add(lblBidANew, "cell 1 1");
		
		JLabel lblItemid = new JLabel("ItemID");
		lblItemid.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		contentPane.add(lblItemid, "cell 1 3,alignx left");
		
		textField = new JTextField();
		contentPane.add(textField, "cell 2 3,growx");
		textField.setColumns(10);
		
		JLabel lblBidPrice = new JLabel("Bid Price");
		lblBidPrice.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		contentPane.add(lblBidPrice, "cell 1 5,alignx left");
		
		textField_1 = new JTextField();
		contentPane.add(textField_1, "cell 2 5,growx");
		textField_1.setColumns(10);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String itemID = textField.getText();
				String bidPrice = textField_1.getText();
				String msgToSend = itemID + "/" + userID + "/" + bidPrice;

				// communication procedure 2 is bid a new auction
				try {
					TCPClient.sendData("2"
							+ new String(cp.encryptMessage(userID, "2" + msgToSend)));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalBlockSizeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (BadPaddingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dispose();
				Hall hall = new Hall(userID, cp);
				hall.fire();

			}
		});
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		contentPane.add(btnSubmit, "cell 2 6,alignx right");
	}

}
