package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deploy extends AbstractAction {

	private final Site target;
	private Site source;
	private final String name;
	
    private Logger log = LoggerFactory.getLogger(getClass());

	public Deploy(Site target, String name) {
		super();
		if (target == null) {
			throw new IllegalArgumentException("target must not be null");
		}
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}
		this.target = target;
		this.name = name;
	}
	
	private void abort(Agent agent) {
    	log.info("Aborting deploy");
    	agent.removeAction(this);
    	source = null;
	}
	
	@Override
	public void doAction(Agent agent) {
        //check if we can complete this action
		if (!isValid(agent)) {
			abort(agent);
		//check if we are carying something else we need to drop off
		} else if (agent.getInventory() != null && !name.equals(agent.getInventory().getName())) {
			Site site = target.getLandscape().findNearestStorageFor(agent.getSite(), agent.getInventory());
			agent.addAction(new Deliver(site));
    	//check if we have not collected it yet
		} else if (agent.getInventory() == null || !name.equals(agent.getInventory().getName())) {
			//go to the source
			if (source == null) {
				//TODO implement this in a generic fashion over all storage sites
				source = agent.getSite().getLandscape().getHomeSite();
			}
			if (!agent.getSite().isAccessible(source)) {
	        	agent.addAction(new NavigateTo(source));
        	//pick it up
			} else {				
		    	IStorage storage = (IStorage) source.getEntity();
		    	if (storage.containsEntity(name)) {
		    		agent.setInventory(storage.removeEntity(name));
		    	}
			}
		//we must be carrying the thing to deploy
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
		if (source != null) {
			synchronized(IStorage.class) {
				if (!IStorage.class.isInstance(source.getEntity())) {
					log.warn("Source entity is not an IStorage");
					return false;
				} else {
					IStorage sourceStorage = (IStorage) source.getEntity();
					if (!sourceStorage.containsEntity(name)) {
						log.warn("Source entity does not contain "+name);
						return false;
					}
				}
			}
		} else {
			//TODO implement this in a generic fashion over all storage sites
			IStorage sourceStorage = (IStorage) target.getLandscape().getHomeSite().getEntity();
			if (!sourceStorage.containsEntity(name)) {
				log.warn("No possible source entity contains "+name);
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

		//check we are not deploying to our current location
		if (source != null && agent.getSite() == source) {
			log.warn("Trying to take from site of agent");
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
