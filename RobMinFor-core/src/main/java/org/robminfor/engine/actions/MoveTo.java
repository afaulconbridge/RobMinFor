package org.robminfor.engine.actions;

import java.util.List;

import org.robminfor.engine.Site;
import org.robminfor.engine.agents.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This represents moving between transitable sites. For larger distances, NavigateToAccess should be used.
 * 
 * @author faulcon
 *
 */
public class MoveTo extends AbstractAction {

	private final Site site;
	private AbstractAction parent;

    private Logger log = LoggerFactory.getLogger(getClass());
	
	public MoveTo(Site site, AbstractAction parent){
		super();
		this.site = site;
		this.parent = parent;
	}
	
	public void doAction(Agent agent) {
        //check if we can complete this action
		if (!agent.getSite().isTransitable(site)) {
        	log.trace("Navigating moveto");
        	agent.removeActionsOfType(MoveTo.class);
        } else {
            //we are next to the target
        	log.trace("Completing moveto");
        	agent.setSite(site);
        	agent.removeAction(this);
        }
            
	}

	@Override
	public boolean isValid() {
		//can't move to solid objects
		if (site.isSolid()) {
			log.error("Trying to move into solid site");
			return false;
		} else if (!site.isWalkable()) {
			log.error("Trying to move into unwalkable site");
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
		if (!agent.getSite().isTransitable(site)) {
        	return false; 
		}
		return true;
	}

	@Override
	public Site getSite() {
		return site;
	}
}
