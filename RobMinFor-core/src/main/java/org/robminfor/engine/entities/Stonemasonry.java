package org.robminfor.engine.entities;

public class Stonemasonry extends AbstractFacility {

	public Stonemasonry() {
		super();
	}

	@Override
	public String getName() {
		return "Stonemasonry";
	}

	@Override
	public int getBuyValue() {
		return 1000;
	}

	@Override
	public int getSellValue() {
		return -1;
	}

}
