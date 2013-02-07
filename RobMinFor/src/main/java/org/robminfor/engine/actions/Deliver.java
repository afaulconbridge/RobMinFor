package org.robminfor.engine.actions;

import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deliver extends AbstractAction {

    private Logger log = LoggerFactory.getLogger(getClass());
    
	@Override
	public void doAction() {
		Agent agent = getAgent();
        //check if we can complete this action
		if (!isValid()){
        	log.info("Aborting deliver");
        	agent.removeAction(this);
		} 

	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

}
