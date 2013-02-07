package org.robminfor.engine.entities;

public class Home extends AbstractEntity {
	
	private static final Home singleton = new Home();
	
	protected Home() {
		super();
	}
	
	public static AbstractEntity getInstance(){
		return singleton;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

}
