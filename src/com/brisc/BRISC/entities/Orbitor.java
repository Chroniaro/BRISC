package com.brisc.BRISC.entities;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.brisc.BRISC.BaconRidingIntelligentSpaceCats;
import com.brisc.BRISC.worldManager.World;

public abstract class Orbitor extends Entity {

	public double dist, ang, speed;
	
	public Orbitor(BufferedImage sprite, double x, double y, double dist, double ang, double speed) {
		
		super(sprite, x, y);
		this.dist = dist;
		this.ang = ang;
		this.speed = speed;
		
	}

	public static String getType() {
		
		return "orbitor";
		
	}

	@Override
	public void update(World w, Point location) {
		
		super.update(w, location);
		if(Math.abs(speed) > 0) {
			
			double ox = x - Math.sin(ang) * dist;
			double oy = y - Math.cos(ang) * dist;
			ang += BaconRidingIntelligentSpaceCats.UPDATE_SPEED / 5.0 * speed;
			x = ox + Math.sin(ang) * dist;
			y = oy + Math.cos(ang) * dist;
			
		}
		
	}
	
}
