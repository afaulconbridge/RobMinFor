package org.robminfor.engine.actions;

import java.util.Collection;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAction {

    private Logger log = LoggerFactory.getLogger(getClass());

	abstract public void doAction(Agent agent);

	/**
	 * This should return false if this action is non-sense e.g. excavate air
	 * 
	 * This should return false for transient failures e.g. not enough supplies
	 * 
	 * @return
	 */
	abstract public boolean isValid();
	
	/**
	 * Calls isValid() first
	 * 
	 * This should return false if this agent is not able to carry it out e.g. can't path to it
	 * 
	 * @return
	 */
	abstract public boolean isValid(Agent agent);
	
	abstract public Site getSite();
	
	public void abort(Agent agent) {
    	agent.getSite().getLandscape().addAction(agent.flushActions());
	}
	
	public void end(Agent agent) {
    	agent.removeAction(this);
	}
}
