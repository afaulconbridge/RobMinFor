package org.robminfor.engine.actions;

import org.robminfor.engine.agents.Agent;

public abstract class AbstractAction {

	abstract public void doAction(Agent agent);

	abstract public boolean isValid();
	abstract public boolean isValid(Agent agent);
	
}
