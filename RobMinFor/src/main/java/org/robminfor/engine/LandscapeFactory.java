package org.robminfor.engine;

import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.Air;
import org.robminfor.engine.entities.Home;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LandscapeFactory {

	private static final LandscapeFactory singleton = new LandscapeFactory();
	
    private Logger log = LoggerFactory.getLogger(getClass());
	
	protected LandscapeFactory() {
		
	}
	
	public static LandscapeFactory getInstance(){
		return singleton;
	}

	public Landscape generate(int sizex, int sizey, int sizez){
		log.info("Starting landscape generator");
		Landscape landscape = new Landscape(sizex, sizey, sizez);
		//specify a surface at 25% depth
		int surfaceheight = sizez/4;
		//make above surface air
		for (int z = 0; z < Math.max(1,surfaceheight); z++){
			for (int y = 0; y < sizey; y++){
				for (int x = 0; x < sizex; x++){
					landscape.getSite(x,y,z).setEntity(Air.getInstance());
				}
			}
		}
		
		//put a home on the surface
		landscape.getSite(sizex/2, sizey/2, surfaceheight-1).setEntity(Home.getInstance());
		log.info("Finished landscape generator");
		
		//add some entities to start with
		Agent agent = new Agent(landscape.getSite((sizex/2)+1, sizey/2, surfaceheight-1));
		landscape.addAgent(agent);
		//agent = new Agent(landscape.getSite((sizex/2)-1, sizey/2, surfaceheight-1));
		//landscape.addAgent(agent);
		//agent = new Agent(landscape.getSite(sizex/2, (sizey/2)+1, surfaceheight-1));
		//landscape.addAgent(agent);
		//agent = new Agent(landscape.getSite(sizex/2, (sizey/2)-1, surfaceheight-1));
		//landscape.addAgent(agent);
		
		return landscape;
	}

}
