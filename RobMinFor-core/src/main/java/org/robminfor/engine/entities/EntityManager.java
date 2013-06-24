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

	private Map<String, IBlock> blockCache = new HashMap<String, IBlock>();
	private Map<String, IItem> itemCache = new HashMap<String, IItem>();

	private List<String> blockNames = new ArrayList<String>();
	private List<String> itemNames = new ArrayList<String>();

	protected EntityManager() {
		SAXReader reader = new SAXReader();
		try {
			document = reader.read(EntityManager.class
					.getResource("/data/entities.xml"));
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}

		Element root = document.getRootElement();

		for (Iterator i = root.elementIterator("block"); i.hasNext();) {
			Element element = (Element) i.next();

			String name = element.attributeValue("name");
			if (blockNames.contains(name)) {
				throw new RuntimeException("Duplicate entity name " + name);
			} else {
				blockNames.add(name);
			}
		}

		for (Iterator i = root.elementIterator("item"); i.hasNext();) {
			Element element = (Element) i.next();

			String name = element.attributeValue("name");
			if (itemNames.contains(name)) {
				throw new RuntimeException("Duplicate entity name " + name);
			} else {
				itemNames.add(name);
			}
		}
	}

	public static EntityManager getEntityManager() {
		if (instance == null) {
			instance = new EntityManager();
		}
		return instance;
	}

	public List<String> getBlockNames() {
		return Collections.unmodifiableList(blockNames);
	}

	public List<String> getItemNames() {
		return Collections.unmodifiableList(itemNames);
	}

	public IBlock getBlock(String name) {
		if (!blockCache.containsKey(name)) {
			// create it from a string
			boolean isSolid = true;
			boolean isStorage = false;
			int buyValue = -1;
			int sellValue = -1;

			Element root = document.getRootElement();
			for (Iterator i = root.elementIterator(); i.hasNext();) {
				Element element = (Element) i.next();
				if (name.equals(element.attributeValue("name"))) {
					for (Iterator j = element.attributeIterator(); j.hasNext();) {
						Attribute attribute = (Attribute) j.next();
						if ("solid".equals(attribute.getName())) {
							if ("false".equals(attribute.getStringValue())) {
								isSolid = false;
							}
						} else if ("storage".equals(attribute.getName())) {
							if ("true".equals(attribute.getStringValue())) {
								isSolid = true;
							}
						}
					}
				}
			}
			IBlock entitiy = new BlockInstance(name, isSolid, isStorage);

			blockCache.put(name, entitiy);
		}

		IBlock toReturn = blockCache.get(name);
		if (toReturn == null)
			throw new RuntimeException("Cannot return null");
		return toReturn;
	}

	private class BlockInstance implements IBlock {

		private final String name;
		private final boolean isSolid;
		private final boolean isStorage;

		public BlockInstance(String name, boolean isSolid, boolean isStorage) {
			this.name = name;
			this.isSolid = isSolid;
			this.isStorage = isStorage;
		}

		@Override
		public boolean isSolid() {
			return isSolid;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isStorage() {
			return isStorage;
		}
	}

	public IItem getItem(String name) {
		if (!itemCache.containsKey(name)) {
			// create it from a string
			int buyValue = -1;
			int sellValue = -1;

			Element root = document.getRootElement();
			for (Iterator i = root.elementIterator(); i.hasNext();) {
				Element element = (Element) i.next();
				if (name.equals(element.attributeValue("name"))) {
					for (Iterator j = element.attributeIterator(); j.hasNext();) {
						Attribute attribute = (Attribute) j.next();
						if ("buyValue".equals(attribute.getName())) {
							buyValue = Integer.parseInt(attribute
									.getStringValue());
						} else if ("sellValue".equals(attribute.getName())) {
							sellValue = Integer.parseInt(attribute
									.getStringValue());
						}
					}
				}
			}
			IItem entitiy = new ItemInstance(name, buyValue, sellValue);

			itemCache.put(name, entitiy);
		}
		IItem toReturn = itemCache.get(name);
		if (toReturn == null)
			throw new RuntimeException("Cannot return null");
		return toReturn;
	}

	private class ItemInstance implements IItem {

		private final String name;
		private final int buyValue;
		private final int sellValue;

		public ItemInstance(String name, int buyValue, int sellValue) {
			this.name = name;
			this.buyValue = buyValue;
			this.sellValue = sellValue;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public int getBuyValue() {
			return buyValue;
		}

		@Override
		public int getSellValue() {
			return sellValue;
		}
	}
}
