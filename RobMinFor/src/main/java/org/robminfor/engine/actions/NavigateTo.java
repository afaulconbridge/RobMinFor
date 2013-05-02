package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigateTo extends AbstractAction {

	private final Site site;

    private Logger log = LoggerFactory.getLogger(getClass());
	
	public NavigateTo(Site site){
		super();
		this.site = site;
	}
	
	public void doAction(Agent agent) {
        //check if we can complete this action
        if (!isValid(agent)) {
        	log.info("Aborting NavigateTo "+site+" because it is invalid");
        	agent.removeAction(this);
        } else if (!agent.getSite().isTransitable(site)) {
            //further away, need to pathfind
        	log.info("Navigating NavigateTo");
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
            	for (int i = path.size()-2; i >= 1; i--){
                	Site loc = path.get(i);
                	log.info("loc = "+loc);
                	agent.addAction(new MoveTo(path.get(i)));
                }
            }
        } else {
            //we are next to the target
        	log.info("Completed NavigateTo");
        	agent.removeAction(this);
        }
            
	}

	@Override
	public boolean isValid() {
		
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
