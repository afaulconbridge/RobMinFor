package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deliver extends AbstractAction {

	private final Site site;
	
    private Logger log = LoggerFactory.getLogger(getClass());

	public Deliver(Site site) {
		super();
		this.site = site;
	}
	
	@Override
	public void doAction(Agent agent) {
        //check if we can complete this action
		if (!isValid()){
        	log.info("Aborting deliver");
        	agent.removeAction(this);
		} else if (agent.getSite().isAccessible(site)) {
	        //we are next to the target
	    	log.info("Performing deliver");
	    	IStorage storage = (IStorage) site.getEntity();
	    	storage.addEntity(agent.getInventory());
	    	agent.setInventory(null);
	    	agent.removeAction(this);
	    } else {
	        //further away, need to pathfind
	    	log.info("Moving to deliver");
        	MoveTo action = new MoveTo(site);
        	agent.addAction(action);
	    }
		

	}

	@Override
	public boolean isValid() {
		//cant deliver to non-solid objects
		if (!site.isSolid()) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isValid(Agent agent) {
		if (!isValid()) {
			return false;
		}
		return true;
	}

}
