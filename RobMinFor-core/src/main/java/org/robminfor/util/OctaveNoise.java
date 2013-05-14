package org.robminfor.util;

import java.util.Random;

public class OctaveNoise {

	private final int seed;
    
    private final int octaveCount;
    
    private final double[] wavelengths;
    private final double[] amplitudes;
		
	public OctaveNoise(int seed, int octaveCount) {
		this.seed = seed;
		this.octaveCount = octaveCount;
		wavelengths = new double[octaveCount];
		amplitudes = new double [octaveCount];
		for (int i = 0; i < octaveCount; i++) {
			wavelengths[i] = Math.pow(2.0, i);
			amplitudes[i] = 1.0/Math.pow(2.0, i);
		}
		//rescale amplitudes to sum to one
		double total = 0.0;
		for (int i = 0; i < octaveCount; i++) {
			total += amplitudes[i];
		}
		for (int i = 0; i < octaveCount; i++) {
			amplitudes[i] /= total;
		}
	}
	
	public int getOctaveCount() {
		return octaveCount;
	}
	
	public int getSeed() {
		return seed;
	}

	public double noise(double x, double y, double z) {
		double value = 0.0;
		for (int i = 0; i < octaveCount; i++) {
			double wavelength = wavelengths[i];
			double amplitude = amplitudes[i];
			
			double x1 = Math.floor(x / wavelength) * wavelength;
			double x2 = x1 + wavelength;
			double y1 = Math.floor(y / wavelength) * wavelength;
			double y2 = y1 + wavelength;
			double z1 = Math.floor(z / wavelength) * wavelength;
			double z2 = z1 + wavelength;
			
			
			double q000 = value(x1,y1,z1);
			double q001 = value(x1,y1,z2);
			double q010 = value(x1,y2,z1);
			double q011 = value(x1,y2,z2);
			double q100 = value(x2,y1,z1);
			double q101 = value(x2,y1,z2);
			double q110 = value(x2,y2,z1);
			double q111 = value(x2,y2,z2);
			
			//log.info("x1 = "+x1+", x = "+x+", x2 = "+x2);
			//log.info("y1 = "+y1+", y = "+y+", y2 = "+y2);
			//log.info("z1 = "+z1+", z = "+z+", z2 = "+z2);
			double addedValue = triLerp(x,y,z, q000, q001, q010, q011, q100, q101, q110, q111, x1, y1, z1, x2, y2, z2);
			//log.info("addedValue = "+addedValue);
			
			double scale = amplitude; 
					
			value += scale*addedValue;
		}
		return value;
	}
	
	private int hash32shift(int key) {
	  key = ~key + (key << 15); // key = (key << 15) - key - 1;
	  key = key ^ (key >>> 12);
	  key = key + (key << 2);
	  key = key ^ (key >>> 4);
	  key = key * 2057; // key = (key + (key << 3)) + (key << 11);
	  key = key ^ (key >>> 16);
	  return key;
	}
	
	private double value(double x, double y, double z) {
		Double xd = new Double(x);
		Double yd = new Double(y);
		Double zd = new Double(z);
		int seed = hash32shift(this.seed+hash32shift(xd.hashCode()+hash32shift(yd.hashCode()+hash32shift(zd.hashCode()))));
		
		Random tmprng = new Random(seed);
		double value = tmprng.nextDouble();
		//log.info("value x= "+x+", y= "+y+", z= "+z+" = "+value);
		return value;
	}

	/**
	 * x coordinate of target point
	 * x1 coordinate of lower grid corner
	 * x2 coordinate of upper grid corner
	 * q0,q1 values at 4 grid corners
	 */
	private double lerp(double x, double x1, double x2, double q0, double q1) {
		return (((x2 - x) / (x2 - x1)) * q0) + (((x - x1) / (x2 - x1)) * q1);
	}

	/**
	 * x,y coordinates of target point
	 * x1,y1 coordinates of lower grid corner
	 * x2,y2 coordinates of upper grid corner
	 * q11,q12,q21,q22 values at 4 grid corners
	 */
	private double biLerp(double x, double y, double q11, double q12, double q21, double q22, double x1, double x2, double y1, double y2) {
		double r1 = lerp(x, x1, x2, q11, q21);
		double r2 = lerp(x, x1, x2, q12, q22);
		return lerp(y, y1, y2, r1, r2);
	}
	
	/**
	 * x,y,z coordinates of target point
	 * x1,y1,z1 coordinates of lower grid corner
	 * x2,y2,z2 coordinates of upper grid corner
	 * q000,q001,q010,q011,q100,q101,q110,q111 values at 8 grid corners
	 */
	private double triLerp(double x, double y, double z, double q000, double q001, double q010, double q011, double q100, double q101, double q110, double q111, double x1, double y1, double z1, double x2, double y2, double z2) {
		if (x < x1) {
			throw new IllegalArgumentException("x must not be less than x1");
		}
		if (x > x2) {
			throw new IllegalArgumentException("x must not be greater than x2");
		}
		if (y < y1) {
			throw new IllegalArgumentException("y must not be less than y1");
		}
		if (y > y2) {
			throw new IllegalArgumentException("y must not be greater than y2");
		}
		if (z < z1) {
			throw new IllegalArgumentException("z must not be less than z1");
		}
		if (z > z2) {
			throw new IllegalArgumentException("z must not be greater than z2");
		}
		
		double x00 = lerp(x, x1, x2, q000, q100);
		double x10 = lerp(x, x1, x2, q010, q110);
		double x01 = lerp(x, x1, x2, q001, q101);
		double x11 = lerp(x, x1, x2, q011, q111);
		double r0 = lerp(y, y1, y2, x00, x01);
		double r1 = lerp(y, y1, y2, x10, x11);
	 
		return lerp(z, z1, z2, r0, r1);
	}
}
