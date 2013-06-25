package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.IItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Buy extends AbstractAction {

	private Logger log = LoggerFactory.getLogger(getClass());

	private Site source = null;
	private Site target = null;
	private final IItem item;
	private boolean hasBought = false;

	public Buy(IItem item) {
		this.item = item;
	}

	@Override
	public boolean isValid() {
		if (hasBought && getAgent() != null
				&& getAgent().peekInventory() != item) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isComplete() {
		if (hasBought) {
			return true;
		} else {
			return false;
		}
	}

	private Site getSource() {
		if (source == null) {
			// TODO do this based on trading locations
			if (getAgent() != null) {
				source = getAgent().getSite().getLandscape()
						.getNearestStorageFor(item, getAgent().getSite());
			}
		}
		return source;
	}

	private Site getTarget() {
		if (target == null) {
			if (getAgent() != null) {
				target = getAgent().getSite().getLandscape()
						.getNearestStorageFor(item, getAgent().getSite());
			}
		}
		return target;
	}

	@Override
	public boolean isCompletable() {
		if (getAgent() == null) {
			return false;
		} else if (!hasBought && getAgent().peekInventory() != null) {
			return false;
		} else if (!hasBought
				&& getAgent().getSite().getLandscape().getMoney() < item
						.getBuyValue()) {
			return false;
		} else if (!hasBought
				&& getAgent().getSite().getLandscape()
						.findPath(getAgent().getSite(), getSource()) == null) {
			return false;
		} else if (hasBought
				&& getAgent().getSite().getLandscape()
						.findPath(getAgent().getSite(), getTarget()) == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void doAction() {
		if (!isValid()) {
			throw new IllegalArgumentException(
					"Agent must be assigned before doing action");
		} else if (getAgent() == null) {
			throw new IllegalArgumentException("Invalid action");
		} else if (isComplete()) {
			getAgent().removeAction(this);
		} else if (!isCompletable()) {
			// sensible action, but not achievable right now
			// work out what's wrong
			if (!hasBought && getAgent().peekInventory() != null) {
				// carrying something already
				IItem thing = getAgent().peekInventory();
				Site tempTarget = getAgent().getSite().getLandscape()
						.getNearestStorageFor(thing, getAgent().getSite());
				getAgent().addAction(new Deliver(tempTarget));
			} else if (!hasBought
					&& getAgent().getSite().getLandscape().getMoney() < item
							.getBuyValue()) {
				// cant afford it, put it back on global list
				source = null;
				target = null;
				getAgent().getSite().getLandscape().addAction(getAgent().flushActions());
				setAgent(null);
			} else if (!hasBought
					&& getAgent().getSite().getLandscape()
							.findPath(getAgent().getSite(), getSource()) == null) {
				// cant path to source, put it back on global list
				source = null;
				target = null;
				getAgent().getSite().getLandscape().addAction(getAgent().flushActions());
				setAgent(null);
			} else if (hasBought
					&& getAgent().getSite().getLandscape()
							.findPath(getAgent().getSite(), getTarget()) == null) {
				// already bought, so just stop
				getAgent().removeAction(this);
			} else {
				// should never be here
				log.warn("cannot complete and can't determine why");
			}
		} else {
			// haven't bought it yet
			if (!hasBought && !getAgent().getSite().isAccessible(getSource())) {
				// not in position to access source
				getAgent().addAction(new NavigateToAccess(getSource()));
			} else if (!hasBought
					&& getAgent().getSite().getLandscape()
							.changeMoney(item.getBuyValue())) {
				hasBought = true;
				getAgent().pushInventory(item);
			} else if (hasBought) {
				// complete the delivery stage
				getAgent().addAction(new Deliver(getTarget()));
			}
		}

	}

	@Override
	public Site getSite() {
		return getTarget();
	}

	@Override
	public int getEffort(Agent agent) {
		if (!hasBought && getSource() != null) {
			return agent.getSite().getLandscape()
					.findPath(agent.getSite(), getSource()).size();
		} else if (hasBought && getTarget() != null) {
			return agent.getSite().getLandscape()
					.findPath(agent.getSite(), getTarget()).size();
		} else {
			return Integer.MAX_VALUE;
		}
	}

}
