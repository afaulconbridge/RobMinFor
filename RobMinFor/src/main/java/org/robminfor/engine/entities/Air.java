package org.robminfor.engine.entities;

public class Air extends AbstractEntity {
	
	private static final Air singleton = new Air();
	
	protected Air() {
		super();
	}
	
	public static Air getInstance(){
		return singleton;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

}
