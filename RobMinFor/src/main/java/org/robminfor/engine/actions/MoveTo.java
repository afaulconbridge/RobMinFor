package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        	log.info("Aborting moveto");
        	agent.removeActionsOfType(MoveTo.class);
        } else if (agent.getSite().isTransitable(site)) {
            //we are next to the target
        	log.info("Completing moveto");
        	agent.setSite(this.site);
        	agent.removeAction(this);
        } else {
            //further away, need to pathfind
        	log.info("Navigating moveto");
            List<Site> path = site.getLandscape().findPath(agent.getSite(), site);
            if (path == null) {
                //cannot complete this, no valid path
                //stop moving
            	//Should already have been identifier in isValid check
            	log.warn("no path found");
            	agent.removeActionsOfType(MoveTo.class);
            } else {
                //remove any other moveto actions
            	agent.removeActionsOfType(MoveTo.class);

                //log.info("path length = "+path.size());
            	//log.info("agent.getSite() = "+agent.getSite());
                //re-pathfind
                for (int i = path.size()-1; i >=0; i--){
                	Site loc = path.get(i);
                	//log.info("loc = "+loc);
                	MoveTo action = new MoveTo(loc);
                	agent.addAction(action);
                }
            }
        }
            
	}

	@Override
	public boolean isValid() {
		//can't move to solid objects
		if (site.isSolid()) {
			return false;
		}
		if (!site.isWalkable()) {
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
}
