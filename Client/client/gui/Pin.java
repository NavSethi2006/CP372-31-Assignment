package client.gui;

public class Pin {
	private int x,y;
	
	public Pin(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	
	public String toString() {
		return String.format("PIN %d %d", x,y);
	}

}
