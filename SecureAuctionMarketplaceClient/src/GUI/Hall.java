package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import Socket.TCPClient;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Model.BidInfo;
import Model.Serialize;
import Protocol.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.BoxLayout;

public class Hall extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private static String userID;
	private static LoginProtocolClient loginClient;
	private static CommunicationProtocol cp;

	/**
	 * Launch the application.
	 */
	public static void fire() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Hall frame = new Hall(userID, cp);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @wbp.parser.constructor
	 */
	public Hall(final String userID, final CommunicationProtocol cp) {
		this.userID = userID;
		this.cp = cp;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 900);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane
				.setLayout(new MigLayout("", "[grow]", "[16.00][527.00][][]"));

		JButton btnNewButton = new JButton("Log Out");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
				LogIn logIn = new LogIn();
				logIn.fire();
			}
		});
		// add by chenxi
		JButton btnNewButton_2 = new JButton("Change Password");
		btnNewButton_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
				// ChangePassword changePassword = new ChangePassword();
				// changePassword.fire();
			}
		});
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		contentPane.add(btnNewButton_2, "flowx,cell 0 0,alignx right");
		// add by chenxi
		contentPane.add(btnNewButton, "cell 0 0,alignx right");

		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, "cell 0 1,grow");

		final JPanel[] panels = new JPanel[3];
		panels[0] = new JPanel();
		tabbedPane.addTab("Auction House", null, panels[0], null);
		panels[0].setLayout(new MigLayout("", "[1px][][547.00,grow]",
				"[1px][][]"));

		// change by chenxi
		panels[1] = new JPanel();
		tabbedPane.addTab("My Bids", null, panels[1], null);

		// add by chenxi
		panels[2] = new JPanel();
		tabbedPane.addTab("My Selling", null, panels[2], null);
		panels[2].setLayout(new MigLayout("", "[805px]", "[400px]"));

		// create a jtextarea
		JTextArea textArea = new JTextArea();
		textArea.setBackground(Color.WHITE);

		// add text to it; we want to make it scroll
		textArea.setText("xx1\nxx2\nxx\nxx\nxx\nxx\nxx\nxx\nxx\nxx\nxx\nxx\nxx\nxx\n");

		JScrollPane scrollPane = new JScrollPane(textArea);
		panels[2].add(scrollPane, "cell 0 0,grow");

		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int index = tabbedPane.getSelectedIndex();
				if (index == 0) {
					String col[] = { "Item", "Seller", "Price", "Start", "End" };
					try {
						TCPClient.sendData("2" + new String(cp.encryptMessage(userID, "4")));
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
					
					HashMap<String, BidInfo> map;
					try {
						String s = cp.decryptMessage(TCPClient.getData().getBytes());
						System.out.println(s);
						map = (HashMap<String, BidInfo>) Serialize.convertStringToOjbect(s);
						Set<String> set = map.keySet();
						
						DefaultTableModel model = new DefaultTableModel(col, set.size());
						JTable table = new JTable(model) {
							@Override
							public boolean isCellEditable(int arg0, int arg1) {

								return false;
							}
						};
						int i = 0;
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
						for(String bidID : set) {
							table.setValueAt(bidID, i, 0);
							table.setValueAt(map.get(bidID).getSeller(), i, 1);
							table.setValueAt(String.valueOf(map.get(bidID).getPrice()), i, 2);
							table.setValueAt(sdf.format(map.get(bidID).getStart()), i, 3);
							table.setValueAt(sdf.format(map.get(bidID).getEnd()), i, 4);
						}
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
					

				} else if (index == 1) {

				} else {

				}
				panels[index].setVisible(true);
			}
		});

		JButton btnNewButton_3 = new JButton("Create a new auction");
		btnNewButton_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
				CreateNewAuction createNewAuction = new CreateNewAuction(
						userID, cp);
				createNewAuction.fire();
			}
		});

		contentPane.add(btnNewButton_3, "flowx,cell 0 3,alignx center");

		JButton btnBidForNew = new JButton("Bid an item");
		btnBidForNew.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
				BidNewItem bidNewItem = new BidNewItem(userID, cp);
				bidNewItem.fire();
			}
		});
		btnBidForNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		contentPane.add(btnBidForNew, "cell 0 3");

		JButton btnNewButton_4 = new JButton("Upload a Bot");
		btnNewButton_4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UploadProgramBot up = new UploadProgramBot(userID, cp);
				up.fire();
			}
		});
		contentPane.add(btnNewButton_4, "cell 0 3");

	}

}
