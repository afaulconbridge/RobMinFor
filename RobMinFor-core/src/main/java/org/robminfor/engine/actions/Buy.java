package org.robminfor.engine.actions;

import java.util.ArrayList;
import java.util.Collection;

import org.robminfor.engine.Landscape;
import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.IStorage;

public class Buy extends AbstractAction {

	private final String name;
	private final AbstractEntity thing;
	private Site target = null;
	private final Landscape landscape; 

	public Buy(AbstractEntity thing, Landscape landscape) {
		this.name = thing.getName();
		this.thing = thing;
		this.landscape = landscape;
	}
	
	public Buy(String name, Landscape landscape) {
		this.name = name;
		this.thing = EntityManager.getEntityManager().getEntity(name);
		this.landscape = landscape;
	}
	
	@Override
	public void abort(Agent agent) {
		super.abort(agent);
    	target = null;
	}
	
	@Override
	public void doAction(Agent agent) {
        //check if we can complete this action
		if (!isValid(agent)) {
			abort(agent);
			return;
		}
			
		//find a target now and for ever
		if (target == null) {
			target = landscape.getNearestStorageFor(name, agent.getSite());
		}
		
		//move to target
		if (!agent.getSite().isAccessible(target)) {
        	agent.addAction(new NavigateToAccess(target, this));
		} else if (landscape.changeMoney(-thing.getBuyValue())) {
			//actually at target so buy stuff
			IStorage store = (IStorage) target.getEntity();
			store.addEntity(name);
			end(agent);		
			return;	
		}
	}

	@Override
	public boolean isValid() {		
		if (landscape.getMoney() < thing.getBuyValue()) {
			return false;
		}
				
		if (target == null) {
			//no storage exists or is available
			if (landscape.getStorageFor(name) == null) {
				return false;
			}
		} else {
			IStorage store = (IStorage) target.getEntity();
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
		if (target == null) {
			Site tempTarget = landscape.getNearestStorageFor(name, agent.getSite());
			if (!agent.getSite().isAccessible(tempTarget) || landscape.findPath(agent.getSite(), tempTarget) == null) {
				return false;
			}
		} else {
			if (!agent.getSite().isAccessible(target) || landscape.findPath(agent.getSite(), target) == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Site getSite() {
		//this may be null
		return target;
	}

}
