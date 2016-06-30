package com.brisc.BRISC.entities;

import java.awt.image.BufferedImage;

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

}
