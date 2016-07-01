package com.brisc.BRISC.entities;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;

import com.brisc.BRISC.worldManager.World;

public class Enemy_Laser extends Laser {
	
	public Enemy_Laser(double x, double y, double dx, double dy, double mx, double my) {
		
		super(x, y, dx, dy, mx, my, Color.green);
		this.layer = 7;
		
	}
	
	@Override
	public void checkCollisions(World w) {
		
		Rectangle thisRectangle = new Rectangle((int)this.x, (int)this.y, (int)this.sx, (int)this.sy);
    	
    	for(Cat c : w.swarm) {
    		
    		Polygon box = c.getHitBox();
    		if(box.intersects(thisRectangle)) 
    			if(box.contains(this.x, this.y) || box.contains(this.x + sx, this.y + sy)) {
    			
	    			c.takeDamage(0.05);
	    			if(c.getHealth() <= 0) {
	    				
	    				c.die();
	    				w.swarm.remove(c);
	    				
	    			}
	    			
	    			this.setVisible(false);
	    			w.removeObject(this);
	    			
	    		}
    		
    	}
		
	}
	
}
