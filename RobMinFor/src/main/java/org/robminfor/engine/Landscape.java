package org.robminfor.engine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.robminfor.engine.actions.AbstractAction;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.Stone;
import org.robminfor.util.Vect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Landscape {

	private List<List<List<Site>>> sites;
	private List<Agent> agents  = new ArrayList<Agent>();
	private Queue<AbstractAction> actions  = new LinkedList<AbstractAction>();

	
	private Calendar calendar = new GregorianCalendar(3141, 5, 9, 2, 6);
	private final Pathfinder pathfinder = new Pathfinder();

    private Logger log = LoggerFactory.getLogger(getClass());

	/*
	 * Creates a new Landscape object of the specified size, but completely blank.
	 * 
	 * To generate an interesting Landscape, use a LandscapeFactory.
	 */
	public Landscape(int sizex, int sizey, int sizez){
		super();
		sites = new ArrayList<List<List<Site>>>(sizez);
		for (int z = 0; z < sizez; z++){
			sites.add(new ArrayList<List<Site>>(sizey));
			for (int y = 0; y < sizey; y++){
				sites.get(z).add(new ArrayList<Site>(sizex));
				for (int x = 0; x < sizex; x++){
					Vect position = new Vect(x,y,z);
					AbstractEntity entity = Stone.getInstance();
					Site site = new Site(entity, position, this);
					sites.get(z).get(y).add(site);
				}
			}
		}
	}

	public void update() {
		calendar.add(Calendar.MINUTE, 1);
		
		for (Agent agent : agents){
			agent.update();
		}
	}
	
	public Agent getAgents(int i){
		return agents.get(i);		
	}
	
	public int getAgentCount(){
		return agents.size();		
	}
	
	public void addAgent(Agent agent){
		agents.add(agent);
	}
	
	public void addAction(AbstractAction action) {
		if(!actions.contains(action) && action.isValid()){
			actions.add(action);
		}
	}
	
	public AbstractAction pollAction(){
		return actions.poll();
	}
	
	public Site getSite(Vect position){
		return getSite(position.getX(), position.getY(), position.getZ());
	}
	
	public Site getSite(int x, int y, int z) {
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

	public int getSizeX() {
		return sites.get(0).get(0).size();
	}

	public int getSizeY() {
		return sites.get(0).size();
	}

	public int getSizeZ() {
		return sites.size();
	}
	
	public boolean isSolid(Vect position){
		return isSolid(position.getX(), position.getY(), position.getZ());
	}

	public boolean isSolid(int x, int y, int z) {
		Site site = sites.get(z).get(y).get(x);
		if (site == null){
			return true;
		} else {
			return site.isSolid();
		}
	}
	
	public Calendar getCalendar() {
		return calendar;
	}

	public List<Site> findPath(Site start, Site end) {
		return pathfinder.findPath(start, end);
	}
	
}
