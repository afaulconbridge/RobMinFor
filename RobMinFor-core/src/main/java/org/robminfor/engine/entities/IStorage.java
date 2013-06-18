package org.robminfor.engine.entities;

public interface IStorage {
	
	public void addEntity(String entityName);
	
	public AbstractEntity removeEntity(String entityName);

	public int getCount(String entityName);
	
	public boolean containsEntity(String thing);

	public boolean canStore(String entityName);
}
