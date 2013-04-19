package org.robminfor.engine.agents;

import java.util.ArrayList;
import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.actions.AbstractAction;
import org.robminfor.engine.actions.Deliver;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.Home;
import org.robminfor.util.Vect;

public class Agent {
	private Site site;
	private final Home home;
	private final List<AbstractAction> actions = new ArrayList<AbstractAction>();
	private AbstractEntity inventory = null;

	public Agent(Site site, Home home) {
		setSite(site);
		this.home = home;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public Site getSite() {
		return site;
	}

	public Vect getPosition() {
		return getSite().getPosition();
	}

	public void removeAction(AbstractAction action) {
		actions.remove(action);
	}

	public void addAction(AbstractAction action) {
		actions.add(0, action);
	}

	public void removeActionsOfType(Class<? extends AbstractAction> t) {
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

	public AbstractEntity getInventory() {
		return inventory;
	}

	public void setInventory(AbstractEntity inventory) {
		this.inventory = inventory;
	}

	public void update() {
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

}
