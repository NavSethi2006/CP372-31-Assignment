package client.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlPanel extends JPanel{
    private JButton postButton, getButton, pinButton, unpinButton;
    private JButton shakeButton, clearButton, refreshButton;
    private JButton zoomInButton, zoomOutButton, resetViewButton;

    public ControlPanel() {
        setOpaque(false);
        setLayout(null); // Floating layout
        setBounds(10, 400, 550, 100);
        // Create buttons
        postButton = createButton("POST", 10, 10, 80, 30);
        getButton = createButton("GET", 100, 10, 80, 30);
        pinButton = createButton("PIN", 190, 10, 80, 30);
        unpinButton = createButton("UNPIN", 280, 10, 80, 30);
        shakeButton = createButton("SHAKE", 370, 10, 80, 30);
        clearButton = createButton("CLEAR", 460, 10, 80, 30);
        refreshButton = createButton("Refresh", 550, 10, 80, 30);
        
        // Zoom controls
        zoomInButton = createButton("+", 650, 10, 40, 30);
        zoomOutButton = createButton("-", 700, 10, 40, 30);
        resetViewButton = createButton("Reset View", 750, 10, 100, 30);
        
        // Set button styles
        setButtonStyles();
        
        // Add buttons to panel
        add(postButton);
        add(getButton);
        add(pinButton);
        add(unpinButton);
        add(shakeButton);
        add(clearButton);
        add(refreshButton);
        add(zoomInButton);
        add(zoomOutButton);
        add(resetViewButton);
    }
    
    private JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setFocusPainted(false);
        return button;
    }
    
    private void setButtonStyles() {
        Color buttonColor = new Color(70, 130, 180); // Steel blue
        Color textColor = Color.WHITE;
        
        JButton[] buttons = {postButton, getButton, pinButton, unpinButton,
                            shakeButton, clearButton, refreshButton,
                            zoomInButton, zoomOutButton, resetViewButton};
        
        for (JButton button : buttons) {
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            button.setFont(new Font("Arial", Font.BOLD, 12));
        }
        
        // Special colors for dangerous operations
        shakeButton.setBackground(new Color(178, 34, 34)); 
        clearButton.setBackground(new Color(178, 34, 34)); 
        zoomInButton.setBackground(new Color(34, 139, 34)); 
        zoomOutButton.setBackground(new Color(34, 139, 34)); 
        resetViewButton.setBackground(new Color(72, 61, 139)); 
    }

    // Getter methods for buttons
    public JButton getPostButton() { return postButton; }
    public JButton getGetButton() { return getButton; }
    public JButton getPinButton() { return pinButton; }
    public JButton getUnpinButton() { return unpinButton; }
    public JButton getShakeButton() { return shakeButton; }
    public JButton getClearButton() { return clearButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getZoomInButton() { return zoomInButton; }
    public JButton getZoomOutButton() { return zoomOutButton; }
    public JButton getResetViewButton() { return resetViewButton; }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw semi-transparent background
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(240, 240, 240, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw border
        g2d.setColor(Color.GRAY);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

}
