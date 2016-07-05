package com.brisc.BRISC.entities;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Line2D;

import com.brisc.BRISC.worldManager.World;

public class Enemy_Laser extends Laser {
	
	public Enemy_Laser(double x, double y, double dx, double dy, double mx, double my) {
		
		super(x, y, dx, dy, mx, my, Color.green);
		this.layer = 7;
		
	}
	
	@Override
	public void checkCollisions(World w) {
    	
		Object[] l = w.swarm.toArray();
		
		Line2D.Double thisLine = new Line2D.Double(this.x, this.y, this.x + this.sx, this.y + this.sy);
		
    	for(Object o : l) {
    		
    		Cat c = (Cat) o;
    		
    		Polygon box = c.getHitBox();
    		if(checkIntersect(thisLine, box)) {
    			
    			c.takeDamage(0.05);
    			w.removeObject(this);
    			
    		}
    		
    	}
		
	}
	
}
