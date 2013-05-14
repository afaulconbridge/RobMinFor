package org.robminfor.engine.actions;

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
		if (!isValid(agent)){
        	log.info("Aborting deliver");
        	agent.removeAction(this);
		} else if (!agent.getSite().isAccessible(site)) {
	        //further away, need to pathfind
	    	log.info("Navigating to deliver");
        	agent.addAction( new NavigateTo(site));
	    } else {
	        //we are next to the target
	    	log.info("Performing deliver");
	    	IStorage storage = (IStorage) site.getEntity();
	    	storage.addEntity(agent.getInventory());
	    	agent.setInventory(null);
	    	//end this action
	    	agent.removeAction(this);
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

	@Override
	public Site getSite() {
		return site;
	}

}
