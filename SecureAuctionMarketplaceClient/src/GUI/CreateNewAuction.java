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
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;

import Model.BidInfo;
import Protocol.CommunicationProtocol;
import Protocol.LoginProtocolClient;
import Socket.TCPClient;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateNewAuction extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JButton btnSuBMi;
	private static String userID;
	private static CommunicationProtocol cp;

	/**
	 * Launch the application.
	 */
	public void fire() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CreateNewAuction frame = new CreateNewAuction(userID, cp);
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
	public CreateNewAuction(final String userID, final CommunicationProtocol cp) {
		this.userID = userID;
		this.cp = cp;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[30px][30px][grow]",
				"[][][30px][30px][30px][30px][]"));

		JLabel lblCreateANew = new JLabel("Create a new auction");
		lblCreateANew.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		contentPane.add(lblCreateANew, "cell 1 0");

		JLabel lblSetItemName = new JLabel("Set Item Name");
		contentPane.add(lblSetItemName, "cell 1 2,alignx left");

		textField = new JTextField();
		contentPane.add(textField, "cell 2 2,growx");
		textField.setColumns(10);

		JLabel lblSetStartTime = new JLabel("Set Start Price");
		contentPane.add(lblSetStartTime, "cell 1 3,alignx left");

		textField_1 = new JTextField();
		contentPane.add(textField_1, "cell 2 3,growx");
		textField_1.setColumns(10);

		JLabel lblSetStartTime_1 = new JLabel("Set Start Time");
		contentPane.add(lblSetStartTime_1, "cell 1 4,alignx left");

		textField_2 = new JTextField();
		contentPane.add(textField_2, "cell 2 4,growx");
		textField_2.setColumns(10);

		JLabel lblSetEndTime = new JLabel("Set End Time");
		contentPane.add(lblSetEndTime, "cell 1 5,alignx left");

		textField_3 = new JTextField();
		contentPane.add(textField_3, "cell 2 5,growx");
		textField_3.setColumns(10);

		btnSuBMi = new JButton("submit");
		btnSuBMi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String itemName = textField.getText();
				String startPriceString = textField_1.getText();
				double startPriceDouble = Double.valueOf(startPriceString);
				String startTime = textField_2.getText();
				String endTime = textField_3.getText();
				Calendar start = Calendar.getInstance();
				Calendar end = Calendar.getInstance();
				// change by chenxi
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd-hh.mm.ss");// SimpleDateFormat sdf = new
												// SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

				try {
					start.setTime(sdf.parse(startTime));
					// System.out.println("check calender time :"+sdf.format(start.getTime()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					end.setTime(sdf.parse(endTime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BidInfo bidInfo = new BidInfo(userID, null, startPriceDouble,
						start, end);
				String msgToSend = itemName + "/" + bidInfo.toString();
				
				

				
					try {
						// communication procedure 1 is create a new auction
						TCPClient.sendData("2"
								+ new String(cp.encryptMessage(userID,
										"1" + msgToSend)));
						dispose();
						Hall hall = new Hall(userID, cp);
						hall.fire();
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

				}

			
		});
		contentPane.add(btnSuBMi, "cell 2 6,alignx right");
	}

}
