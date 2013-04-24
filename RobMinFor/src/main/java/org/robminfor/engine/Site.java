package org.robminfor.engine;

import java.util.ArrayList;
import java.util.List;

import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.util.Vect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Site {

    private Logger log = LoggerFactory.getLogger(getClass());

	private AbstractEntity entity;
	private final Vect position;
	private final Landscape landscape;	
	private boolean visible = false;
	
	public Site(AbstractEntity entity, Vect position, Landscape landscape) {
		super();
		this.setEntity(entity);
		this.position = position;
		this.landscape = landscape;
	}

	public AbstractEntity getEntity() {
		return entity;
	}

	public void setEntity(AbstractEntity entity) {
		AbstractEntity oldentity = this.entity;
		this.entity = entity;
		
		//if visible and we have been taken from solid to non-solid
		if (isVisible() && oldentity.isSolid() && !entity.isSolid()) {
			setVisible();
		}
	}
	
	public Vect getPosition() {
		return position;
	}
	
	public int getX() {
		return getPosition().getX();
	}
	
	public int getY() {
		return getPosition().getY();
	}
	
	public int getZ() {
		return getPosition().getZ();
	}
	
	public Landscape getLandscape() {
		return landscape;
	}

	
	public boolean isSolid() {
		return getEntity().isSolid();
	}
	
	public boolean isWalkable() {
		if (isSolid()){
			return false;
		} else {
			return landscape.isSolid(position.getX(), position.getY(), position.getZ()+1);
		}
	}
	
	
	public boolean isAdjacent(Site other) {
        if (Math.abs(getZ()-other.getZ()) <= 1
        		&& Math.abs(getX()-other.getX()) + Math.abs(getY()-other.getY()) <= 1) {
            return true;
        } else {
        	return false;
        }
	}
	
	/**
	 * @return List<Site> sites that are adjacent including diagonals and inside the landscape
	 */
	public List<Site> getAdjacents(){
		List<Site> adjacents = new ArrayList<Site>();
		for(int x = -1; x <= 1; x++){
			for(int y = -1; y <= 1; y++){
				for(int z = -1; z <= 1; z++){
					Site other = landscape.getSite(getX()+x, getY()+y, getZ()+z);
					if (other != null && other != this && isAdjacent(other)){
						adjacents.add(other);
					}
				}	
			}	
		}
		//log.info("Found "+adjacents.size()+" adjacent sites to "+this);
		return adjacents;
	}
	
    public boolean isAccessible(Site other) {
        //everything that is accessible, is adjacent
        if (!isAdjacent(other)){
        	//log.info("Is not adacent");
            return false;
        
        //check it is standable at the start
        } else if (!isWalkable()){
        	//log.info("Is not walkable");
            return false;
        
        //step down
        } else if (getZ() < other.getZ()){
        	//site above target must be empty
        	if (landscape.isSolid(other.getX(), other.getY(), other.getZ()-1)){
        		return false;
        	} else {
        		return true;
        	}
        //step up
        } else if (getZ() > other.getZ()){
        	//site above self must be empty
        	if (landscape.isSolid(getX(), getY(), getZ()-1)){
        		return false;
        	} else {
        		return true;
        	}                
        //same level
        } else {
            return true;
        }
    }
    
	public List<Site> getAccessibles(){
		List<Site> accessibles = new ArrayList<Site>();
		for (Site other : getAdjacents()) {
			if (isAccessible(other)) {
				accessibles.add(other);
			}	
		}
		//log.info("Found "+accessibles.size()+" accessible sites to "+this);
		return accessibles;
	}
    
    public boolean isTransitable(Site other){
        //everything that is transitable, is accessible
        if (!isAccessible(other)) {
            //log.info("other is NOT accessible "+other);
            return false;
        //check it is standable at the start
    	} else if (!other.isWalkable()) {
            //log.info("other is accessible but NOT walkable "+other);
            return false;
        //don't count straight down as transitable
        //avoids digging ground out from underneath, but stops existence of ladders/lifts
    	} else if (other.getX() == getX() && other.getY() == getY() && other.getZ() == getZ()+1) {
    		return false; 
    	} else {
            //log.info("other is accessible "+other);
            return true;
    	}
    }
    
	public List<Site> getTransitables(){
		List<Site> transitables = new ArrayList<Site>();
		for (Site other : getAccessibles()) {
			if (isTransitable(other)) {
				transitables.add(other);
			}	
		}
		//log.info("Found "+transitables.size()+" transitable sites to "+this);
		return transitables;
	}
	
	@Override
	public String toString(){
		return "Site "+getX()+","+getY()+","+getZ();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible() {
		this.visible = true;
		if (!isSolid()) {
			for(Site other : getAdjacents()) {
				if (!other.isVisible()) {
					other.setVisible();
				}
			}
		}
	}
}
