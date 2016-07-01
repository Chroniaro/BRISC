/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.entities;

import java.util.HashMap;
import com.brisc.BRISC.worldManager.*;

/**
 *
 * @author Zac
 */
public abstract class AbstractEntity {
    
    public double x, y;
    public double layer = 0;
    
    public static final String 
		WL_DATA          = "WL_DATA",
		WL_ENTITY        = "WL_ENTITY",
		WL_START_HEADING = "WL_START_HEADING",
		WL_END_HEADING   = "WL_END_HEADING",
		WL_START_REGION_DATA  = "WL_START_REGION",
		WL_END_REGION_DATA    = "WL_END_REGION";
    
    public AbstractEntity(double x, double y) {
        
        this.x = x;
        this.y = y;
        
    }
    
    public AbstractEntity() {
        
        this(0,0);
        
    }
    
    /**
     * 
     * <style type="text/css">
	 *	<!--
	 *	.tab { margin-left: 160px; }
	 *	-->
	 *	</style>
     * 
     * Every subclass should override this method.<p>
     * Overrides should be formatted like this:<p>
     * <pre>
     * 	WL_OPEN_ENTITY,
     * 
     *		WL_START_HEADING,
     *
     *			"&lttype&gt", getType(),
     *			</pre><p class="tab">(Additional info about specifically what type of object it is. Data should
     *			never contain the characters "<", or ">", except when used to mark a tag, and it should
     *			never contain a newline character or an "*". Tags may be more than one string, but will be parsed and 
     *			saved as a single string.)<pre>
     *			"<(tag)>", (data)
     *
     *		WL_END_HEADING,
     *
     *		WL_OPEN_SECTION,
     *			</pre><p class="tab">(Any additional data necessary to reconstruct the object. Data should
     *			never contain the characters "<", or ">", except when used to mark a tag, and it should
     *			never contain a newline character or an "*". Tags may be more than one string, but will be parsed and 
     *			saved as a single string.)<pre>
     *			"<(tag)>", (data)
     *
     *		WL_CLOSE_SECTION,
     *
     *	WL_CLOSE_ENTITY
     * </pre>
     * 
     * @return A String[] that will be saved to a file when the world is saved.
     */
    public String getSaveData() {
    	
    	return
    			
			WL_START_HEADING+
			
				"<type>"+ getType()+
		
			WL_END_HEADING+
		
			WL_DATA+
				
				"<x>"+ String.valueOf(x)+
				"<y>"+ String.valueOf(y)
				
		;
    	
    }
    
    /**
     * Every subclass should override this method. It is used to load objects from files.
     * 
     * @param heading A hashmap representing every tag and value saved in the heading.
     * @param sections An array of hashmaps, each representing a section. They are in
     * the order that the sections were defined.
     * @return An AbstractEntity (may need to be downcast) that should be, as long as
     * the load and getSaveData methods were properly implemented, identical to the
     * entity that was saved.
     */
    public static AbstractEntity load(HashMap<String,String> heading, HashMap<String, String>[] sections) {
    	
    	AbstractEntity e = new AbstractEntity() {};
    	
    	e.x = Double.parseDouble(sections[0].get("x"));
    	e.y = Double.parseDouble(sections[0].get("y"));
    	
    	return e;
    	
    }
    
    public void addData(AbstractEntity e) {
    	
    	this.x = e.x;
    	this.y = e.y;
    	
    }
    
    public static String getType() {
    	return "AbstractEntity";
    }
    
    public final World getWorld() {
    	
    	return null;
    	
    }
    
    public final Region getRegion() {
    	
    	return null;
    	
    }
    
}
