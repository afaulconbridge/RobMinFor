package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigateToAccess extends AbstractAction {

	private final Site site;
	private AbstractAction parent;

    private Logger log = LoggerFactory.getLogger(getClass());
	
	public NavigateToAccess(Site site, AbstractAction parent){
		super();
		this.site = site;
		this.parent = parent;
	}
	
	
	@Override
	public void abort(Agent agent) {
		agent.flushActions();
    	agent.removeActionsOfType(MoveTo.class);
    	agent.removeActionsOfType(NavigateToAccess.class);
	}
	
	public void doAction(Agent agent) {
        //check if we can complete this action
        if (!isValid(agent)) {
        	log.info("Aborting NavigateToAccess "+site+" because it is invalid");
        	abort(agent);
        } else if (agent.getSite() != site) {
            //further away, need to pathfind
        	log.trace("Navigating NavigateToAccess");
            //remove any moveto actions
        	agent.removeActionsOfType(MoveTo.class);
        	agent.removeAction(this);
        	
            List<Site> path = site.getLandscape().findPath(agent.getSite(), site);
            if (path == null) {
                //cannot complete this, no valid path
                //stop moving
            	//Should already have been identified in isValid check
            	log.warn("no path found");
            } else {
                //log.info("path length = "+path.size());
            	//log.info("agent.getSite() = "+agent.getSite());
                //re-pathfind
            	//add the actions in reverse order because actions are a stack
            	//don't do the last one because thats what we want to access
            	for (int i = path.size()-2; i >= 1; i--){
                	Site loc = path.get(i);
                	log.trace("loc = "+loc);
                	agent.addAction(new MoveTo(path.get(i), this));
                }
            }
        } else {
            //we are next to the target
        	log.trace("Completed NavigateToAccess");
        	end(agent);
        }
            
	}

	@Override
	public boolean isValid() {
		//this always returns as valid
		//target being solid/empty does not matter - you only need to be able to access it
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
