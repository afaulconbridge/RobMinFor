package org.robminfor.engine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.robminfor.engine.actions.AbstractAction;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.AbstractFacility;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.IStorage;
import org.robminfor.util.Vect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Landscape {

	private List<List<List<Site>>> sites;
	private List<Agent> agents  = Collections.synchronizedList(new ArrayList<Agent>());
	private List<AbstractAction> actions  = Collections.synchronizedList(new LinkedList<AbstractAction>());

	private Site homeSite = null;
	private Collection<AbstractFacility> facilities = Collections.synchronizedList(new LinkedList<AbstractFacility>());
	private Integer money = 1000;
	
	private Calendar calendar = new GregorianCalendar(3141, 5, 9, 2, 6);
	private final Pathfinder pathfinder = new Pathfinder();

    private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Creates a new Landscape object of the specified size, but completely blank.
	 * 
	 * To generate an interesting Landscape, use a LandscapeFactory.
	 */
	public Landscape(int sizex, int sizey, int sizez){
		super();
		AbstractEntity stone = EntityManager.getEntityManager().getEntity("Stone");
		sites = new ArrayList<List<List<Site>>>(sizez);
		for (int z = 0; z < sizez; z++){
			sites.add(new ArrayList<List<Site>>(sizey));
			for (int y = 0; y < sizey; y++){
				sites.get(z).add(new ArrayList<Site>(sizex));
				for (int x = 0; x < sizex; x++){
					Vect position = new Vect(x,y,z);
					AbstractEntity entity = stone;
					Site site = new Site(entity, position, this);
					sites.get(z).get(y).add(site);
				}
			}
		}
	}

	public synchronized void update() {
		calendar.add(Calendar.MINUTE, 1);
		
		for (Agent agent : agents){
			agent.update();
		}
	}
	
	public synchronized Agent getAgent(int i){
		return agents.get(i);		
	}
	
	public synchronized int getAgentCount(){
		return agents.size();		
	}
	
	public synchronized List<Agent> getAgents(){
		return Collections.unmodifiableList(agents);		
	}
	
	public synchronized void addAgent(Agent agent){
		agents.add(agent);
	}
	
	public synchronized void addAction(AbstractAction action) {
		if(!actions.contains(action) && action.isValid()){
			actions.add(action);
		}
	}
	
	public synchronized AbstractAction getActionForAgent(Agent agent) {
		for (int i = 0; i < actions.size(); i++) {
			AbstractAction action = actions.get(i);
			if (action.isValid(agent)) {
				return actions.remove(i);
			}
		}
		//no suitable actions available
		return null;
	}
	
	public synchronized Site getSite(Vect position){
		return getSite(position.getX(), position.getY(), position.getZ());
	}
	
	public synchronized Site getSite(int x, int y, int z) {
		if (x < 0 || x >= getSizeX()){
			return null;
		} else if (y < 0 || y >= getSizeY()){
			return null;
		}else  if (z < 0 || z >= getSizeZ()){
			return null;
		}
		//log.info("getting "+x+","+y+","+z);
		return sites.get(z).get(y).get(x);
	}

	public synchronized int getSizeX() {
		return sites.get(0).get(0).size();
	}

	public synchronized int getSizeY() {
		return sites.get(0).size();
	}

	public synchronized int getSizeZ() {
		return sites.size();
	}
	
	public synchronized boolean isSolid(Vect position){
		return isSolid(position.getX(), position.getY(), position.getZ());
	}

	public synchronized boolean isSolid(int x, int y, int z) {
		Site site = sites.get(z).get(y).get(x);
		if (site == null){
			return true;
		} else {
			return site.isSolid();
		}
	}
	
	public synchronized Calendar getCalendar() {
		return calendar;
	}

	public synchronized List<Site> findPath(Site start, Site end) {
		return pathfinder.findPath(start, end);
	}
	
	public synchronized void setHomeSite(Site site) {
		this.homeSite = site;
	}
	
	public synchronized Site getHomeSite() {
		return homeSite;
	}
	
	public synchronized int getMoney() {
		synchronized(this.money) {
			return money;
		}
	}

	public synchronized boolean changeMoney(int difference) {
		synchronized(this.money) {
			if (this.money+difference < 0) {
				return false;
			}
			this.money += difference;
			return true;
		}
	}
	
	public synchronized List<AbstractAction> getActions() {
		return Collections.unmodifiableList(actions);
	}
	
	public synchronized void addFacility(AbstractFacility facility) {
		if (!facilities.contains(facility)) {
			facilities.add(facility);
		} else {
			throw new IllegalArgumentException("Duplicate facility "+facility);
		}
	}
	
	public synchronized void removeFacility(AbstractFacility facility) {
		if (facilities.contains(facility)) {
			facilities.add(facility);
		} else {
			throw new IllegalArgumentException("Non contained facility "+facility);
		}
	}
	
	public synchronized Site getNearestStorageFor(AbstractEntity entity, Site target) {
		return getNearestStorageFor(entity.getName(), target);
	}
	
	public synchronized Site getNearestStorageFor(String entityName, Site target) {
		//TODO implement this in a generic fashion over all storage sites
		return getHomeSite();
	}
	
	public synchronized Site getNearestStorageOf(AbstractEntity entity, Site target) {
		return getNearestStorageOf(entity.getName(), target);
		
	}
	
	public synchronized Site getNearestStorageOf(String entityName, Site target) {
		//TODO implement this in a generic fashion over all storage sites
		IStorage store = (IStorage) getHomeSite().getEntity();
		if (store.containsEntity(entityName)) {
			return getHomeSite();
		}
		//if we can't find anything
		return null;
	}
	
	public synchronized Site getStorageFor(AbstractEntity entity) {
		return getStorageFor(entity.getName());
	}
	
	public synchronized Site getStorageFor(String entityName) {
		//TODO implement this in a generic fashion over all storage sites
		return getHomeSite();
	}
	
	public synchronized Site getStorageOf(AbstractEntity entity) {
		return getStorageOf(entity.getName());
		
	}
	
	public synchronized Site getStorageOf(String entityName) {
		//TODO implement this in a generic fashion over all storage sites
		IStorage store = (IStorage) getHomeSite().getEntity();
		if (store.containsEntity(entityName)) {
			return getHomeSite();
		}
		return null;
	}
}
