package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ErrorMessage extends JFrame {

	private JPanel contentPane;
	private static String error;

	/**
	 * Launch the application.
	 */
	public void fire() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ErrorMessage frame = new ErrorMessage(error);
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
	public ErrorMessage(String s) {
		
		error = s;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[73px]", "[16px][]"));
		
		JLabel lblCannotConnectTo = new JLabel(error);
		lblCannotConnectTo.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblCannotConnectTo, "cell 0 0,alignx center,aligny center");
		
		JButton btnNewButton = new JButton("Back To LogIn");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
				LogIn logIn = new LogIn();
				logIn.fire();
			}
		});
		contentPane.add(btnNewButton, "cell 0 1,growx");
	}

}
