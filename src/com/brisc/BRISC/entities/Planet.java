/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 *
 * @author Zac
 */
public class Planet extends Orbitor {
    
	double size;
	Color color;
    
    public Planet(double x, double y, double size, Color color, double dist, double currentAng, double speed) {
    	
    	super(getImage(size, color), x, y, dist, currentAng, speed);
    	
    }
    
    public Planet(double x, double y, double size, Color color) {
        
    	this(x, y, size, color, 0, 0, 0);
    	
    }
    
    static BufferedImage getImage(double size, Color color) {
        
        BufferedImage img = new BufferedImage((int)size, (int)size, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillOval(0, 0, (int)size, (int)size);
        return img;
        
    }
    
    public static String getType() {
    	return "planet";
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
				"<size>"+ String.valueOf(size)+
				"<visible>"+ String.valueOf(isVisible())+
				"<color>"+ color.getRGB()+
				"<speed>"+ speed+
				"<dist>"+ dist+
				"<angle>" + ang
    				
    	;
    	
    }
    
    public static Planet load(HashMap<String,String> heading, HashMap<String, String>[] sections) {
    	
    	Planet e = new Planet(Double.parseDouble(sections[0].get("x")),Double.parseDouble(sections[0].get("y")),
    			Double.parseDouble(sections[0].get("size")), new Color(Integer.parseInt(sections[0].get("color"))),
    			Double.parseDouble(sections[0].get("speed")), Double.parseDouble(sections[0].get("dist")),
    			Double.parseDouble(sections[0].get("angle")));
    	
    	e.setVisible(Boolean.parseBoolean(sections[0].get("visible")));
    	
    	return e;
    	
    }
    
}
