package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dig extends AbstractAction {

	private final Site site;

    private Logger log = LoggerFactory.getLogger(getClass());

	public Dig(Site site) {
		super();
		this.site = site;
	}
	
	@Override
	public void doAction(Agent agent) {
        //check if we can complete this action
		if (!isValid()){
			//target is not solid and thus not diggable
        	log.info("Aborting dig");
        	agent.removeAction(this);
		} else if (!isValid(agent)){
			//target is not solid and thus not diggable
        	log.info("Aborting dig");
        	agent.removeAction(this);
        	agent.getSite().getLandscape().addAction(this);
		} else if (!agent.getSite().isAccessible(site)) {
            //further away, need to pathfind
        	log.info("Navigating to dig");
        	agent.addAction( new NavigateToAccess(site));
        } else {
            //we are next to the target
        	agent.pushInventory(site.getEntity());
			site.setEntity(EntityManager.getEntityManager().getEntity("Air"));
			//end this action
        	agent.removeAction(this);
        }
	}
	
	@Override
	public boolean isValid() {
		//can't dig non-solid objects
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
		//must be able to path there
		//either be directly adjacent or full path
		if (!agent.getSite().isTransitable(site) && site.getLandscape().findPath(agent.getSite(), site) == null) {
        	return false;
		}
		return true;
	}


	@Override
	public Site getSite() {
		return site;
	}
}
