package org.robminfor.util;

public class Vect {
	private final int x;
	private final int y;
	private final int z;

	public Vect(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!this.getClass().isInstance(other))
			return false;
		Vect o = (Vect) other;
		if (o.getX() != this.getX())
			return false;
		if (o.getY() != this.getY())
			return false;
		if (o.getZ() != this.getZ())
			return false;
		return true;
	}

}
