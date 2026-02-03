package client.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StatusPanel extends JPanel{
	
    private JLabel statusLabel;
    private JTextArea logArea;

    
    public StatusPanel() {
        setOpaque(false);
        setLayout(null);
        setBounds(10, 50, 200, 200);
        
        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setBounds(10, 10, 100, 20);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(Color.BLUE);
        add(statusLabel);
        
        // Log area
        logArea = new JTextArea();
        logArea.setBounds(10, 40, 180, 150);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setBackground(new Color(255, 255, 240));
        logArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBounds(10, 40, 180, 150);
        add(scrollPane);
        
    }
    
    public void setStatus(String status) {
        statusLabel.setText("Status: " + status);
    }
    
    public void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    public void clearLog() {
        logArea.setText("");
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw semi-transparent background
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw border
        g2d.setColor(Color.GRAY);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

}
