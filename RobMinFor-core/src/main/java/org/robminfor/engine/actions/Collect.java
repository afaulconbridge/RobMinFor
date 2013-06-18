package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Collect extends AbstractAction {

	private final Site source;
	private final AbstractEntity thing;
	
    private Logger log = LoggerFactory.getLogger(getClass());

	public Collect(Site source, AbstractEntity thing) {
		super();
		this.source = source;
		this.thing = thing;
	}
	
	@Override
	public void abort(Agent agent) {
		//do not place back onto global queue
    	end(agent);
	}
	
	@Override
	public void doAction(Agent agent) {
        //check if we can complete this action
		if (!isValid(agent)){
			abort(agent);
        //further away, need to pathfind
		} else if (!agent.getSite().isAccessible(source)) {
	    	log.info("Navigating to collect");
        	agent.addAction(new NavigateToAccess(source));
	    //we are next to the source with empty inventory
	    } else {
	    	log.info("Performing collect");
	    	boolean isStorage;
	    	synchronized(IStorage.class) {
	    		isStorage = IStorage.class.isInstance(source.getEntity());
	    	}
	    	//source is a storage, so put this in it
	    	if (isStorage) {
		    	IStorage storage = (IStorage) source.getEntity();
		    	agent.pushInventory(storage.removeEntity(thing.getName()));
	    	} else {
	    		//source is not storage
	    		//pick up what is there
	    		AbstractEntity other = source.getEntity();
	    		source.setEntity(EntityManager.getEntityManager().getEntity("Air"));
	    		if (other.isSolid()) {
	    			agent.pushInventory(other);
	    		}
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
		} else if (agent.peekInventory() != null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Site getSite() {
		return source;
	}

}
