package client.gui;

import java.awt.Color;

public class Note {
	private int x,y;
	private String colorName;
	private Color color;
	private String message;
	private boolean pinned;
	private final int width, height;
	
	private Color parseColor(String colorName) {
		switch(colorName.toLowerCase()) {
		case "red": return Color.RED;
		case "green": return Color.GREEN;
		case "blue": return Color.BLUE;
		case "yellow": return Color.YELLOW;
		case "orange": return Color.ORANGE;
		case "pink": return Color.PINK;
		case "cyan": return Color.CYAN;
		case "magenta": return Color.MAGENTA;
		case "white": return Color.WHITE;
		case "gray": return Color.GRAY;
		default : return Color.LIGHT_GRAY;
		}
	}
	
	public Note(int x, int y, String colorName, String message, boolean pinned, int width, int height) {
		this.x = x;
		this.y = y;
		this.colorName = colorName;
		this.message = message;
		this.pinned = pinned;
		this.width = width;
		this.height = height;
		this.color = parseColor(colorName);
	}
	
	public boolean containsPoint(int px, int py) {
		return px >= x && px <= x + width && py >= y && py <= y + height;
	}
	
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
    public String getColorName() { return colorName; }
    public String getMessage() { return message; }
    public boolean isPinned() { return pinned; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public void setPinned(boolean pinned) { this.pinned = pinned; }
	
	
}
