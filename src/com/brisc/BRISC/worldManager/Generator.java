package com.brisc.BRISC.worldManager;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import com.brisc.BRISC.entities.Planet;

public class Generator {
	
	String seed;

	public Generator(String seed) {
		
		this.seed = seed;
		
	}
	
	public void generate(Region r) {
		
		Random rand = new Random();
		rand.setSeed((seed + r.x + "-0-" + r.y + seed).hashCode());
		
		generatePlanets(rand, r);		
		
	}
	
	public static void generatePlanets(Random rand, Region r) {
		
		int planets = (int)Math.round(rand.nextGaussian() * 0.8 + 2.1);
		if(planets >= 1) {
			
			if(planets >= 2 && rand.nextDouble() <= (0.5 / planets) && (r.x + r.y) % 3 == 0)
				generateSolarSystem(rand, r, planets + 1);
			else
				generateRocks(rand, r, planets);
				
		}
		
	}
	
	public static void generateSolarSystem(Random rand, Region r, int planets) {
		
		int starSize;
		do
			starSize = (int)Math.round(rand.nextGaussian() * 50 + 400);
		while(starSize < 200 || starSize > 600);
		Point location = new Point((int)((r.x + rand.nextDouble()) * World.regionsize), 
				(int)((r.y + rand.nextDouble()) * World.regionsize));
		Planet p = new Planet(location.x, location.y, starSize, Color.orange);
		r.entities.add(p);
		
		double previousDist = starSize / 2;
		
		for(int x = 0; x < planets; x++) {
			
			int psize;
			
			do
				psize = (int)Math.round(rand.nextGaussian() * 10 + 120);
			while(psize < 90 || psize > 150);
			
			double ang = ((x + rand.nextDouble()) * Math.PI * 2 / planets);
			double dist = previousDist + psize / 2 + rand.nextDouble() * 10;
			
			previousDist = dist + psize / 2;
			
			double speed =  (rand.nextDouble() - 0.5) * 0.04 + 0.005 * rand.nextGaussian();
			
			Color c = Color.blue;
			if(rand.nextDouble() < 0.3)
				c = Color.red.darker().darker();
			if(rand.nextDouble() < 0.01)
				c = new Color(.2f, 0f, .5f);
			
			p = new Planet(
					location.x + (starSize - psize) / 2 + (Math.sin(ang) * dist), 
					location.y + (starSize - psize) / 2 + (Math.cos(ang) * dist), 
					psize, c, dist, ang, speed);
			r.entities.add(p);
			
		}
		
	}
	
	public static void generateRocks(Random rand, Region r, int planets) {
		
		for(int x = 0; x < planets; x++) {
			
			int size;
			do
				size = (int)Math.round(rand.nextGaussian() * 10 + 60);
			while(size < 20 || size > 100);
			Planet p = new Planet((r.x + rand.nextDouble()) * World.regionsize, (r.y + rand.nextDouble()) * World.regionsize, size, Color.gray);
			r.entities.add(p);
	
		}
		
	}

}
