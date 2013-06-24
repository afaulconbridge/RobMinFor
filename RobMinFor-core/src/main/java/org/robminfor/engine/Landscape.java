package org.robminfor.engine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.robminfor.engine.actions.AbstractAction;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.IBlock;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.IItem;
import org.robminfor.util.Vect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Landscape {

	private List<List<List<Site>>> sites;
	private List<Agent> agents = Collections
			.synchronizedList(new ArrayList<Agent>());
	private List<AbstractAction> actions = Collections
			.synchronizedList(new LinkedList<AbstractAction>());
	private List<Site> stores = Collections
			.synchronizedList(new ArrayList<Site>());

	private Site homeSite = null;
	private Integer money = 1000;

	private Calendar calendar = new GregorianCalendar(3141, 5, 9, 2, 6);
	private final Pathfinder pathfinder = new Pathfinder();

	private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Creates a new Landscape object of the specified size, but completely
	 * blank.
	 * 
	 * To generate an interesting Landscape, use a LandscapeFactory.
	 */
	public Landscape(int sizex, int sizey, int sizez) {
		super();
		IBlock stone = EntityManager.getEntityManager().getBlock("Stone");
		sites = new ArrayList<List<List<Site>>>(sizez);
		for (int z = 0; z < sizez; z++) {
			sites.add(new ArrayList<List<Site>>(sizey));
			for (int y = 0; y < sizey; y++) {
				sites.get(z).add(new ArrayList<Site>(sizex));
				for (int x = 0; x < sizex; x++) {
					Vect position = new Vect(x, y, z);
					Site site = new Site(stone, position, this);
					sites.get(z).get(y).add(site);
				}
			}
		}
	}

	public synchronized void update() {
		calendar.add(Calendar.MINUTE, 1);

		for (Agent agent : agents) {
			agent.update();
		}
	}

	public synchronized Agent getAgent(int i) {
		return agents.get(i);
	}

	public synchronized int getAgentCount() {
		return agents.size();
	}

	public synchronized List<Agent> getAgents() {
		return Collections.unmodifiableList(agents);
	}

	public synchronized void addAgent(Agent agent) {
		agents.add(agent);
	}

	public synchronized void addAction(AbstractAction action) {
		if (!actions.contains(action) && action.isValid()) {
			actions.add(action);
		}
	}

	public synchronized AbstractAction getActionForAgent(Agent agent) {
		Integer minEffort = null;
		AbstractAction bestAction = null;
		for (int i = 0; i < actions.size(); i++) {
			AbstractAction action = actions.get(i);
			Integer effort = action.getEffort(agent);
			if (minEffort == null || effort < minEffort) {
				bestAction = action;
				minEffort = effort;
			}
		}
		return bestAction;
	}

	public synchronized Site getSite(Vect position) {
		return getSite(position.getX(), position.getY(), position.getZ());
	}

	public synchronized Site getSite(int x, int y, int z) {
		if (x < 0 || x >= getSizeX()) {
			return null;
		} else if (y < 0 || y >= getSizeY()) {
			return null;
		} else if (z < 0 || z >= getSizeZ()) {
			return null;
		}
		// log.info("getting "+x+","+y+","+z);
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

	public synchronized boolean isSolid(Vect position) {
		return isSolid(position.getX(), position.getY(), position.getZ());
	}

	public synchronized boolean isSolid(int x, int y, int z) {
		Site site = sites.get(z).get(y).get(x);
		if (site == null) {
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

	public synchronized int getMoney() {
		synchronized (this.money) {
			return money;
		}
	}

	public synchronized boolean changeMoney(int difference) {
		synchronized (this.money) {
			if (this.money + difference < 0) {
				return false;
			}
			this.money += difference;
			return true;
		}
	}

	public synchronized List<AbstractAction> getActions() {
		return Collections.unmodifiableList(actions);
	}

	public synchronized Site getNearestStorageFor(IItem item, Site target) {
		Site nearest = null;
		int nearestDist = -1;
		for (Site s : stores) {
			// TODO check if this can store this item
			int thisDist = findPath(target, s).size();
			if (nearest == null || thisDist < nearestDist) {
				nearest = s;
				nearestDist = thisDist;
			}
		}
		return nearest;
	}

	public synchronized Site getNearestStorageOf(IItem item, Site target) {
		Site nearest = null;
		int nearestDist = -1;
		for (Site s : stores) {
			if (s.getItems().contains(item)) {
				int thisDist = findPath(target, s).size();
				if (nearest == null || thisDist < nearestDist) {
					nearest = s;
					nearestDist = thisDist;
				}
			}
		}
		return nearest;
	}

	public synchronized void addStorageSite(Site site) {
		if (stores.contains(site))
			throw new IllegalArgumentException("site cannot already be a store");
		stores.add(site);
	}

	public synchronized void removeStorageSite(Site site) {
		if (!stores.contains(site))
			throw new IllegalArgumentException("site must already be a store");
		stores.remove(site);
	}

	public synchronized List<Site> getStorageSites() {
		return Collections.unmodifiableList(stores);
	}
}
