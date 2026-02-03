package client.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

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
	private BoardPanel BulletinPanel;
	private ControlPanel ControlPanel;
	private StatusPanel StatusPanel;



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
				
		conn = new ConnectionManager();
		
	}
	
	private int boardwidth;
	private int boardheight;
	private int notewidth;
	private int noteheight;
	private ArrayList<String> colors = new ArrayList<String>();
	
	
	private void bulletinboard() {
		BulletinPanel = new BoardPanel(conn, boardwidth, boardheight, notewidth, noteheight);
		ControlPanel = new ControlPanel();
		StatusPanel = new StatusPanel();

		frame.add(ControlPanel);
		frame.add(StatusPanel);
	    frame.add(BulletinPanel);
		frame.setSize(boardwidth, boardheight);
		
		JLabel ServerAddress = new JLabel(IPNumber+":"+PortNumber+" Username : "+ alias);
		ServerAddress.setFont(new Font("Dialog", Font.BOLD, 13));
		ServerAddress.setBounds(0,0,300,50);
			
		BulletinPanel.add(ServerAddress);
		addListener();
		
	}
	
    private void showPostDialog() {
        JDialog dialog = new JDialog();
        dialog.setLayout(new FlowLayout());
        
        JTextField xField = new JTextField("10", 5);
        JTextField yField = new JTextField("10", 5);
        String[] colors = {"red", "blue", "green", "yellow", "pink", "white", "cyan"};
        JComboBox<String> colorBox = new JComboBox<>(colors);
        JTextField messageField = new JTextField(20);
        
        dialog.add(new JLabel("X:"));
        dialog.add(xField);
        dialog.add(new JLabel("Y:"));
        dialog.add(yField);
        dialog.add(new JLabel("Color:"));
        dialog.add(colorBox);
        dialog.add(new JLabel("Message:"));
        dialog.add(messageField);
        
        JButton postBtn = new JButton("Post");
        postBtn.addActionListener(e -> {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                String color = (String) colorBox.getSelectedItem();
                String message = messageField.getText();
                
                conn.send("POST "+x+" "+y+" "+color+" "+message);
                
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                StatusPanel.log("Error: Invalid coordinates");
            }
        });
        
        dialog.add(postBtn);
        dialog.pack();
        dialog.setVisible(true);
    }
    
    private void refreshBoard() {
    	StatusPanel.log("Refreshing Board....");
    	
    	String notesResponse = conn.get("GET");
    	System.out.println(notesResponse);
    }
    
    private void showPinDialog(boolean isPin) {
        JDialog dialog = new JDialog(frame, isPin ? "Add Pin" : "Remove Pin", true);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        dialog.setSize(250, 150);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // X coordinate
        JPanel xPanel = new JPanel(new BorderLayout());
        xPanel.add(new JLabel("X:"), BorderLayout.WEST);
        JTextField xField = new JTextField(isPin ? "15" : "15", 10);
        xPanel.add(xField, BorderLayout.CENTER);
        
        // Y coordinate
        JPanel yPanel = new JPanel(new BorderLayout());
        yPanel.add(new JLabel("Y:"), BorderLayout.WEST);
        JTextField yField = new JTextField(isPin ? "12" : "12", 10);
        yPanel.add(yField, BorderLayout.CENTER);
        
        panel.add(xPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(yPanel);
        
        JButton actionBtn = new JButton(isPin ? "Pin" : "Unpin");
        actionBtn.addActionListener(e -> {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                
                String command = isPin ? String.format("PIN %d %d", x, y) : 
                                         String.format("UNPIN %d %d", x, y);
                conn.send(command);
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid coordinates!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(Box.createVerticalStrut(20));
        panel.add(actionBtn);
        
        dialog.add(panel);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
	
    private void addListener() {
    	ControlPanel.getPostButton().addActionListener(e->showPostDialog());
    	ControlPanel.getPinButton().addActionListener(e->showPinDialog(true));
    	ControlPanel.getUnpinButton().addActionListener(e->showPinDialog(false));
    	ControlPanel.getGetButton().addActionListener(e->refreshBoard());
    	ControlPanel.getShakeButton().addActionListener(e-> {
    		conn.send("SHAKE");
    	});
    	ControlPanel.getClearButton().addActionListener(e-> {
    		conn.send("CLEAR");
    	});
    	ControlPanel.getRefreshButton().addActionListener(e->refreshBoard());
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
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					ClientGUI.guiError("ERROR : Could not delay main thread to send message, please close and try again");
				}
				StartPanel.setVisible(false);
				String handshakeinfo = conn.getHandshake();
				String []info = handshakeinfo.split(" ");
				boardwidth = Integer.parseInt(info[0]);
				boardheight = Integer.parseInt(info[1]);
				notewidth = Integer.parseInt(info[2]);
				noteheight = Integer.parseInt(info[3]);
				for(int i = 3; i < info.length; i++)
					colors.add(info[i]);
				
				bulletinboard();
				
			}
			
			
		}	
	}
	public String ServerIpAddr() {return IPNumber;}
	public int ServerPortAddr() {return PortNumber;}
	public String ServerAlias() {return alias;}
 	public static void guiError(String message) {JOptionPane.showMessageDialog(frame, message);}
}
