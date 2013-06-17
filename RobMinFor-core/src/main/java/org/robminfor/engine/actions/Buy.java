package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.IStorage;

public class Buy extends AbstractAction {

	private final String name;
	private Site source = null;
	
	public Buy(String name) {
		this.name = name;
	}
	
	@Override
	public void doAction(Agent agent) {

		AbstractEntity thing = EntityManager.getEntityManager().getEntity(name);
		
        //check if we can complete this action
		if (!isValid(agent)) {
	    	agent.removeAction(this);
	    	source = null;
    	//cant complete it now, put it back on the queue
		} else if (agent.getSite().getLandscape().getMoney() < thing.getBuyValue()) {
	    	source = null;
	    	agent.getSite().getLandscape().addAction(this);
		} else {
			//find a source now and for ever
			if (source == null) {
				//TODO implement this in a generic fashion over all storage sites
				source = agent.getSite().getLandscape().getHomeSite();
			}

			IStorage store = (IStorage) source.getEntity();
			
			//move to source
			if (!agent.getSite().isAccessible(source)) {
	        	agent.addAction(new NavigateTo(source));
			} else {
				//actually at source so buy stuff
				if (agent.getSite().getLandscape().changeMoney(-thing.getBuyValue())) {
					store.addEntity(name);
				}
			}
		}

	}

	@Override
	public boolean isValid() {
		return true;
		//kind of hard to make this untrue
		//being out of cash doesn't make this invalid, just impossible at the moment
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
