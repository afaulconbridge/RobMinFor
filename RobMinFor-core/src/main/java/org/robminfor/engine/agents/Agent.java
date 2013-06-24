package org.robminfor.engine.agents;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.actions.AbstractAction;
import org.robminfor.engine.actions.Deliver;
import org.robminfor.engine.entities.IItem;
import org.robminfor.util.Vect;

public class Agent {
	private Site site;
	private Site previousSite;
	private final LinkedList<AbstractAction> actions = new LinkedList<AbstractAction>();
	private IItem inventory = null;

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
		if (!actions.remove(action))
			throw new IllegalArgumentException("action not in actions");
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

	public synchronized void removeActionsOfType(
			Class<? extends AbstractAction> t) {
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

	/**
	 * 
	 * 
	 * @return item being carried, or null if not carrying anything
	 */
	public synchronized IItem peekInventory() {
		return inventory;
	}

	public synchronized void pushInventory(IItem thing) {
		if (thing == null)
			throw new IllegalArgumentException("thing should not be null");
		this.inventory = thing;
	}

	public synchronized IItem popInventory() {
		IItem thing = this.inventory;
		this.inventory = null;
		return thing;
	}

	public synchronized void update() {
		this.previousSite = this.site;
		// if we are standing over something non-solid, fall
		if (!site.isWalkable()) {
			Site target = site.getLandscape().getSite(site.getX(), site.getY(),
					site.getZ() + 1);
			setSite(target);
		} else if (actions.size() == 0) {
			// if we are carrying something, and don't have another purpose for
			// it, deliver it somewhere
			if (peekInventory() != null) {
				AbstractAction next = new Deliver(site.getLandscape()
						.getNearestStorageFor(peekInventory(), site));
				if (next.getEffort(this) < Integer.MAX_VALUE) {
					addAction(next);
				}
			} else {
				AbstractAction next = site.getLandscape().getActionForAgent(
						this);
				if (next != null) {
					addAction(next);
				}
			}
		} else {
			actions.get(0).doAction();
		}
	}

	/**
	 * The return value from this is typically used by the renderer to associate
	 * an image or other graphic with a worker.
	 * 
	 * return @String identifying what worker this is
	 * 
	 * @return
	 */
	public synchronized String getName() {
		return "Worker";
	}

}
