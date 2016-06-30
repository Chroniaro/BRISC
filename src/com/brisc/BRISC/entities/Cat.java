/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.entities;

import com.brisc.BRISC.states.Game;
import com.brisc.Resources.ResourceManager;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 *
 * @author Zac
 */

public class Cat extends Entity {
    
    Point eye;
    
    public double bob = 0;
    int shotTimer = 0;
    public double offSetX;
    public double offSetY;
    public int laserDelay = 80;
    public int type = 0;
    public static final int
    	TYPE_BLACK = 0,
    	TYPE_BROWN = 1,
    	TYPE_ORANGE = 2,
    	TYPE_PURPLE = 3,
    	TYPE_TABBY = 4;
    
    public Cat(int x, int y, int type) {

    	super(ResourceManager.getCat(type), x, y);
    	this.type = type;
        
    }
    
    public Laser laser(boolean mouseDown, Point mouseLocation, double[] motion) {
        
        if(mouseDown) shotTimer ++;
        else shotTimer = Math.min(laserDelay, shotTimer + 1);
        
        if(shotTimer > laserDelay) {
            
            shotTimer = 0;
            return getLaser(mouseLocation, motion);
            
        }
        
        return null;
    }
    
    public Laser getLaser(Point mouseLocation, double[] motion) {
        
        double lx, ly, ldx, ldy;
        					 
        if(mouseLocation.x > Game.centerOfMotion.x + offSetX) {
        	eye = new Point((int)offSetX + Game.centerOfMotion.x - (getSprite().getWidth()/2) + 46, 
    				(int)offSetY + Game.centerOfMotion.y - (getSprite().getHeight()/2) + (int)bob + 30);
        	ldx = (mouseLocation.getX() - eye.x) / mouseLocation.distance(eye);
            lx = getApparentLocationInSpace().x + 46;
        } else {
        	eye = new Point((int)offSetX + Game.centerOfMotion.x - (getSprite().getWidth()/2) + 18, 
					(int)offSetY + Game.centerOfMotion.y - (getSprite().getHeight()/2) + (int)bob + 30);
        	ldx = (mouseLocation.getX() - eye.x) / mouseLocation.distance(eye);
            lx = getApparentLocationInSpace().x + 16;
        }
        
        ldy = (mouseLocation.getY() - eye.y) / mouseLocation.distance(eye);
        ly = getApparentLocationInSpace().y + 30;
        
        if(!((mouseLocation.x > Game.centerOfMotion.x + offSetX + 16) || 
        		(mouseLocation.x <= Game.centerOfMotion.x + offSetX && mouseLocation.x > Game.centerOfMotion.x + offSetX - 14)))
            lx += ldx * 30;
        
        if(mouseLocation.y <= eye.y)  ly += ldy * 30;
        
        return new Laser(lx, ly, ldx * 30, ldy * 30, motion[0] + ldx * 12, motion[1] + ldy * 12);
        
    }
    
    public Point getApparentLocationInSpace() {
        
        double lx,ly;
        lx = offSetX + x - (getSprite().getWidth()/2);
        ly = offSetY + y - (getSprite().getHeight()/2) + bob;
            
        return new Point((int)lx, (int)ly);
        
    }

	public static String getType() {
		return "cat";
	}
    
	@Override
    public String getSaveData() {
		
		return
    			
			WL_START_HEADING+
			
				"<type>"+ getType()+
				"<cat type>"+ String.valueOf(type)+
		
			WL_END_HEADING+
		
			WL_DATA+
				
				"<x>"+ String.valueOf(x)+
				"<y>"+ String.valueOf(y)+
				"<dx>"+ String.valueOf(dx)+
				"<dy>"+ String.valueOf(dy)+
				"<visible>"+ String.valueOf(isVisible())+
				"<offset x>"+ String.valueOf(offSetX)+
				"<offset y>"+ String.valueOf(offSetY)+
				"<delay>"+ String.valueOf(laserDelay)
    				
    	;
    	
    }
    
    public static Cat load(HashMap<String,String> heading, HashMap<String, String>[] sections) {
    	
    	Cat e = new Cat(0,0,Integer.parseInt(heading.get("cat type")));
    	e.addData(Entity.load(heading, sections));
    	e.offSetX = Double.parseDouble(sections[0].get("offset x"));
    	e.offSetY = Double.parseDouble(sections[0].get("offset y"));
    	e.shotTimer = Integer.parseInt(sections[0].get("delay"));
    	return e;
    	
    }
	
}
