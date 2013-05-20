package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deploy extends AbstractAction {

	private final Site target;
	private final Site source;
	private final AbstractEntity thing;
	
    private Logger log = LoggerFactory.getLogger(getClass());

	public Deploy(Site source, Site target, AbstractEntity thing) {
		super();
		if (source == null) {
			throw new IllegalArgumentException("source must not be null");
		}
		if (target == null) {
			throw new IllegalArgumentException("target must not be null");
		}
		if (thing == null) {
			throw new IllegalArgumentException("thing must not be null");
		}
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
		if (!isValid(agent)) {
			abort(agent);
    	//check if we have collected it
		} else if (agent.getInventory() != thing) {
			//go to the source
			if (!agent.getSite().isAccessible(source)) {
	        	agent.addAction(new NavigateTo(source));
        	//pick it up
			} else {				
		    	IStorage storage = (IStorage) source.getEntity();
		    	storage.removeEntity(thing.getName());
		    	agent.setInventory(thing);
			}
		//if we are not next to the target, go to it
		} else if (!agent.getSite().isAccessible(target)) {
        	agent.addAction(new NavigateTo(target));
    	//we are next to target, deploy
		} else {
			target.setEntity(agent.getInventory());
			agent.setInventory(null);
			//end of this action
	    	agent.removeAction(this);
		}
	}

	@Override
	public boolean isValid() {
		
		
		synchronized(IStorage.class) {
			if (!IStorage.class.isInstance(source.getEntity())) {
				log.warn("Source entity is not an IStorage");
				return false;
			}
		}
		//TODO add some more criteria here
		return true;
	}
	
	@Override
	public boolean isValid(Agent agent) {
		if (!isValid()) {
			return false;
		}
		
		IStorage sourceStorage = null;
		synchronized(IStorage.class) {
			if (IStorage.class.isInstance(source.getEntity())) {
				sourceStorage = (IStorage) (source.getEntity());
			}
		}
		if (sourceStorage == null) {
			log.warn("Source entity is null for IStorage");
			return false;
		}
		
		if (agent.getInventory() != thing && !sourceStorage.containsEntity(thing.getName())) {
			log.warn("thing is not in inventory or source entity storage");
			return false;
		}
		
		//check we are not deploying to our current location
		if (agent.getSite() == target) {
			log.warn("Trying to deploy on top of agent");
			return false;
		}
		//TODO add some more criteria here
		return true;
	}

	@Override
	public Site getSite() {
		return target;
	}

}
