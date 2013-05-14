package org.robminfor.engine.entities;

public class Bricks extends AbstractEntity {
	
	private static final Bricks singleton = new Bricks();
	
	protected Bricks() {
		super();
	}
	
	public static Bricks getInstance(){
		return singleton;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public String getName() {
		return "Bricks";
	}

}
