/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.entities;

import java.awt.*;
import java.awt.image.*;
import java.util.HashMap;

import com.brisc.BRISC.worldManager.World;

/**
 *
 * @author Zac
 */
public abstract class Entity extends AbstractEntity {
    
    private boolean visible;
    private BufferedImage sprite;
    public double dx, dy;
    
    public Entity(BufferedImage sprite, double x, double y) {
        
        super(x, y);
        this.sprite = sprite;
        visible = false;
        dx = dy = 0;
        
    }
    
    public Entity(BufferedImage sprite) {
        
        this(sprite, 0, 0);
        
    }
    
    public Point[] perimeter() {
        
        return(new Point[] { 
            new Point((int)this.x, (int)this.y), 
            new Point((int)this.x + sprite.getWidth(), (int)this.y) ,
            new Point((int)this.x + sprite.getWidth(), (int)this.y + sprite.getHeight()),
            new Point((int)this.x, (int)this.y + sprite.getHeight())
        });
        
    }
    
    public boolean intersects(Point p) {
        
        Point[] perimeter = perimeter();
        int n= perimeter.length;
        int[] xps = new int[n];
        int[] yps = new int[n];
        
        for(int x = 0; x < n; x++) {
            
            xps[x] = (int)perimeter[x].getX();
            yps[x] = (int)perimeter[x].getY();
            
        }
        
        Polygon polyPerimeter = new Polygon(xps,yps,n);
        return(polyPerimeter.contains(p));
        
    }
    
    public boolean colliding(Entity e) {
                
        for(Point p:e.perimeter())
            if(this.intersects(p))
                return true;
        
        for(Point p:this.perimeter())
            if(e.intersects(p))
                return true;
        
        return false;
        
    }
    
    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
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
				"<dx>"+ String.valueOf(dx)+
				"<dy>"+ String.valueOf(dy)+
				"<visible>"+ String.valueOf(visible)
    				
    	;
    	
    }
    
    public static Entity load(HashMap<String,String> heading, HashMap<String, String>[] sections) {
    	
    	Entity e = new Entity(null) {};
    	e.addData(AbstractEntity.load(heading, sections));
    	e.dx = Double.parseDouble(sections[0].get("dx"));
    	e.dy = Double.parseDouble(sections[0].get("dy"));
    	e.visible  = Boolean.parseBoolean(sections[0].get("visible"));
    	
    	return e;
    	
    }
    
    public void addData(Entity e) {
    	
    	super.addData(e);
    	this.dx = e.dx;
    	this.dy = e.dy;
    	this.visible = e.visible;
    	
    }
   
    public static String getType() {
    	return "AbstractEntity";
    }
    
    @Override
	public void update(World w, Point location) {
		
    	x += dx;
    	y += dy;
		
	}
    
}