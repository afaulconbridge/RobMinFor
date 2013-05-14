package org.robminfor.engine;

import java.util.Comparator;
import java.util.Map;

public class PathComparator implements Comparator<Site> {
	
	private final Map<Site, Float> f;
	
	public PathComparator(Map<Site, Float> f){
		this.f = f;
	}
	
	
	@Override
	public int compare(Site o1, Site o2) {
		Float d1 = f.get(o1);
		Float d2 = f.get(o2);
		if (d1 < d2){
			return -1;
		} else if (d1 > d2){
			return 1;
		} else {
			return 0;
		}
	}

}
