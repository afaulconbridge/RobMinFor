package org.robminfor.util;

import java.util.Random;

public class Perlin {
	// JAVA REFERENCE IMPLEMENTATION OF IMPROVED NOISE - COPYRIGHT 2002 KEN PERLIN.

	public double noise(double x, double y, double z) {
		
		//FIND UNIT CUBE THAT CONTAINS POINT
		int X = (int) Math.floor(x) & 255;
		int Y = (int) Math.floor(y) & 255;
		int Z = (int) Math.floor(z) & 255;
		// FIND RELATIVE X,Y,Z OF POINT IN CUBE.
		x -= Math.floor(x); 
		y -= Math.floor(y);
		z -= Math.floor(z);
		 // COMPUTE FADE CURVES FOR EACH OF X,Y,Z.
		double u = fade(x);
		double v = fade(y); 
		double w = fade(z);
		// HASH COORDINATES OF THE 8 CUBE CORNERS
		int A = p[X] + Y;
		int AA = p[A] + Z; 
		int AB = p[A + 1] + Z;						
		int B = p[X + 1] + Y;
		int BA = p[B] + Z;
		int BB = p[B + 1] + Z;
															
		// AND ADD BLENDED RESULTS FROM 8 CORNERS OF CUBE
		return lerp(w, 
				lerp(v, lerp(u, grad(p[AA], x, y, z), 
						grad(p[BA], x - 1, y, z)), 
						lerp(u, grad(p[AB], x, y - 1, z), 
								grad(p[BB], x - 1, y - 1, z))), 
				lerp(v,
						lerp(u, grad(p[AA + 1], x, y, z - 1),  
								grad(p[BA + 1], x - 1, y, z - 1)),  
						lerp(u, grad(p[AB + 1], x, y - 1, z - 1),
								grad(p[BB + 1], x - 1, y - 1, z - 1))));
	}

	private double fade(double t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private double lerp(double t, double a, double b) {
		return a + t * (b - a);
	}

	private double grad(int hash, double x, double y, double z) {
		// CONVERT LO 4 BITS OF HASH CODE INTO 12 GRADIENT DIRECTIONS.
		int h = hash & 15; 
		double u = h < 8 ? x : y;
		double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	private final int p[] = new int[512];
	
	public Perlin(long seed) {
		Random rng = new Random(seed);
		for (int i = 0; i < 256; i++) {
			p[256 + i] = p[i] = rng.nextInt(256);
		}
	}
	
	public Perlin() {
		Random rng = new Random();
		for (int i = 0; i < 256; i++) {
			p[256 + i] = p[i] = rng.nextInt(256);
		}
	}
}
