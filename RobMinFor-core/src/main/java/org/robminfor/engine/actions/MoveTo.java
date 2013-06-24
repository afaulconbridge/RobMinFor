package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This represents moving between transitable sites. For larger distances,
 * NavigateToAccess should be used.
 * 
 * @author faulcon
 * 
 */
public class MoveTo extends AbstractAction {

	private final Site site;

	private Logger log = LoggerFactory.getLogger(getClass());

	public MoveTo(Site site) {
		super();
		this.site = site;
	}

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public boolean isValid() {
		// can't move to solid objects
		if (site.isSolid()) {
			log.error("Trying to move into solid site");
			return false;
		} else if (!site.isWalkable()) {
			log.error("Trying to move into unwalkable site");
			return false;
		}
		return true;
	}

	@Override
	public boolean isComplete() {
		if (getAgent() == null) {
			return false;
		} else if (site.equals(getAgent().getSite())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isCompletable() {
		if (getAgent() == null) {
			return false;
		} else if (!getAgent().getSite().isTransitable(site)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void doAction() {
		if (!isValid())
			throw new IllegalArgumentException(
					"Agent must be assigned before doing action");
		if (getAgent() == null)
			throw new IllegalArgumentException("Invalid action");
		if (isComplete()) {
			getAgent().removeAction(this);
		} else {
			getAgent().setSite(site);
		}

	}

	@Override
	public int getEffort(Agent agent) {
		return 1;
	}
}
