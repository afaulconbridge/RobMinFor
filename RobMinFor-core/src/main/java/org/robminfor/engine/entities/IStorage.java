package org.robminfor.engine.entities;

import java.util.Collection;

public interface IStorage {
	
	public void addEntity(AbstractEntity entity);
	
	public void removeEntity(AbstractEntity entity);
	
	public Collection<AbstractEntity> getContent();

	public boolean containsEntity(AbstractEntity thing);
}
