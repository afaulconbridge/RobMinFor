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
	public synchronized void setPreviousSite() {
		previousSite = site;
	}

	public synchronized Site getPreviousSite() {
		return previousSite;
	}

	public synchronized void removeAction(AbstractAction action) {
		if (!actions.remove(action)) {
			throw new IllegalArgumentException("action not in actions");
		}
		action.setAgent(null);
	}

	public synchronized AbstractAction flushActions() {
		AbstractAction topAction = actions.peekLast();
		for (AbstractAction a : actions) {
			a.setAgent(null);
		}
		actions.clear();
		return topAction;
	}

	public synchronized void addAction(AbstractAction action) {
		action.setAgent(this);
		actions.add(0, action);
	}

	public synchronized AbstractAction getTopAction() {
		return actions.peekLast();
	}

	public synchronized AbstractAction getCurrentAction() {
		return actions.peek();
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
