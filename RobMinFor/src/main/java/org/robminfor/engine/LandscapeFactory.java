package org.robminfor.engine;

import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.Air;
import org.robminfor.engine.entities.Crystal;
import org.robminfor.engine.entities.Home;
import org.robminfor.engine.entities.Ore;
import org.robminfor.util.OctaveNoise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LandscapeFactory {

	private static final LandscapeFactory singleton = new LandscapeFactory();
	
    private Logger log = LoggerFactory.getLogger(getClass());
	
	protected LandscapeFactory() {
		
	}
	
	public static LandscapeFactory getInstance() {
		return singleton;
	}

	public Landscape generate(int sizex, int sizey, int sizez) {
		log.info("Starting landscape generator");
		Landscape landscape = new Landscape(sizex, sizey, sizez);
		//specify a surface at 25% depth
		int surfaceheight = sizez/4;
		surfaceheight = Math.max(1,surfaceheight);
		//make above surface air
		for (int z = 0; z < surfaceheight; z++) {
			for (int y = 0; y < sizey; y++) {
				for (int x = 0; x < sizex; x++) {
					landscape.getSite(x,y,z).setEntity(Air.getInstance());
				}
			}
		}
		
		OctaveNoise oreNoise = new OctaveNoise(1,6);
		OctaveNoise crystalNoise = new OctaveNoise(2,6);
		
		//for some locations below the surface, make some ore and some crystal
		double target = 0.90; //not a percentage, but 0 < x < 1
		for (int z = surfaceheight; z < sizez; z++) {
			for (int y = 0; y < sizey; y++) {
				for (int x = 0; x < sizex; x++) {
					double dx = new Double(x);
					double dy = new Double(y);
					double dz = new Double(z);
					double oreValue = oreNoise.noise(dx/20.0, dy/20.0, dz/10.0);
					double crystalValue = crystalNoise.noise(dx/5.0, dy/5.0, dz/2.5);
					
					//log.info("ore value "+oreValue+", x = "+x+", y = "+y+", z = "+z);
					//log.info("crystal value "+oreValue+", x = "+x+", y = "+y+", z = "+z);
					
					if ( oreValue > target && oreValue > crystalValue) {
						landscape.getSite(x,y,z).setEntity(Ore.getInstance());
						log.info("Ore!");
					} else if ( crystalValue > target && crystalValue > oreValue) {
						landscape.getSite(x,y,z).setEntity(Crystal.getInstance());
						log.info("Crystal!");
					}
				}
			}
		}
		
		//put a home on the surface
		Home home = Home.getInstance();
		landscape.getSite(sizex/2, sizey/2, surfaceheight-1).setEntity(home);
		landscape.setHomeSite(landscape.getSite(sizex/2, sizey/2, surfaceheight-1));
		log.info("Finished landscape generator");
		
		//add some entities to start with
		Agent agent = new Agent(landscape.getSite((sizex/2)+1, sizey/2, surfaceheight-1), home);
		landscape.addAgent(agent);
		agent = new Agent(landscape.getSite((sizex/2)-1, sizey/2, surfaceheight-1), home);
		landscape.addAgent(agent);
		agent = new Agent(landscape.getSite(sizex/2, (sizey/2)+1, surfaceheight-1), home);
		landscape.addAgent(agent);
		agent = new Agent(landscape.getSite(sizex/2, (sizey/2)-1, surfaceheight-1), home);
		landscape.addAgent(agent);
		
		return landscape;
	}

}
