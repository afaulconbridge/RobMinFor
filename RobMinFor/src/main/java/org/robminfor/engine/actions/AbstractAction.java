package org.robminfor.engine.actions;

import org.robminfor.engine.agents.Agent;

public abstract class AbstractAction {

	private Agent agent = null;
	
	abstract public void doAction();

	abstract public boolean isValid();
	

	public Agent getAgent() {
		return agent;
	}


	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
}
