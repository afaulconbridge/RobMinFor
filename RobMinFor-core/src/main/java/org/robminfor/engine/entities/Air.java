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

	@Override
	public String getName() {
		return "Air";
	}

	@Override
	public int getBuyValue() {
		return -1;
	}

	@Override
	public int getSellValue() {
		return -1;
	}

}
