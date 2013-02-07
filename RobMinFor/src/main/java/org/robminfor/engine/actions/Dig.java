package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.Air;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dig extends AbstractAction {

	private final Site site;

    private Logger log = LoggerFactory.getLogger(getClass());

	public Dig(Site site){
		super();
		this.site = site;
	}
	
	@Override
	public void doAction() {
		Agent agent = getAgent();
        //check if we can complete this action
		if (!isValid()){
			//target is not solid and thus not diggable
        	log.info("Aborting dig");
        	agent.removeAction(this);
		}else if (agent.getSite().isAccessible(site)) {
            //we are next to the target
        	log.info("Performing dig");
			site.setEntity(Air.getInstance());
        	agent.removeAction(this);
        	//TODO finish
        } else {
            //further away, need to pathfind
        	log.info("Moving to dig");
        	
            List<Site> path = site.getLandscape().findPath(agent.getSite(), site);
            if (path == null) {
                //cannot complete this, no valid path
                //stop moving
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
		//cant dig non-solid objects
		if (!site.isSolid()){
			return false;
		}
		return true;
	}

}
