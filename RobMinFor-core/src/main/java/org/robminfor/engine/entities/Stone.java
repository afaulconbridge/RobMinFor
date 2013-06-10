package org.robminfor.engine.entities;

public class Stone extends AbstractEntity {
	
	private static final Stone singleton = new Stone();
	
	protected Stone() {
		super();
	}
	
	public static Stone getInstance(){
		return singleton;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public String getName() {
		return "Stone";
	}

	@Override
	public int getBuyValue() {
		return 20;
	}

	@Override
	public int getSellValue() {
		return 5;
	}

}
