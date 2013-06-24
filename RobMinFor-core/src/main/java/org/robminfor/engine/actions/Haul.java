package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;

public class Haul extends AbstractAction {

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCompletable() {
		if (getAgent() == null) {
			return false;
		} else {
			throw new RuntimeException("Not implemented");
			// TODO Auto-generated method stub
		}
	}

	@Override
	public void doAction() {
		if (!isValid())
			throw new IllegalArgumentException(
					"Agent must be assigned before doing action");
		if (getAgent() == null)
			throw new IllegalArgumentException("Invalid action");
		// TODO Auto-generated method stub
	}

	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEffort(Agent agent) {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

}
