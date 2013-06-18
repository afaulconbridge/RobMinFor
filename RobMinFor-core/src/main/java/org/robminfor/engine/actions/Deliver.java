package org.robminfor.engine.actions;

import org.robminfor.engine.Landscape;
import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deliver extends AbstractAction {

	private Site target = null;
	private AbstractEntity thing = null;
	
    private Logger log = LoggerFactory.getLogger(getClass());

	public Deliver(AbstractEntity thing, Site target) {
		super();
		this.thing = thing;
		this.target = target;
	}
	
	
	@Override
	public void abort(Agent agent) {
		super.abort(agent);
	}
	
	@Override
	public void doAction(Agent agent) {
		if (!isValid(agent)){
			abort(agent);
			return;
		} else if (!agent.getSite().isAccessible(target)) {
	        //further away, need to pathfind
	    	log.info("Navigating to deliver");
        	agent.addAction(new NavigateToAccess(target));
	    } else {
	        //we are next to the target
	    	log.info("Performing deliver");
	    	boolean isStorage;
	    	synchronized(IStorage.class) {
	    		isStorage = IStorage.class.isInstance(target.getEntity());
	    	}
	    	//target is a storage, so put this in it
	    	if (isStorage) {
		    	IStorage storage = (IStorage) target.getEntity();
		    	storage.addEntity(agent.popInventory().getName());
	    	} else {
	    		//target is not storage
	    		//swap what we are carrying with it
	    		AbstractEntity other = target.getEntity();
	    		target.setEntity(agent.popInventory());
    			agent.pushInventory(other);
	    	}
	    	//end this action
	    	end(agent);
	    	return;
	    }
	}

	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public boolean isValid(Agent agent) {
		if (!isValid()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Site getSite() {
		return target;
	}

}
