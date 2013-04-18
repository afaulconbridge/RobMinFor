package org.robminfor.engine.entities;

public class Crystal extends AbstractEntity {
	
	private static final Crystal singleton = new Crystal();
	
	protected Crystal() {
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
