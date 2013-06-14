package org.robminfor.engine;

import java.util.Random;

import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.Home;
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

	public Landscape generate(int sizex, int sizey, int sizez, int seed) {
		log.info("Starting landscape generator");
		AbstractEntity ore = EntityManager.getEntityManager().getEntity("Ore");
		AbstractEntity crystal = EntityManager.getEntityManager().getEntity("Crystal");
		AbstractEntity air = EntityManager.getEntityManager().getEntity("Air");
		
		Landscape landscape = new Landscape(sizex, sizey, sizez);
		//specify a surface at 25% depth
		int surfaceheight = sizez/4;
		surfaceheight = Math.max(1,surfaceheight);
		//make above surface air
		for (int z = 0; z < surfaceheight; z++) {
			for (int y = 0; y < sizey; y++) {
				for (int x = 0; x < sizex; x++) {
					landscape.getSite(x,y,z).setEntity(air);
					//actually, limited visibility doesn't really help gameplay
					landscape.getSite(x,y,z).setVisible();
				}
			}
		}
		Random rng = new Random(seed);
		OctaveNoise oreNoise = new OctaveNoise(rng.nextInt(),8);
		OctaveNoise crystalNoise = new OctaveNoise(rng.nextInt(),8);
		
		//for some locations below the surface, make some ore and some crystal
		
		//not a percentage
		//assume a mean of 0.5 and a stddev of 0.1 (reasonable based on sampling)
		double target = 0.80;
		for (int z = surfaceheight; z < sizez; z++) {
			for (int y = 0; y < sizey; y++) {
				for (int x = 0; x < sizex; x++) {
					double dx = new Double(x);
					double dy = new Double(y);
					double dz = new Double(z);
					double oreValue = oreNoise.noise(dx/10.0, dy/10.0, dz/5.0);
					double crystalValue = crystalNoise.noise(dx/10.0, dy/10.0, dz/5.0);
					
					//
					
					
					//log.info("ore value "+oreValue+", x = "+x+", y = "+y+", z = "+z);
					//log.info("crystal value "+oreValue+", x = "+x+", y = "+y+", z = "+z);
					
					if ( oreValue > target && oreValue > crystalValue) {
						landscape.getSite(x,y,z).setEntity(ore);
						log.info("Ore!");
					} else if ( crystalValue > target && crystalValue > oreValue) {
						landscape.getSite(x,y,z).setEntity(crystal);
						log.info("Crystal!");
					}
				}
			}
		}
		
		//put a home on the surface
		Home home = new Home();
		Site homesite = landscape.getSite(sizex/2, sizey/2, surfaceheight-1);
		homesite.setEntity(home);
		landscape.setHomeSite(homesite);
		homesite.setVisible();
		for (Site other : homesite.getAdjacents()) {
			if (!other.isSolid()) {
				other.setVisible(); //this will recurse as needed to other sites
			}
		}
		
		//add some entities to start with
		Agent agent;
		agent = new Agent(landscape.getSite((sizex/2)+1, sizey/2, surfaceheight-1));
		landscape.addAgent(agent);
		agent = new Agent(landscape.getSite((sizex/2)-1, sizey/2, surfaceheight-1));
		landscape.addAgent(agent);
		agent = new Agent(landscape.getSite(sizex/2, (sizey/2)+1, surfaceheight-1));
		landscape.addAgent(agent);
		agent = new Agent(landscape.getSite(sizex/2, (sizey/2)-1, surfaceheight-1));
		landscape.addAgent(agent);

		log.info("Finished landscape generator");
		return landscape;
	}
}
