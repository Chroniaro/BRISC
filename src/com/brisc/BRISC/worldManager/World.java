/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.worldManager;

import com.brisc.BRISC.entities.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 *
 * @author Zac
 */
public class World {
	
	public static final int regionsize = 1000;
    
	public final HashMap<String, Class<? extends AbstractEntity>> typeClasses;
	public final HashMap<Class<? extends AbstractEntity>, String> entityTypes;
    Map<String, Region> regions;
    Generator gen;
    Rectangle loadedArea;
    
	@SuppressWarnings("unchecked")
	public World(String location, Generator generator) {
        
    	typeClasses = new HashMap<>();
    	entityTypes = new HashMap<>();
    	
    	loadedArea = new Rectangle();
    	
    	gen = generator;
    	
    	for(Class<AbstractEntity> p : new Class[] {
    		AbstractEntity.class,
    		Entity.class,
    		Cat.class,
    		Laser.class,
    		Orbitor.class,
    		Planet.class,
    		Enemy.class
    	}) {
    	
	    	try {
	    		
	    		entityTypes.put(p, (String)(p.getDeclaredMethod("getType", new Class[0]).invoke(null, new Object[0])));
				typeClasses.put((String)(p.getDeclaredMethod("getType", new Class[0]).invoke(null, new Object[0])), p);
				
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				
				e.printStackTrace();
				
			}
    	}
    	
    	regions = new HashMap<>();
    	
    	System.out.println(typeClasses);
    	
    }
	
	public void update(double catX, double catY) {
		
		for(int x = loadedArea.x; x < loadedArea.x + loadedArea.width; x++)
        	for(int y = loadedArea.y; y < loadedArea.y + loadedArea.height; y++) {
        	
        		regions.get(id(x, y)).update(catX, catY);
        		
        	}
		
	}
    
    public List<AbstractEntity> getAllEntities() {
        
    	ArrayList<AbstractEntity> t = new ArrayList<>();
        for(int x = loadedArea.x; x < loadedArea.x + loadedArea.width; x++)
        	for(int y = loadedArea.y; y < loadedArea.y + loadedArea.height; y++) {
        		
        		load(new Point(x, y));
        		t.addAll(regions.get(id(x, y)).entities);
        		
        	}
        
        return t;
    	
    }
    
	public <T extends AbstractEntity> List<T> getAllEntities(Class<T> c) {
    	
    	ArrayList<T> t = new ArrayList<>();
    	
    	for(AbstractEntity e : getAllEntities()) {
    		
    		if(c.isAssignableFrom(e.getClass())) t.add(c.cast(e));
    		
    	}
    	
    	return t;
    	
    }
    
    public List<AbstractEntity> getAllEntities(String type) {
    	
    	return getAllEntities(entityTypes.get(type));
    	
    }
    
    public List<Cat> getAllCats() {
        
        return getAllEntities(Cat.class);
        
    }
    
    public List<Laser> getAllLasers() {
    	
    	return getAllEntities(Laser.class);
    	
    }
    
    public List<?> getAllPlanets() {
    	
    	return getAllEntities(Planet.class);
    	
    }
    
    public void addObject(AbstractEntity e) {
    	
    	load(region(e));
    	regions.get(id(e)).addEntity(e);
    	
    }
    
    public void generate(Point chunk) {
    	
    	Region r = new Region(this, id(chunk), chunk.x, chunk.y);
    	gen.generate(r);
		regions.put(id(chunk), r);
    	
    }
    
    public void generate(Rectangle chunkArea) {
    	
    	for(int x = (int)chunkArea.getX(); x < chunkArea.x + chunkArea.getWidth(); x++)
    		for(int y = (int)chunkArea.getY(); y < chunkArea.y + chunkArea.getHeight(); y++) {
    			
    			generate(new Point(x, y));
    			
    		}
    	
    }
    
    public void load(Point chunk) {
    	
    	if(!regions.containsKey(id(chunk))) {
    		
    		generate(chunk);
    		
    	}
    	
    }
    
    public void setLoadedChunks(Rectangle r) {
    	
    	for(int x = r.x; x < r.x + r.getWidth(); x++)
    		for(int y = r.y; y < r.y + r.getHeight(); y++) {
    			
    			load(new Point(x, y));
    			
    		}
    	
    	this.loadedArea = r;
    	
    }
    
    public String getSaveLocation() {
    	
    	throw new UnsupportedOperationException("Doesn't save or load regions yet.");
    	
    }
    
    public static <A, B> String id(A x, B y) {
    	
    	return "(" + x.toString() + "," + y.toString() + ")";
    	
    }
    
    public static String id(AbstractEntity e) {
    	
    	return id(region(e));
    	
    }
    
    public static String id(Point p) {
    	
    	return id(p.x, p.y);
    	
    }
    
    public static Point region(double x, double y) {
    	
    	Point p = new Point();
    	double newX, newY;
    	newX = x / regionsize;
    	newY = y / regionsize;
    	
    	if(x < 0)
    		p.x = (int)newX - 1;
    	else
    		p.x = (int)newX;
    	
    	if(y < 0)
    		p.y = (int)newY - 1;
    	else
    		p.y = (int)newY;
    	
    	return p;
    	
    }
    
    public static Point region(AbstractEntity e) {
    	
    	return region(e.x, e.y);
    	
    }

}
