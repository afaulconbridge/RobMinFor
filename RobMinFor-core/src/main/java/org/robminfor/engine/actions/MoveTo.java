package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This represents moving between transitable sites. For larger distances, NavigateTo should be used.
 * 
 * @author faulcon
 *
 */
public class MoveTo extends AbstractAction {

	private final Site site;

    private Logger log = LoggerFactory.getLogger(getClass());
	
	public MoveTo(Site site){
		super();
		this.site = site;
	}
	
	public void doAction(Agent agent) {
        //check if we can complete this action
        if (!isValid(agent)) {
        	log.trace("Aborting moveto, invalid");
        	agent.removeActionsOfType(MoveTo.class);
        } else if (!agent.getSite().isTransitable(site)) {
            //further away, need to pathfind
        	log.trace("Navigating moveto");
        	agent.removeActionsOfType(MoveTo.class);
        } else {
            //we are next to the target
        	log.trace("Completing moveto");
        	agent.setSite(this.site);
        	agent.removeAction(this);
        }
            
	}

	@Override
	public boolean isValid() {
		//can't move to solid objects
		if (site.isSolid()) {
			log.error("Trying to move into solid site");
			return false;
		}
		if (!site.isWalkable()) {
			log.error("Trying to move into unwalkabke site");
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
		if (!agent.getSite().isTransitable(site)) {
			
	        List<Site> path = site.getLandscape().findPath(agent.getSite(), site);
	        if (path == null) {
	        	return false; 
	        }
		}
        
		return true;
	}

	@Override
	public Site getSite() {
		return site;
	}
}
