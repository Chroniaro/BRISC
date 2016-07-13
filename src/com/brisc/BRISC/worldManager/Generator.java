package com.brisc.BRISC.worldManager;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import com.brisc.BRISC.entities.Enemy;
import com.brisc.BRISC.entities.EnemyBig;
import com.brisc.BRISC.entities.Planet;

public class Generator {
	
	String seed;

	public Generator(String seed) {
		
		this.seed = seed;
		
	}
	
	public void generate(Region r) {
		
		Random rand = new Random();
		rand.setSeed((seed + r.x + "-0-" + r.y + seed).hashCode());
		
		generatePlanets(rand, r, seed);		
		
	}
	
	public static void generatePlanets(Random rand, Region r, String seed) {
		
		double planets = getPlanetNumber(rand);
		if(planets >= 1) {
			
			if(shouldGenSystem(rand, r, seed, planets))
				generateSolarSystem(rand, r, (int)Math.round(planets) + 1);
			else
				generateRocks(rand, r, (int)Math.round(planets));
				
		}
		
	}
	
	public static boolean shouldGenSystem(Random rand, Region r, String seed, double planets) {
		
		if(planets >= 2)
			for(int x = -1; x <= 1; x++)
				for(int y = -1; y <= 1; y++) {
					
					if(x == 0 && y == 0) continue;
					
					Random checkRand = new Random();
					checkRand.setSeed((seed + (r.x + x) + "-0-" + (r.y + y) + seed).hashCode());
					double checkP = getPlanetNumber(checkRand);
					if(checkP >= planets)
						return false;
					
				}
		else
			return false;
		
		return true;
		
	}
	
	public static double getPlanetNumber(Random rand) {
		
		return rand.nextGaussian() * 0.8 + 2.1;
		
	}
	
	public static void generateSolarSystem(Random rand, Region r, int planets) {
		
		int starSize;
		do
			starSize = (int)Math.round(rand.nextGaussian() * 50 + 400);
		while(starSize < 200 || starSize > 600);
		Point location = new Point((int)((r.x + rand.nextDouble()) * World.regionsize), 
				(int)((r.y + rand.nextDouble()) * World.regionsize));
		Planet p = new Planet(location.x, location.y, starSize, Color.orange);
		r.addEntity(p);
		p.setVisible(true);
		
		double previousDist = starSize / 2;
		
		for(int x = 0; x < planets; x++) {
			
			int psize;
			
			do
				psize = (int)Math.round(rand.nextGaussian() * 10 + 120);
			while(psize < 90 || psize > 150);
			
			double ang = ((x + rand.nextDouble()) * Math.PI * 2 / planets);
			double dist = previousDist + psize / 2 + rand.nextDouble() * 10;
			
			previousDist = dist + psize / 2;
			
			double speed =  0.01 + (rand.nextDouble() - 0.5) * 0.04 + 0.005 * rand.nextGaussian();
			
			Color c = Color.blue;
			if(rand.nextDouble() < 0.3)
				c = Color.red.darker().darker();
			if(rand.nextDouble() < 0.02)
				c = new Color(.15f, 0f, .5f);
			
			if(rand.nextDouble() < 0.05) {
				
				EnemyBig e = new EnemyBig(
						location.x + (starSize - psize) / 2 + (Math.sin(ang) * dist), 
						location.y + (starSize - psize) / 2 + (Math.cos(ang) * dist), 
						dist, ang, speed, new Point(location.x + starSize / 2, location.y + starSize / 2)
				);
				
				r.addEntity(e);
				e.layer = 3;
				e.setVisible(true);
				
			} else if(rand.nextDouble() < 0.6) {
				
				Enemy e = new Enemy(
						location.x + (starSize - psize) / 2 + (Math.sin(ang) * dist) + 32, 
						location.y + (starSize - psize) / 2 + (Math.cos(ang) * dist) + 32, 
						dist, ang, speed, new Point(location.x + starSize / 2, location.y + starSize / 2)
				);
				
				r.addEntity(e);
				e.layer = 3;
				e.setVisible(true);
				
			} else {
				
				p = new Planet(
						location.x + (starSize - psize) / 2 + (Math.sin(ang) * dist), 
						location.y + (starSize - psize) / 2 + (Math.cos(ang) * dist), 
						psize, c, dist, ang, speed);
				r.addEntity(p);
				p.layer = 1;
				p.setVisible(true);
				
			}
			
			p.setVisible(true);
			
		}
		
	}
	
	public static void generateRocks(Random rand, Region r, int planets) {
		
		for(int x = 0; x < planets; x++) {
			
			int size;
			do
				size = (int)Math.round(rand.nextGaussian() * 10 + 60);
			while(size < 20 || size > 100);
			Planet p = new Planet((r.x + rand.nextDouble()) * World.regionsize, (r.y + rand.nextDouble()) * World.regionsize, size, Color.gray);
			r.addEntity(p);
			p.setVisible(true);
	
		}
		
	}

}
