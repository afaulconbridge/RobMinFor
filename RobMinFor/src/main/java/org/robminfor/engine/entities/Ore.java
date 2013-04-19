package org.robminfor.engine.entities;

public class Ore extends AbstractEntity {
	
	private static final Ore singleton = new Ore();
	
	protected Ore() {
		super();
	}
	
	public static Ore getInstance(){
		return singleton;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

}
