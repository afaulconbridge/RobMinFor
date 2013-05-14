package org.robminfor.engine.entities;

public class Crystal extends AbstractEntity {
	
	private static final Crystal singleton = new Crystal();
	
	protected Crystal() {
		super();
	}
	
	public static Crystal getInstance(){
		return singleton;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public String getName() {
		return "Crystal";
	}

}
