package org.robminfor.engine.agents;

import java.util.ArrayList;
import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.actions.AbstractAction;
import org.robminfor.engine.actions.MoveTo;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.util.Vect;

public class Agent {
	private Site site;
	private final List<AbstractAction> actions = new ArrayList<AbstractAction>();
	private AbstractEntity inventory = null;

	public Agent(Site site) {
		setSite(site);
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
		action.setAgent(null);
	}

	public void addAction(AbstractAction action) {
		action.setAgent(this);
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
		if (actions.size() == 0) {
			AbstractAction next = site.getLandscape().pollAction();
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
		} else {
			actions.get(0).doAction();
		}
	}

}
