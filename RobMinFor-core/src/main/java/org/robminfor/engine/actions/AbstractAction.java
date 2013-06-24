package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAction {

	private Logger log = LoggerFactory.getLogger(getClass());

	private Agent agent = null;

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Agent getAgent() {
		return agent;
	}

	abstract public void doAction();

	/**
	 * This should return false if this action is non-sense e.g. excavate air
	 * 
	 * This should return true for solveable transient failures e.g. not enough
	 * supplies
	 * 
	 * @return
	 */
	abstract public boolean isValid();

	/**
	 * This should return true if this is finished
	 * 
	 * @return
	 */
	abstract public boolean isComplete();

	/**
	 * This should return false if this action is non-sense e.g. excavate air
	 * 
	 * This should return false if there any solveable transient failures e.g.
	 * not enough supplies
	 * 
	 * @return
	 */
	abstract public boolean isCompletable();

	abstract public Site getSite();

	abstract public int getEffort(Agent agent);

}
