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
		
		double difficulty = (r.x * r.x) + (r.y * r.y);
		
		generatePlanets(rand, r, seed, difficulty);		
		
	}
	
	public static void generatePlanets(Random rand, Region r, String seed, double difficulty) {
		
		double planets = getPlanetNumber(rand);
		if(planets >= 1) {
			
			if(shouldGenSystem(rand, r, seed, planets))
				generateSolarSystem(rand, r, (int)StrictMath.round(planets) + 1, difficulty);
			else
				generateRocks(rand, r, (int)StrictMath.round(planets));
				
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
	
	public static void generateSolarSystem(Random rand, Region r, int planets, double difficulty) {
		
		int starSize;
		do
			starSize = (int)StrictMath.round(rand.nextGaussian() * 50 + 400);
		while(starSize < 200 || starSize > 600);
		Point location = new Point((int)((r.x + rand.nextDouble()) * World.regionsize), 
				(int)((r.y + rand.nextDouble()) * World.regionsize));
		
		Planet p;
		double previousDist;
		
		if(rand.nextDouble() < 0.02) {
			
			starSize *= 2;
			planets *= 3;
			p = new Planet(location.x, location.y, starSize, new Color(200, 20, 160));
			previousDist = starSize / 2 + 200;
			
		} else {
			
			p = new Planet(location.x, location.y, starSize, Color.orange);
			previousDist = starSize / 2;
			
		}
		
		r.addEntity(p);
		p.setVisible(true);
		
		for(int x = 0; x < planets; x++) {
			
			int psize;
			
			do
				psize = (int)StrictMath.round(rand.nextGaussian() * 10 + 120);
			while(psize < 90 || psize > 150);
			
			double ang = ((x + rand.nextDouble()) * Math.PI * 2 / planets);
			double dist = previousDist + psize / 2 + rand.nextDouble() * 10;
			
			previousDist = dist + psize / 2;
			
			double speed =  (0.02 + (rand.nextDouble() - 0.5) * 0.04 + 0.005 * rand.nextGaussian()) / Math.sqrt((dist * dist + 1) / 20000);
			
			Color c = Color.blue;
			if(rand.nextDouble() < 0.3)
				c = Color.red.darker().darker();
			if(rand.nextDouble() < 0.02)
				c = new Color(.15f, 0f, .5f);
			
			if(rand.nextDouble() < bossChance(difficulty)) {
				
				EnemyBig e = new EnemyBig(
						location.x + (starSize - psize) / 2 + (Math.sin(ang + Math.PI) * dist), 
						location.y + (starSize - psize) / 2 + (Math.cos(ang + Math.PI) * dist), 
						dist, ang + Math.PI, speed, new Point(location.x + starSize / 2, location.y + starSize / 2)
				);
				
				r.addEntity(e);
				e.layer = 3;
				e.setVisible(true);
				
			} else if(rand.nextDouble() < 0.4) {
				
				Enemy e = new Enemy(
						location.x + (starSize - psize) / 2 + (Math.sin(ang + Math.PI) * dist) + 32, 
						location.y + (starSize - psize) / 2 + (Math.cos(ang + Math.PI) * dist) + 32, 
						dist, ang + Math.PI, speed, new Point(location.x + starSize / 2, location.y + starSize / 2)
				);
				
				r.addEntity(e);
				e.layer = 3;
				e.setVisible(true);
				
			}
				
			p = new Planet(
					location.x + (starSize - psize) / 2 + (Math.sin(ang) * dist), 
					location.y + (starSize - psize) / 2 + (Math.cos(ang) * dist), 
					psize, c, dist, ang, speed);
			r.addEntity(p);
			p.layer = 1;
			p.setVisible(true);
			
			p.setVisible(true);
			
		}
		
	}
	
	static double bossChance(double diff) {
		
		final double[][] logistics = new double[][] {
			{1.0, 0.05, 100.0},
			{3.0, 0.01, 1000.0},
			{5.0, 0.002, 8000.0}
		};
		
		double chance = 1.0;
		for(double[] values : logistics) {
			
			chance += values[0] / (1.0 + StrictMath.pow(StrictMath.E, -values[1] * (diff - values[2])));
			
		}
		
		return 0.05 * chance;
		
	}
	
	static double normChance(double diff) {
		
		return 0.1;
		
	}
	
	public static void generateRocks(Random rand, Region r, int planets) {
		
		for(int x = 0; x < planets; x++) {
			
			int size;
			do
				size = (int)StrictMath.round(rand.nextGaussian() * 10 + 60);
			while(size < 20 || size > 100);
			Planet p = new Planet((r.x + rand.nextDouble()) * World.regionsize, (r.y + rand.nextDouble()) * World.regionsize, size, Color.gray);
			r.addEntity(p);
			p.setVisible(true);
	
		}
		
	}

}
