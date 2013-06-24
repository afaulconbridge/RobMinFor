package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigateToAccess extends AbstractAction {

	private final Site site;

	private Logger log = LoggerFactory.getLogger(getClass());

	public NavigateToAccess(Site site) {
		super();
		this.site = site;
	}

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public boolean isValid() {
		// this always returns as valid
		// target being solid/empty does not matter - you only need to be able
		// to access it
		// being unable to find a path is a transient fail
		return true;
	}

	@Override
	public boolean isComplete() {
		if (getAgent() == null) {
			return false;
		} else if (getAgent().getSite().isAccessible(site)) {
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
				.findPath(getAgent().getSite(), site) == null) {
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
		else if (getAgent() == null)
			throw new IllegalArgumentException("Invalid action");
		else if (isComplete()) {
			getAgent().removeAction(this);
		} else if (!isCompletable()) {
			// sensible action, but not achieavble right now
			getAgent().getSite().getLandscape()
					.addAction(getAgent().flushActions());
		} else {
			List<Site> path = site.getLandscape().findPath(
					getAgent().getSite(), site);
			if (path == null) {
				// cannot complete this, no valid path
				// Should already have been identified in isCompletable check
				log.warn("no path found");
			} else {
				// log.info("path length = "+path.size());
				// log.info("agent.getSite() = "+agent.getSite());
				// re-pathfind
				// add the actions in reverse order because actions are a stack
				// don't do the last one because thats what we want to access
				for (int i = path.size() - 2; i >= 1; i--) {
					Site loc = path.get(i);
					log.trace("loc = " + loc);
					getAgent().addAction(new MoveTo(path.get(i)));
				}
			}
		}
	}

	@Override
	public int getEffort(Agent agent) {
		return agent.getSite().getLandscape().findPath(agent.getSite(), site)
				.size();
	}
}
