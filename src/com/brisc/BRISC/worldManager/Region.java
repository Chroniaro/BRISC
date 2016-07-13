/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.worldManager;

import java.awt.Point;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.brisc.BRISC.entities.*;

/**
 *
 * @author Zac
 */
public class Region {
	
	String id;
	World world;
	List<AbstractEntity> entities;
	int x, y;
	
	public Region(World world, String id, int x, int y) {
		
		this.id = id;
		this.world = world;
		this.x = x;
		this.y = y;
		entities = Collections.synchronizedList(new ArrayList<>());
		
	}
	
    public void loadFromFile(File f) {
        
    	if(!f.exists()) throw new IOError(new Throwable("File not found: " + f.getPath()));
    	if(!f.isFile()) throw new IOError(new Throwable("Path not file: " + f.getPath()));
    	if(!f.canRead()) throw new IOError(new Throwable("Can't read file: " + f.getPath()));
    	
    	try {
    		
			FileReader fr = new FileReader(f);
			BufferedReader reader = new BufferedReader(fr);
			String line = "";
			while(true) {
				
				line = reader.readLine();
				if(line == null) {
					
					reader.close();
					
					break;
					
				}
				
				if(line.startsWith(id)) {
					
					reader.close();
					
					parse(line);
					
					break;
					
				}
				
			}
			
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
        
    }
    
    public void parse(String line) {
		
		//Parse region data
		String regionData = line.substring(line.indexOf(Entity.WL_START_REGION_DATA) + Entity.WL_START_REGION_DATA.length(), line.lastIndexOf(Entity.WL_END_REGION_DATA));
		HashMap<String, String> rDataMap = new HashMap<>();
		for(String rdata : regionData.split("<")) {
			
			if(rdata.matches("")) continue;
			rDataMap.put(rdata.split(">")[0], rdata.split(">")[1]);
			
		}
		
		build(rDataMap);
		
		//Parse entities
		String[] entities = line.split(Entity.WL_ENTITY);
		for(int x = 0; x < entities.length; x++) {
			
			if(!(entities[x].contains(Entity.WL_START_HEADING) && entities[x].contains(Entity.WL_END_HEADING))) continue;
			
			System.out.println();
			
			//Parse header
			String heading = entities[x].substring(entities[x].indexOf(Entity.WL_START_HEADING) + Entity.WL_START_HEADING.length(), 
					entities[x].lastIndexOf(Entity.WL_END_HEADING));
			String[] keys = heading.split("<");
			HashMap<String, String> header = new HashMap<>();
			for(String s:keys) {
				if(s.trim().matches("")) continue;
				header.put(s.split(">")[0], s.split(">")[1]);
			}
			
			//Parse data sections
			String sectstrs[] = entities[x].split(Entity.WL_DATA);
			String[] tmp = new String[sectstrs.length - 1];
			@SuppressWarnings("unchecked")
			HashMap<String, String>[] sections = new HashMap[tmp.length];
			for(int s = 0; s < tmp.length; s++) {
				tmp[s] = sectstrs[s + 1];
				keys = tmp[s].split("<");
				sections[s] = new HashMap<String, String>();
				for(String str:keys) {
					if(str.trim().matches("")) continue;
					sections[s].put(str.split(">")[0], str.split(">")[1]);
				}
			}
			
			Class<? extends AbstractEntity> c = world.typeClasses.get(header.get("type"));
			
			try {
				
				Object o = c.getMethod("load", new Class<?>[] {HashMap.class, HashMap.class})
						.invoke(null, new Object[] {heading, sections});
				
				world.addObject((AbstractEntity)c.cast(o));
				
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
		}
		
	
    	
    }
    
    public void build(HashMap<String, String> dataMap) {
    	
    	throw new UnsupportedOperationException("Region doesn't build from file yet.");
    	
    }
    
    public void saveToFile(File f) {
    	
    	if(!f.exists()) throw new IOError(new Throwable("File not found: " + f.getPath()));
    	if(!f.isFile()) throw new IOError(new Throwable("Path not file: " + f.getPath()));
    	if(!f.canRead()) throw new IOError(new Throwable("Can't read file: " + f.getPath()));
    	if(!f.canWrite()) throw new IOError(new Throwable("Can't write to file: " + f.getPath()));
        
        String s = 
        		
        		this.id+
        		Entity.WL_START_REGION_DATA+
        			
        			"<x>"+String.valueOf(x)+
        			"<y>"+String.valueOf(y)+
        		
        		Entity.WL_END_REGION_DATA
        ;
        
        synchronized(entities) {
        	
	        for(AbstractEntity e : entities) {
	        	
	        	s += Entity.WL_ENTITY + e.getSaveData().trim();
	        	
	        }
        }
        
        try {
        	
			FileReader fr = new FileReader(f);
			BufferedReader reader = new BufferedReader(fr);
			String data = "";
			String line = "";
			
			while(true) {
				
				line = reader.readLine();
				
				if(line == null) {
					
					reader.close();
					
					break;
					
				} else {
					
					data += line + "\n";
					
				}
				
				if(line.startsWith(id)) {
					
					reader.close();
					
					break;
					
				}
				
			}
			
			if(data.contains(this.id)) {
				
				String[] spldata = data.split(this.id);
				spldata[1] = spldata[1].substring(spldata[1].indexOf("\n"), spldata[1].length()-1);
				data = spldata[0]+s+"\n"+spldata[1];
				
			} else {
				
				data += s + "\n";
				
			}
			
			FileWriter fw = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(fw);
			writer.write(data);
			writer.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
        
    }
    
    public void updateLazy() {
        
    	synchronized(this.entities) {
        	
        	List<AbstractEntity> toTransfer = new ArrayList<>();
        	Iterator<AbstractEntity> i = entities.iterator();
	        while(i.hasNext()) {
				AbstractEntity e = i.next();
				if(e.x < this.x || e.x > this.x + World.regionsize || e.y < this.y || e.y > this.y + World.regionsize)
					toTransfer.add(e);
			}
	        for(AbstractEntity n : toTransfer) {
	        	
	        	if(entities.contains(n))
	        		transferEntity(n);
	        	
	        }
	        
        }
    	
    }
    
    public void update(double catX, double catY) {
        
        updateLazy();
        Object[] entities = this.entities.toArray();
        for(Object e : entities) {
    		((AbstractEntity) e).update(this.world, new Point((int)catX, (int)catY));
    	}
        	
        
    }

	public void addEntity(AbstractEntity e) {
		
		this.entities.add(e);
		
	}
	
	public void transferEntity(AbstractEntity e) {
		
		removeEntity(e);
		world.addObject(e);
		
	}

	public void removeEntity(AbstractEntity e) {
		
		this.entities.remove(e);
		
	}
    
}
