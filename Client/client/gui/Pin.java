package client.gui;

import java.util.Objects;

public class Pin {
	private int x, y;
	
	public Pin(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	
	public String toString() {
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