package org.robminfor.engine.agents;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.actions.AbstractAction;
import org.robminfor.engine.actions.Deliver;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.util.Vect;

public class Agent {
	private Site site;
	private Site previousSite;
	private final LinkedList<AbstractAction> actions = new LinkedList<AbstractAction>();
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
		if (!actions.remove(action)) throw new IllegalArgumentException("action not in actions");
	}

	public synchronized AbstractAction flushActions() {
		AbstractAction topAction = actions.peekLast();
		actions.clear();
		return topAction;
	}
	
	public synchronized void addAction(AbstractAction action) {
		actions.add(0, action);
	}

	public synchronized AbstractAction getTopAction() {
		return actions.peekLast();
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

	public synchronized AbstractEntity peekInventory() {
		return inventory;
	}

	public synchronized void pushInventory(AbstractEntity thing) {
		if (thing == null) throw new IllegalArgumentException("thing should not be null");
		if (this.inventory != null) throw new IllegalArgumentException("agent inventory must be null");
		if (!thing.isSolid())  throw new IllegalArgumentException("thing must be solid");
		
		this.inventory = thing;
	}

	public synchronized AbstractEntity popInventory() {
		AbstractEntity thing = this.inventory;
		this.inventory = null;
		return thing;
	}

	public synchronized void update() {
		this.previousSite = this.site;
		//if we are standing over something non-solid, fall
		if (!site.isWalkable()) {
			Site target = site.getLandscape().getSite(site.getX(), site.getY(), site.getZ()+1);
			setSite(target);
		} else if (actions.size() == 0) {
			//if we are carrying something, and don't have another purpose for it, deliver it somewhere
			if (peekInventory() != null) {
				Site target = site.getLandscape().getNearestStorageFor(peekInventory(), site);
				this.addAction(new Deliver(peekInventory(), target));
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
