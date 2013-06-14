package org.robminfor.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pathfinder {

    private Logger log = LoggerFactory.getLogger(getClass());
	
	public List<Site> findPath(Site start, Site end){
		
		log.info("Finding path");
		
		Map<Site, Site> parents = new HashMap<Site, Site>();
		Map<Site, Float> f = new HashMap<Site, Float>();
		Map<Site, Float> g = new HashMap<Site, Float>();
		Map<Site, Float> h = new HashMap<Site, Float>();
		
		Queue<Site> open = new PriorityQueue<Site>(10, new PathComparator(f));
		List<Site> closed = new ArrayList<Site>();

		g.put(start, 0.0f);
		h.put(start, getScoreTo(start, end));
		f.put(start, g.get(start)+h.get(start));
		open.add(start);
		
		while (!closed.contains(end) && open.size() > 0){
			Site current = open.poll();
			closed.add(current);
			//log.info("foo "+current);
			//look at all adjacent sites
			for(Site adjacent : current.getAdjacents()){
				if (adjacent != end && !current.isTransitable(adjacent)){
					continue;
				}
				//log.info("bar "+adjacent);
				if (closed.contains(adjacent)) {
					//do nothing
				}  else if (open.contains(adjacent)){
					//already have a score for this site, see if it can improve
					//calculate new path rather than existing scored path 
					List<Site> newpath = getPath(current, start, parents);
					newpath.add(0, adjacent);
					//score the new path
					Float newg = getScoreFrom(newpath);
					//if its an improvement
					if (newg < g.get(adjacent)){
						//remove it first
						open.remove(adjacent);
						//update stuff
						parents.put(adjacent, current);
						g.put(adjacent, newg);
						f.put(adjacent, newg+h.get(adjacent));
						//re-add
						open.add(adjacent);
					}
				} else {
					//track parent
					parents.put(adjacent, current);
					//calculate scores
					List<Site> newpath = getPath(adjacent, start, parents);
					g.put(adjacent, getScoreFrom(newpath));
					h.put(adjacent, getScoreTo(adjacent, end));
					f.put(adjacent, g.get(adjacent)+h.get(adjacent));
					//add to open list
					open.add(adjacent);
				}
			}
		}
		
		
		if (!closed.contains(end)){
			//no path
			log.warn("Unable to find path from "+start+" to "+end);
			return null;
		} else {
			List<Site> path = getPath(end, start, parents);
			//log.trace("Found path from "+start+" to "+end);
			for (Site site : path) {
				//log.trace("  "+site);
			}
			if (path.contains(null)){
				throw new RuntimeException("path should not contain null(s)");
			}
			return path;
		}
	}
	
	private List<Site> getPath(Site site, Site start, Map<Site,Site> parents){
		//log.info("getting path");
		List<Site> path = new ArrayList<Site>();
		Site parent = site;
		while (parent != start){
			//log.info("Parent is "+parent);
			path.add(0, parent);
			if (!parents.containsKey(parent)){
				throw new RuntimeException("nonsense parent");
			}
			parent = parents.get(parent);
			if (parent == null || path.contains(parent)){
				throw new RuntimeException("Incorrect path");
			}
		}
		path.add(0, parent);
		//log.info("finished getting path");
		return path;
		
		
	}
	
	public Float getScoreFrom(List<Site> path){
		//override this
		//calculates g values
		Float score = 10.0f * path.size();
		return score;
	}
	
	public Float getScoreTo(Site site, Site end){
		//manhattan scoring
		//override this
		//calculates g values
		Float score = (float)Math.abs(site.getX()-end.getX());
		score += (float)Math.abs(site.getY()-end.getY());
		score += (float)Math.abs(site.getZ()-end.getZ());
		score *= 10.0f;
		return score;
	}
}
