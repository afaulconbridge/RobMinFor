package org.robminfor.engine.entities;

public abstract class AbstractFacility extends AbstractEntity {
	
	public AbstractFacility() {
	}
	
	@Override
	public boolean isSolid() {
		return true;
	}

}
