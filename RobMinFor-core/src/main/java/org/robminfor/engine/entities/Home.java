package org.robminfor.engine.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Home extends AbstractEntity implements IStorage  {
	
	private final Map<String, Integer> content = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	public Home() {
		super();
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public String getName() {
		return "Home";
	}

	@Override
	public synchronized void addEntity(String entityName) {
		if (!content.containsKey(entityName)) {
			content.put(entityName, 1);
		} else {
			content.put(entityName, content.get(entityName)+1);
		}
	}

	@Override
	public synchronized AbstractEntity removeEntity(String entityName) {
		if (!containsEntity(entityName)) {
			throw new IllegalArgumentException("Entitiy not in storage");
		}
		if (content.get(entityName) == 1) {
			content.remove(entityName);
		} else {
			content.put(entityName, content.get(entityName)-1);
		}
		return EntityManager.getEntityManager().getEntity(entityName);
	}

	@Override
	public synchronized int getCount(String entityName) {
		if (containsEntity(entityName)) {
			return content.get(entityName);
		} else {
			return 0;
		}
	}

	@Override
	public synchronized boolean containsEntity(String entityName) {
		return content.containsKey(entityName);
	}

	@Override
	public int getBuyValue() {
		return -1;
	}

	@Override
	public int getSellValue() {
		return -1;
	}

	@Override
	public boolean canStore(String entityName) {
		//for the moment, we can store everything
		return true;
	}
	
	

}
