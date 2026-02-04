package client.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import client.net.ConnectionManager;

public class BoardPanel extends JPanel{

    private float scale = 3.0f;
    
    // Data collections
    public List<Note> notes = new ArrayList<>();
    public List<Pin> pins = new ArrayList<>();
    
    // Colors
    private final Color BOARD_COLOR = new Color(245, 245, 220); // Light beige
    private final Color GRID_COLOR = new Color(220, 220, 200);
    private final Color BORDER_COLOR = new Color(139, 69, 19); // Brown
    private final Color PIN_COLOR = new Color(220, 20, 60); // Crimson red
  
    
    // Mouse interaction
    private Point dragStart = null;
    private Point boardOffset = new Point(20, 20); // Offset from panel edges
    
    ConnectionManager conn;
    
    private int boardWidth;
    private int boardHeight;
    public int noteWidth;
    public int noteHeight;
    
    public BoardPanel(ConnectionManager conn, int boardWidth, int boardHeight, int noteWidth, int noteHeight) {
    	this.conn = conn;
    	this.boardWidth = boardWidth;
    	this.boardHeight = boardHeight;
    	this.noteWidth = noteWidth;
    	this.noteHeight = noteHeight;
    	
    	notes = new ArrayList<Note>();
    	pins = new ArrayList<Pin>();
    	
    	
        setOpaque(false); // Transparent background
        setPreferredSize(new Dimension(800, 600));
        
        // Enable mouse interaction for dragging/panning
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    dragStart = e.getPoint();
                }
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                dragStart = null;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (dragStart != null) {
                    Point current = e.getPoint();
                    boardOffset.x += current.x - dragStart.x;
                    boardOffset.y += current.y - dragStart.y;
                    dragStart = current;
                    repaint();
                }
            }
        });
        
        // Mouse wheel for zoom
        addMouseWheelListener(e -> {
            float zoomFactor = 1.1f;
            if (e.getWheelRotation() < 0) {
                // Zoom in
                scale *= zoomFactor;
            } else {
                // Zoom out
                scale /= zoomFactor;
            }
            scale = Math.max(0.5f, Math.min(scale, 10.0f)); // Clamp between 0.5 and 10
            repaint();
        });
    }
    
   
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Get panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        // Calculate board dimensions with scale
        float scaledBoardWidth = boardWidth * scale;
        float scaledBoardHeight = boardHeight * scale;
        
        // Draw subtle gradient background for the entire panel
        GradientPaint bgGradient = new GradientPaint(
            0, 0, new Color(240, 245, 255),
            panelWidth, panelHeight, new Color(220, 230, 245)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, panelWidth, panelHeight);
        
        // Draw drop shadow for board
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fillRoundRect(
            boardOffset.x + 5, 
            boardOffset.y + 5, 
            (int) scaledBoardWidth, 
            (int) scaledBoardHeight, 
            10, 10
        );
        
        // Draw board background with texture
        drawBoardBackground(g2d, boardOffset.x, boardOffset.y, 
                           (int) scaledBoardWidth, (int) scaledBoardHeight);
        
        // Draw subtle grid lines
        drawGrid(g2d, boardOffset.x, boardOffset.y, 
                (int) scaledBoardWidth, (int) scaledBoardHeight);
        
        // Draw all notes (with shadows)
        for (Note note : notes) {
            drawNoteWithShadow(g2d, note);
        }
        
        // Draw all pins
        for (Pin pin : pins) {
            drawPin(g2d, pin);
        }
        
        // Draw board border with wood texture effect
        drawBoardBorder(g2d, boardOffset.x, boardOffset.y, 
                       (int) scaledBoardWidth, (int) scaledBoardHeight);
        
        // Draw scale info in corner
        drawInfoOverlay(g2d);
    }
    
    private void drawBoardBackground(Graphics2D g2d, int x, int y, int width, int height) {
        // Corkboard texture effect
        g2d.setColor(BOARD_COLOR);
        g2d.fillRoundRect(x, y, width, height, 8, 8);
        
        // Add subtle texture dots
        g2d.setColor(new Color(220, 210, 180));
        int dotSpacing = (int)(10 * scale);
        for (int dx = x + dotSpacing; dx < x + width; dx += dotSpacing) {
            for (int dy = y + dotSpacing; dy < y + height; dy += dotSpacing) {
                if (Math.random() > 0.7) { // Random dots
                    int dotSize = (int)(1 * scale);
                    g2d.fillOval(dx, dy, dotSize, dotSize);
                }
            }
        }
    }
    
    private void drawGrid(Graphics2D g2d, int x, int y, int width, int height) {
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(0.5f));
        
        // Vertical lines every 10 units
        for (int i = 0; i <= boardWidth; i += 10) {
            int lineX = x + (int)(i * scale);
            g2d.drawLine(lineX, y, lineX, y + height);
        }
        
        // Horizontal lines every 10 units
        for (int i = 0; i <= boardHeight; i += 10) {
            int lineY = y + (int)(i * scale);
            g2d.drawLine(x, lineY, x + width, lineY);
        }
    }
    
    private void drawNoteWithShadow(Graphics2D g2d, Note note) {
        int noteX = boardOffset.x + (int)(note.getX() * scale);
        int noteY = boardOffset.y + (int)(note.getY() * scale);
        int noteWidthScaled = (int)(note.getWidth() * scale);
        int noteHeightScaled = (int)(note.getHeight() * scale);
        
        // Draw shadow
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillRoundRect(
            noteX + (int)(2 * scale), 
            noteY + (int)(2 * scale), 
            noteWidthScaled, 
            noteHeightScaled, 
            (int)(4 * scale), 
            (int)(4 * scale)
        );
        
        // Draw note body with gradient
        GradientPaint noteGradient = new GradientPaint(
            noteX, noteY, note.getColor(),
            noteX + noteWidthScaled, noteY + noteHeightScaled, 
            note.getColor().darker()
        );
        g2d.setPaint(noteGradient);
        g2d.fillRoundRect(noteX, noteY, noteWidthScaled, noteHeightScaled, 
                         (int)(4 * scale), (int)(4 * scale));
        
        // Draw note border
        if (note.isPinned()) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
        } else {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
        }
        g2d.drawRoundRect(noteX, noteY, noteWidthScaled, noteHeightScaled, 
                         (int)(4 * scale), (int)(4 * scale));
        
        // Draw message text
        drawNoteText(g2d, note, noteX, noteY, noteWidthScaled, noteHeightScaled);
        
        // Draw pin indicator if pinned
        if (note.isPinned()) {
            drawPinIndicator(g2d, noteX + noteWidthScaled/2, noteY - (int)(3 * scale));
        }
    }
    
    private void drawNoteText(Graphics2D g2d, Note note, int x, int y, int width, int height) {
        String message = note.getMessage();
        
        // Wrap text if too long
        Font font = new Font("SansSerif", Font.PLAIN, Math.max(4, (int)(4 * scale)));
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        
        // Split text into lines that fit within note width
        List<String> lines = new ArrayList<>();
        String[] words = message.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine + (currentLine.length() > 0 ? " " : "") + word;
            if (fm.stringWidth(testLine) < width * 0.9) {
                currentLine.append(currentLine.length() > 0 ? " " : "").append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                }
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        // Limit to 3 lines maximum
        if (lines.size() > 3) {
            lines = lines.subList(0, 3);
            lines.set(2, lines.get(2) + "...");
        }
        
        // Draw each line centered
        g2d.setColor(Color.BLACK);
        int lineHeight = fm.getHeight();
        int totalTextHeight = lineHeight * lines.size();
        int startY = y + (height - totalTextHeight) / 2 + fm.getAscent();
        
        for (int i = 0; i < lines.size(); i++) {
            int lineWidth = fm.stringWidth(lines.get(i));
            int lineX = x + (width - lineWidth) / 2;
            g2d.drawString(lines.get(i), lineX, startY + (i * lineHeight));
        }
    }
    
    private void drawPinIndicator(Graphics2D g2d, int x, int y) {
        // Draw a small pushpin
        g2d.setColor(new Color(184, 134, 11)); // Golden rod
        g2d.fillOval(x - (int)(2 * scale), y, (int)(4 * scale), (int)(4 * scale));
        
        // Pin shaft
        g2d.setColor(new Color(160, 120, 10));
        g2d.fillRect(x - (int)(0.5 * scale), y + (int)(4 * scale), 
                    (int)(1 * scale), (int)(6 * scale));
    }
    
    private void drawPin(Graphics2D g2d, Pin pin) {
        int pinX = boardOffset.x + (int)(pin.getX() * scale);
        int pinY = boardOffset.y + (int)(pin.getY() * scale);
        int pinSize = (int)(6 * scale);
        
        // Draw pin with shine effect
        GradientPaint pinGradient = new GradientPaint(
            pinX - pinSize/2, pinY - pinSize/2, PIN_COLOR,
            pinX + pinSize/2, pinY + pinSize/2, PIN_COLOR.darker()
        );
        g2d.setPaint(pinGradient);
        g2d.fillOval(pinX - pinSize/2, pinY - pinSize/2, pinSize, pinSize);
        
        // Draw pin highlight
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.fillOval(pinX - pinSize/4, pinY - pinSize/4, pinSize/2, pinSize/2);
        
        // Draw pin outline
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(0.5f));
        g2d.drawOval(pinX - pinSize/2, pinY - pinSize/2, pinSize, pinSize);
    }
    
    private void drawBoardBorder(Graphics2D g2d, int x, int y, int width, int height) {
        // Draw wood texture border
        Color woodColor1 = new Color(139, 69, 19); // Saddle brown
        Color woodColor2 = new Color(160, 82, 45); // Sienna
        
        // Outer border
        g2d.setColor(woodColor1);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(x - 2, y - 2, width + 4, height + 4, 10, 10);
        
        // Inner border with wood grain effect
        g2d.setColor(woodColor2);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, width, height, 8, 8);
        
        // Draw corner brackets
        int bracketSize = (int)(10 * scale);
        g2d.setColor(new Color(101, 67, 33)); // Darker wood
        g2d.setStroke(new BasicStroke(3));
        
        // Top-left bracket
        g2d.drawLine(x, y, x + bracketSize, y);
        g2d.drawLine(x, y, x, y + bracketSize);
        
        // Top-right bracket
        g2d.drawLine(x + width, y, x + width - bracketSize, y);
        g2d.drawLine(x + width, y, x + width, y + bracketSize);
        
        // Bottom-left bracket
        g2d.drawLine(x, y + height, x + bracketSize, y + height);
        g2d.drawLine(x, y + height, x, y + height - bracketSize);
        
        // Bottom-right bracket
        g2d.drawLine(x + width, y + height, x + width - bracketSize, y + height);
        g2d.drawLine(x + width, y + height, x + width, y + height - bracketSize);
    }
    
    private void drawInfoOverlay(Graphics2D g2d) {
        // Draw scale and info in bottom-right corner
        String info = String.format("Scale: %.1fx | Notes: %d | Pins: %d", 
                                   scale, notes.size(), pins.size());
        
        Font infoFont = new Font("Monospaced", Font.PLAIN, 12);
        g2d.setFont(infoFont);
        FontMetrics fm = g2d.getFontMetrics();
        
        // Semi-transparent background for text
        int padding = 5;
        int textWidth = fm.stringWidth(info);
        int textHeight = fm.getHeight();
        int infoX = getWidth() - textWidth - padding - 5;
        int infoY = getHeight() - padding - 5;
        
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(infoX - padding, infoY - textHeight, 
                         textWidth + padding*2, textHeight + padding, 5, 5);
        
        // Text
        g2d.setColor(Color.WHITE);
        g2d.drawString(info, infoX, infoY);
        
        // Instructions
        String instructions = "Right-click + drag: Pan | Mouse wheel: Zoom";
        int instWidth = fm.stringWidth(instructions);
        int instX = getWidth() - instWidth - padding - 5;
        int instY = infoY - textHeight - 5;
        
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(instX - padding, instY - textHeight, 
                         instWidth + padding*2, textHeight + padding, 5, 5);
        
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString(instructions, instX, instY);
    }
    
    // ================ PUBLIC METHODS ================
    

    

    public void updateBoard(List<Note> notes, List<Pin> pins) {
        // Update the local collections
        this.notes = new ArrayList<>(notes);
        this.pins = new ArrayList<>(pins);
        
        // Trigger repaint on EDT
        SwingUtilities.invokeLater(this::repaint);
    }

    public void refreshDisplay() {
        SwingUtilities.invokeLater(this::repaint);
    }
    
    public void updateNotes(List<Note> notes) {
        this.notes = notes;
        repaint();
    }
    
    public void updatePins(List<Pin> pins) {
        this.pins = pins;
        repaint();
    }
    
    public void addNote(Note note) {
        this.notes.add(note);
        repaint();
    }
    
    public void addPin(Pin pin) {
        this.pins.add(pin);
        repaint();
    }
    
    public void clearBoard() {
        notes.clear();
        pins.clear();
        repaint();
    }
    
    public void zoomIn() {
        scale *= 1.1f;
        scale = Math.min(scale, 10.0f);
        repaint();
    }
    
    public void zoomOut() {
        scale /= 1.1f;
        scale = Math.max(scale, 0.5f);
        repaint();
    }
    
    public void resetView() {
        boardOffset.setLocation(20, 20);
        scale = 3.0f;
        repaint();
    }
    
    public Point getBoardOffset() {
        return new Point(boardOffset);
    }
    
    public void setBoardOffset(int x, int y) {
        boardOffset.setLocation(x, y);
        repaint();
    }
    
    public float getScale() {
        return scale;
    }
    
    public void setScale(float scale) {
        this.scale = Math.max(0.5f, Math.min(scale, 10.0f));
        repaint();
    }
    
    // Utility method to convert screen coordinates to board coordinates
    public Point screenToBoard(Point screenPoint) {
        int boardX = (int)((screenPoint.x - boardOffset.x) / scale);
        int boardY = (int)((screenPoint.y - boardOffset.y) / scale);
        return new Point(boardX, boardY);
    }
    
    // Utility method to convert board coordinates to screen coordinates
    public Point boardToScreen(Point boardPoint) {
        int screenX = boardOffset.x + (int)(boardPoint.x * scale);
        int screenY = boardOffset.y + (int)(boardPoint.y * scale);
        return new Point(screenX, screenY);
    }
}
