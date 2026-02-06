package server.gui;

import java.util.Objects;

public class Pin {
	
	private int x;
	private int y;

	public Pin(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	
	public boolean isInsideNote(Note note) {
		return note.containsPoint(x, y);
	}
	
	public String toProtocol() {
		return String.format("PIN %d %d", x, y);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pin pin = (Pin) o;
		return x == pin.x && y == pin.y;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}