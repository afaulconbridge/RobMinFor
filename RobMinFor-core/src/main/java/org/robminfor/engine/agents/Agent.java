package org.robminfor.engine.agents;

import java.util.ArrayList;
import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.actions.AbstractAction;
import org.robminfor.engine.actions.Deliver;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.util.Vect;

public class Agent {
	private Site site;
	private Site previousSite;
	private final List<AbstractAction> actions = new ArrayList<AbstractAction>();
	private AbstractEntity inventory = null;

	public Agent(Site site) {
		this.previousSite = site;
		this.site = site;
	}

	public synchronized void setSite(Site site) {
		if (site == null) {
			throw new IllegalArgumentException("Agent cannot be at a null site");
		}
		this.previousSite = this.site;
		this.site = site;
	}

	public synchronized Site getSite() {
		return site;
	}

	public synchronized Site getPreviousSite() {
		return previousSite;
	}

	public synchronized Vect getPosition() {
		return getSite().getPosition();
	}

	public synchronized void removeAction(AbstractAction action) {
		actions.remove(action);
	}

	public synchronized void addAction(AbstractAction action) {
		actions.add(0, action);
	}

	public synchronized void removeActionsOfType(Class<? extends AbstractAction> t) {
		List<AbstractAction> toremove = new ArrayList<AbstractAction>();
		for (AbstractAction a : actions) {
			if (t.isInstance(a)) {
				toremove.add(a);
			}
		}
		for (AbstractAction a : toremove) {
			removeAction(a);
		}
	}

	public synchronized AbstractEntity getInventory() {
		return inventory;
	}

	public synchronized void setInventory(AbstractEntity inventory) {
		this.inventory = inventory;
	}

	public synchronized void update() {
		this.previousSite = this.site;
		//if we are standing over something non-solid, fall
		if (!site.isWalkable()) {
			Site target = site.getLandscape().getSite(site.getX(), site.getY(), site.getZ()+1);
			setSite(target);
		} else if (actions.size() == 0) {
			//if we are carying something, deliver it somewhere
			if (getInventory() != null) {
	        	Site storage = site.getLandscape().findNearestStorageFor(getSite(), getInventory());
				addAction(new Deliver(storage));
			} else {
				AbstractAction next = site.getLandscape().getActionForAgent(this);
				if (next != null) {
					addAction(next);
				} else {
					// not doing anything then wiggle
	//				int dx = (int) ((Math.random() * 10.0) - 5.0);
	//				int dy = (int) ((Math.random() * 10.0) - 5.0);
	//				Site target = site.getLandscape().getSite(site.getX() + dx,
	//						site.getY() + dy, site.getZ());
	//				// move down to something that can be stood on
	//				while (!target.isSolid() && !target.isWalkable()) {
	//					target = site.getLandscape().getSite(target.getX(),
	//							target.getY(), target.getZ() + 1);
	//				}
	//				if (target.isWalkable()) {
	//					addAction(new MoveTo(target));
	//				}
				}
			}
		} else {
			actions.get(0).doAction(this);
		}
	}
	
	/**
	 * The return value from this is typically used by the renderer to associate an image 
	 * or other graphic with a worker.
	 * 
	 * return @String identifying what worker this is
	 * @return
	 */
	public synchronized String getName() {
		return "Worker";
	}

}
