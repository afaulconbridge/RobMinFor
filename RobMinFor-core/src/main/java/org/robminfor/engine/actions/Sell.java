package org.robminfor.engine.actions;

import org.robminfor.engine.Landscape;
import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.IStorage;

public class Sell extends AbstractAction {

	private final String name;
	private final AbstractEntity thing;
	private Site source = null; 
	private final Landscape landscape;
	
	public Sell(AbstractEntity thing, Landscape landscape) {
		this.name = thing.getName();
		this.thing = thing;
		this.landscape = landscape;
	}
	
	public Sell(String name, Landscape landscape) {
		this.name = name;
		this.thing = EntityManager.getEntityManager().getEntity(name);
		this.landscape = landscape;
	}
	
	@Override
	public void abort(Agent agent) {
		super.abort(agent);
    	source = null;
	}
	
	@Override
	public void doAction(Agent agent) {
        //check if we can complete this action
		if (!isValid(agent)) {
	    	abort(agent);
	    	return;
		}
			
		//find a source now and for ever
		if (source == null) {
			source = agent.getSite().getLandscape().getNearestStorageContaining(name, agent.getSite());
		}
		
		IStorage store = (IStorage) source.getEntity();
		if (!store.containsEntity(name)) {
			//none of this in store any more
			//put back on todo list
			abort(agent);
	    	return;
		} else {
			//move to source
			if (!agent.getSite().isAccessible(source)) {
	        	agent.addAction(new NavigateToAccess(source, this));
			} else {
				//actually at source so buy stuff
				AbstractEntity thing = store.removeEntity(name);
				//couldn't complete the money sale, so put it back
				if (!landscape.changeMoney(thing.getSellValue())) {
					store.addEntity(thing.getName());
				}
				end(agent);
				return;
			}
		}
	}

	@Override
	public boolean isValid() {
		if (source == null) {
			Site tempSource = landscape.getStorageOf(name);
			if (tempSource == null) {
				return false;
			}
		} else {
			IStorage store = (IStorage) source.getEntity();
			if (!store.canStore(name)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean isValid(Agent agent) {
		if (!isValid()) {
			return false;
		}
		//there must be at least one possible target this agent can reach
		if (source == null) {
			Site tempSource = landscape.getNearestStorageFor(name, agent.getSite());
			if (!agent.getSite().isAccessible(tempSource) || landscape.findPath(agent.getSite(), tempSource) == null) {
				return false;
			}
		} else {
			if (!agent.getSite().isAccessible(source) || landscape.findPath(agent.getSite(), source) == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Site getSite() {
		//this may be null
		return source;
	}

}
