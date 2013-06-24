package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deliver extends AbstractAction {

	private Logger log = LoggerFactory.getLogger(getClass());

	private Site target;

	public Deliver(Site target) {
		this.target = null;
	}

	private Site getTarget() {
		if (target == null) {
			if (getAgent() != null) {
				target = getAgent()
						.getSite()
						.getLandscape()
						.getNearestStorageFor(getAgent().peekInventory(),
								getAgent().getSite());
			}
		}
		return target;
	}

	@Override
	public Site getSite() {
		return getTarget();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean isComplete() {
		if (getAgent().peekInventory() == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isCompletable() {
		if (getAgent() == null) {
			return false;
		} else if (getAgent().getSite().getLandscape()
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
			// can't be done, work out why
			if (getAgent().getSite().getLandscape()
					.findPath(getAgent().getSite(), getTarget()) == null) {
				// cant path to target, just drop it here
			} else {
				// should never be here
				log.warn("cannot complete and can't determine why");
			}
		} else {
			if (!getAgent().getSite().isAccessible(getTarget())) {
				// not in position to access source
				getAgent().addAction(new NavigateToAccess(getTarget()));
			} else {
				getTarget().addItem(getAgent().popInventory());
			}
		}

		// TODO Auto-generated method stub
	}

	@Override
	public int getEffort(Agent agent) {
		if (getAgent() == null) {
			return Integer.MAX_VALUE;
		} else if (getTarget() == null) {
			return Integer.MAX_VALUE;
		} else {
			return agent.getSite().getLandscape()
					.findPath(agent.getSite(), getTarget()).size();
		}
	}

}
