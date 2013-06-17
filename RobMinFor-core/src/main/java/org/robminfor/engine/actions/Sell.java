package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.IStorage;

public class Sell extends AbstractAction {

	private final String name;
	private Site source = null;
	
	public Sell(String name) {
		this.name = name;
	}
	
	@Override
	public void doAction(Agent agent) {

		
        //check if we can complete this action
		if (!isValid(agent)) {
	    	agent.removeAction(this);
	    	source = null;
		} else {
			
			//find a source now and for ever
			if (source == null) {
				//TODO implement this in a generic fashion over all storage sites
				source = agent.getSite().getLandscape().getHomeSite();
			}
			
			IStorage store = (IStorage) source.getEntity();
			if (!store.containsEntity(name)) {
				//none of this in store
				//put back on todo list
		    	source = null;
		    	agent.getSite().getLandscape().addAction(this);
		    	return;
		    	
			} else {
			
				//move to source
				if (!agent.getSite().isAccessible(source)) {
		        	agent.addAction(new NavigateTo(source));
				} else {
					//actually at source so buy stuff
					AbstractEntity thing = store.removeEntity(name);
					agent.getSite().getLandscape().changeMoney(thing.getSellValue());
				}
			}
		}

	}

	@Override
	public boolean isValid() {
		return true;
		//kind of hard to make this untrue
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
		//this may be null
		return source;
	}

}
