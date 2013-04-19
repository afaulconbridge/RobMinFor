package org.robminfor.engine.entities;

public class AbstractFacility extends AbstractEntity {

	protected final Home home;
	
	public AbstractFacility(Home home) {
		this.home = home;
		home.addFacility(this);
	}
	
	@Override
	public boolean isSolid() {
		return true;
	}

}
