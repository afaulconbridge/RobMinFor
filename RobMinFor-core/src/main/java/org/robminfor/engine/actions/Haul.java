package org.robminfor.engine.actions;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Haul extends AbstractAction {

	private final Site source;
	private final Site target;
	private final AbstractEntity thing;
	
    private Logger log = LoggerFactory.getLogger(getClass());

	public Haul(Site source, Site target, AbstractEntity thing) {
		super();
		this.source = source;
		this.target = target;
		this.thing = thing;
	}
	
	@Override
	public void abort(Agent agent) {
		super.abort(agent);
	}
	
	@Override
	public void doAction(Agent agent) {
        //check if we can complete this action
		if (!isValid(agent)){
			abort(agent);
		}
    	agent.addAction(new Deliver(thing, target));
    	agent.addAction(new Collect(source, thing));
    	end(agent);
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
		return source;
	}

}
