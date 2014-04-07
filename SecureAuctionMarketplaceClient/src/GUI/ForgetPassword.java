package GUI;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Protocol.LoginProtocolClient;

import net.miginfocom.swing.MigLayout;

public class ForgetPassword extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JLabel lblSecureQuestion_1;
	private JTextField textField_1;
	private JLabel lblSecureQuestion_2;
	private JTextField textField_2;
	private JButton btnNewButton;
	private static String userID;
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
					ForgetPassword frame = new ForgetPassword(userID,questionsandAnswers, siteKey, loginClient);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @param question2 
	 * @param userID2 
	 */
	public ForgetPassword(final String userID, final String questionAndAnswers, final String siteKey, final LoginProtocolClient loginClient) {
		ForgetPassword.userID = userID;
		ForgetPassword.questionsandAnswers = questionAndAnswers;
		ForgetPassword.siteKey = siteKey;
		ForgetPassword.loginClient = loginClient;
		String[] questions = ForgetPassword.questionsandAnswers.split("_");
		Set<String> questionSet = new HashSet<String>();
		questionSet.add(questions[0]);
		questionSet.add(questions[1]);
		questionSet.add(questions[2]);
		final HashSet<String> theCorrect = new HashSet<String>(questionSet);
		final String question1 = questions[0].split("/")[0];
		final String question2 = questions[1].split("/")[0];
		final String question3 = questions[2].split("/")[0];


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[376.00,grow][][][][grow]", "[][pref!][][][][][][]"));
		
		JLabel lblSecureQuestion = new JLabel("Secure Question 1:" + question1);
		contentPane.add(lblSecureQuestion, "cell 0 0");
		
		textField = new JTextField();
		contentPane.add(textField, "cell 0 1,growx");
		textField.setColumns(10);
		
		lblSecureQuestion_1 = new JLabel("Secure Question 2:" + question2);
		contentPane.add(lblSecureQuestion_1, "cell 0 2");
		
		textField_1 = new JTextField();
		contentPane.add(textField_1, "cell 0 3,growx");
		textField_1.setColumns(10);
		
		lblSecureQuestion_2 = new JLabel("Secure Question 3:" + question3);
		contentPane.add(lblSecureQuestion_2, "cell 0 4");
		
		textField_2 = new JTextField();
		contentPane.add(textField_2, "cell 0 5,growx");
		textField_2.setColumns(10);
		
		btnNewButton = new JButton("Send");
		StringBuilder sb = new StringBuilder();
		String answer1 = textField.getText();
		String answer2 = textField_1.getText();
		String answer3 = textField_2.getText();
		
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
				String answer1 = textField.getText();
				String answer2 = textField_1.getText();
				String answer3 = textField_2.getText();
				String thequestion1 = question1;
				String thequestion2 = question2;
				String thequestion3 = question3;
				HashSet<String> theAnswers = new HashSet<String>();
				theAnswers.add(thequestion1 + "/" + answer1);
				theAnswers.add(thequestion2 + "/" + answer2);
				theAnswers.add(thequestion3 + "/" + answer3);
				boolean isEqual = theAnswers.equals(theCorrect);
				System.out.println("If they are equal:" + isEqual);
				if(isEqual)
				{
					ResetPassword reset = new ResetPassword (userID, siteKey, loginClient);
					ResetPassword.fire();
				} else
				{
					
				}
			}
		});
	    
		contentPane.add(btnNewButton, "cell 0 7,alignx center");
	}

}
