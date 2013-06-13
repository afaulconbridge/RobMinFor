package org.robminfor.engine.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class EntityManager {

	protected static EntityManager instance = null;
	protected final Document document;
	
	private Map<String, AbstractEntity> entityCache = new HashMap<String, AbstractEntity>();
	
	private List<String> entityNames = new ArrayList<String>();
	
	protected EntityManager() {
		SAXReader reader = new SAXReader();
        try {
			document = reader.read(EntityManager.class.getResource("/data/entities.xml"));
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
        
        Element root = document.getRootElement();
        for (Iterator i = root.elementIterator(); i.hasNext(); ) {
            Element element = (Element) i.next();
            String name = element.attributeValue("name");
            if (entityNames.contains(name)) {
            	throw new RuntimeException("Duplicate entity name "+name);
            } else {
            	entityNames.add(name);
            }
        }
	}
	
	public static EntityManager getEntityManager() {
		if (instance == null) {
			instance = new EntityManager();
		}
		return instance;
	}
	
	public AbstractEntity getEntity(String name) {
		if (!entityCache.containsKey(name)) {
			//create it from a string
			boolean isSolid = true;
			int buyValue = -1;
			int sellValue = -1;
			
	        Element root = document.getRootElement();
	        for (Iterator i = root.elementIterator(); i.hasNext(); ) {
	            Element element = (Element) i.next();
	            if (name.equals(element.attributeValue("name"))) {
	            	for (Iterator j = element.attributeIterator(); j.hasNext();) {
	    	            Attribute attribute = (Attribute) j.next();
	            		if ("solid".equals(attribute.getName())) {
	    	            	if ("false".equals(attribute.getStringValue())) {
	    	            		isSolid = false;
	    	            	}
	            		} else if ("buyValue".equals(attribute.getName())) {
	            			buyValue = Integer.parseInt(attribute.getStringValue());
	            		} else if ("sellValue".equals(attribute.getName())) {
	    	            	sellValue = Integer.parseInt(attribute.getStringValue());
	            		}
	            	}
	            }
	        }
			AbstractEntity entitiy = new EntityInstance(name, isSolid, buyValue, sellValue);
			
			entityCache.put(name, entitiy);
		}
		
		return entityCache.get(name);
	}
	
	public List<String> getEntityNames() {
		return Collections.unmodifiableList(entityNames);
	}

	private class EntityInstance extends AbstractEntity {

		private final String name;
		private final boolean isSolid;
		private final int buyValue;
		private final int sellValue;
		
		public EntityInstance(String name, boolean isSolid, int buyValue, int sellValue) {
			this.name = name;
			this.isSolid = isSolid;
			this.buyValue = buyValue;
			this.sellValue = sellValue;
		}
		
		@Override
		public boolean isSolid() {
			return isSolid;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public int getBuyValue() {
			return this.buyValue;
		}

		@Override
		public int getSellValue() {
			return this.sellValue;
		}
		
	}
}
