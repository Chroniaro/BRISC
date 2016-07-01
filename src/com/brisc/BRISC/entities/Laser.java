/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.brisc.BRISC.worldManager.World;

import javafx.scene.shape.Line;

/**
 *
 * @author Zac
 */
public class Laser extends Entity {
    
	double sx, sy;
	
    public Laser(double x, double y, double dx, double dy, double mx, double my, Color color) {
        
        super(getImage((int)dx, (int)dy, color) , x, y);
        this.dx = mx;
        this.dy = my;
        this.setVisible(true);
        this.sx = dx;
        this.sy = dy;
        this.layer = 8;
        
    }
    
    public Laser(double x, double y, double dx, double dy, double mx, double my) {
    	
    	this(x, y, dx, dy, mx, my, Color.red);
    	
    }
    
    static BufferedImage getImage(int dx, int dy, Color laserColor) {
        
        BufferedImage img = new BufferedImage(Math.abs(dx) + 5, Math.abs(dy) + 5, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = img.createGraphics();
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(laserColor);
        int x1, x2, y1, y2;
        if(dx > 0) {
            x1 = 0;
            x2 = dx;
        } else {
            x1 = -dx;
            x2 = 0;
        }
        if(dy > 0) {
            y1 = 0;
            y2 = dy;
        } else {
            y1 = -dy;
            y2 = 0;
        }
        g2d.drawLine(x1 + 3, y1 + 3, x2, y2);
        return img;
        
    }
    
    public static String getType() {
    	return "laser";
    }
    
    @Override
    public String getSaveData() {
    	
    	return
    			
			WL_START_HEADING+
			
				"<type>"+ getType()+
		
			WL_END_HEADING+
		
			WL_DATA+
				
				"<x>"+ String.valueOf(x)+
				"<y>"+ String.valueOf(y)+
				"<mx>"+ String.valueOf(dx)+
				"<my>"+ String.valueOf(dy)+
				"<dx>"+ String.valueOf(sx)+
				"<dy>"+ String.valueOf(sy)+
				"<visible>"+ String.valueOf(isVisible())
    				
    	;
    	
    }
    
    public static Laser load(HashMap<String,String> heading, HashMap<String, String>[] sections) {
    	
    	Laser e = new Laser(Double.parseDouble(sections[0].get("x")),Double.parseDouble(sections[0].get("y")),
    			Double.parseDouble(sections[0].get("dx")),Double.parseDouble(sections[0].get("dy")),
    			Double.parseDouble(sections[0].get("mx")),Double.parseDouble(sections[0].get("my")));
    	
    	e.setVisible(Boolean.parseBoolean(sections[0].get("visible")));
    	
    	return e;
    	
    }
    
    public void checkCollisions(World w) {
    	
    	Rectangle thisRectangle = new Rectangle((int)this.x, (int)this.y, (int)this.sx, (int)this.sy);
    	
    	for(Damageable d : w.getAllEntities(Damageable.class)) {
    		
    		Polygon box = d.getHitBox();
    		if(box.intersects(thisRectangle)) 
    			if(box.contains(this.x, this.y) || box.contains(this.x + sx, this.y + sy)) {
    			
	    			d.takeDamage(0.2);
	    			this.setVisible(false);
	    			w.removeObject(this);
	    			
	    		}
    		
    	}
    	
    }
    
}
