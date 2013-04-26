package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deploy extends AbstractAction {

	private Site target;
	private Site source;
	private AbstractEntity thing;
	
    private Logger log = LoggerFactory.getLogger(getClass());

	public Deploy(Site source, Site target, AbstractEntity thing) {
		super();
		this.source = source;
		this.target = target;
		this.thing = thing;
	}
	
	private void abort(Agent agent) {
    	log.info("Aborting deploy");
    	agent.removeAction(this);
	}
	
	@Override
	public void doAction(Agent agent) {
        //check if we can complete this action
		if (!isValid()) {
			abort(agent);
    	//check if we have collected it
		} else if (agent.getInventory() != thing) {
			// go pick it up
			if (agent.getSite().isAccessible(source)) {
				
			} else {
	        	MoveTo action = new MoveTo(source);
	        	agent.addAction(action);
			}
		}
		

	}

	@Override
	public boolean isValid() {
		//TODO add some criteria here
		return true;
	}
	
	@Override
	public boolean isValid(Agent agent) {
		if (!isValid()) {
			return false;
		}
		//TODO add some criteria here
		return true;
	}

}
