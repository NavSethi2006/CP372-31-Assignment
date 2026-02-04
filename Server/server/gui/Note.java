package server.gui;

import java.util.Objects;

public class Note {

	private int x, y;
	private String color;
	private String content;
	private int width, height;
	
	public Note(int x, int y, String color, String content, int notewidth, int noteheight) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.content = content;
		this.width = notewidth;
		this.height = noteheight;

	}
	
    public boolean containsPoint(int px, int py) {
        return px >= x && px < x + width && 
               py >= y && py < y + height;
    }
    
    public boolean completeOverlap(Note other) {
    	return this.x == other.x && this.y == other.y &&
    			this.width == other.width && this.height == other.height;
    }
    public boolean isWithinBounds(int bw, int bh) {
    	return x >= 0 && y >= 0 && (width+x) <= bw && (height+y) <= bh;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public String getColor() { return color; }
    public String getMessage() { return content; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    
    public String toProtocolString(boolean isPinned) {
    	return String.format("NOTE %d %d %s %s %s", x, y, color, content, isPinned ? "true" : "false");
    }
    
    @Override
    public int hashCode() {
    	return Objects.hash(x,y,width,height);
    }
    
    
    
	
}
