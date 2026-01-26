package client.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.net.ConnectionManager;


public class ClientGUI {

	private static JFrame frame;
	private JPanel StartPanel;
	private JTextField ServerIP;
	private JTextField ServerPort;
	private JTextField Alias;
	private ConnectionManager conn;
	private String IPNumber;
	private int PortNumber;
	private String alias;
	private JPanel BulletinPanel;



	/**
	 * Create the application.
	 */
	public ClientGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		// START PANEL VARIABLES
		StartPanel= new JPanel();
		frame.getContentPane().add(StartPanel, BorderLayout.CENTER);
		StartPanel.setLayout(null);
		
		JLabel PortLabel = new JLabel("Server Port");
		PortLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		PortLabel.setBounds(58, 67, 121, 19);
		StartPanel.add(PortLabel);
		
		JLabel ServerIPLabel = new JLabel("Server IP");
		ServerIPLabel.setBounds(58, 34, 85, 19);
		ServerIPLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		StartPanel.add(ServerIPLabel);
		
		ServerIP = new JTextField();
		ServerIP.setBounds(183, 32, 218, 23);
		ServerIP.setFont(new Font("Dialog", Font.PLAIN, 16));
		ServerIPLabel.setLabelFor(ServerIP);
		StartPanel.add(ServerIP);
		ServerIP.setColumns(10);
		
		ServerPort = new JTextField();
		PortLabel.setLabelFor(ServerPort);
		ServerPort.setFont(new Font("Dialog", Font.PLAIN, 16));
		ServerPort.setBounds(183, 65, 218, 23);
		StartPanel.add(ServerPort);
		ServerPort.setColumns(10);
		
		Alias = new JTextField();
		Alias.setFont(new Font("Dialog", Font.PLAIN, 16));
		Alias.setToolTipText("The name that the admin will refer to you as");
		Alias.setBounds(183, 100, 218, 23);
		StartPanel.add(Alias);
		Alias.setColumns(10);
		
		JLabel AliasLabel = new JLabel("Alias/Username");
		AliasLabel.setToolTipText("The name that the admin will refer to you as");
		AliasLabel.setLabelFor(Alias);
		AliasLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		AliasLabel.setBounds(37, 100, 153, 22);
		StartPanel.add(AliasLabel);
		
		JButton Enter = new JButton("Enter");
		Enter.setFont(new Font("Dialog", Font.BOLD, 16));
		Enter.setBounds(142, 222, 146, 32);
		StartPanel.add(Enter);		
		StartPanel.repaint();
		
		Enter.addActionListener(new getInfo());
		
		// BULLETIN BOARD VARIABLES
		
	}
	
	private void bulletinboard() {
		BulletinPanel = new JPanel();
		frame.add(BulletinPanel);
		BulletinPanel.setLayout(null);
		
		JLabel ServerAddress = new JLabel(IPNumber+":"+PortNumber+"		Username : "+ Alias);
		ServerAddress.setBounds(10,10,500,50);
		BulletinPanel.add(ServerAddress);
		
	}
	
	private class getInfo implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			IPNumber = ServerIP.getText();
			PortNumber = Integer.parseInt(ServerPort.getText().replaceAll(" ", ""));
			alias = Alias.getText();
			
			conn.connectToServer(IPNumber, PortNumber, alias);
			conn.start();
			
			if(conn.getConnectionStatus()) {
				StartPanel.setVisible(false);
			}
			
			
		}	
	}
	public String ServerIpAddr() {return IPNumber;}
	public int ServerPortAddr() {return PortNumber;}
	public String ServerAlias() {return alias;}
 	public static void guiError(String message) {JOptionPane.showMessageDialog(frame, message);}
}
