package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAction {

    private Logger log = LoggerFactory.getLogger(getClass());

	abstract public void doAction(Agent agent);

	abstract public boolean isValid();
	abstract public boolean isValid(Agent agent);
}
