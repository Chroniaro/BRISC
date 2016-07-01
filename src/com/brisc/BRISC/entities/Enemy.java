package com.brisc.BRISC.entities;

import java.awt.Point;
import java.util.*;

import com.brisc.Resources.ResourceManager;

public class Enemy extends Orbitor {
	
	int phase;
	int preferredDist = 150;
	public static int enemiesOnCats = 0;
	int placeOnCat = -1;
	boolean onCat = false;
	final static int betweenDist = 100;
	final static Map<Integer, Boolean> takenSpots = new HashMap<>();
	Point homeSystem;
	final static int protectiveZone = 2000;

	public Enemy(double x, double y, double dist, double ang, double speed, Point homeSystem) {
		
		super(ResourceManager.getResource(ResourceManager.Resources.enemyBasic), x, y, dist, ang, speed);
		phase = 0;
		this.homeSystem = homeSystem;
		
	}
	
	public static String getType() {
		
		return "enemy";
		
	}
	
	public void doAIStuff(double catX, double catY) {
		
		final double dist = distanceFrom(catX, catY);
		final double homeDist = distanceFrom(homeSystem.x, homeSystem.y);
		final double angToCat = getAngleToPosition(catX, catY);
		final double diff = Math.abs(this.ang - angToCat);
		final double angOffset = Math.min(diff, 2 * Math.PI - diff);
		
		if(phase == 0) {
			
			if(dist < 500 && angOffset < Math.PI / 8) {
				
				phase = 1;
				
			}
			
		}
		
		if(phase == 1) {
			
			speed = 3 / (dist + 2);
			if(placeOnCat % 2 == 1)
				speed = -speed;
			
			this.dist = dist;
			this.ang = angToCat;
			
			double turn;
			
			if(onCat)
				turn = (dist - (preferredDist + placeOnCat * betweenDist)) * Math.PI / 20;
			else
				turn = (dist - preferredDist) * Math.PI / 20;
			if(turn > Math.PI / 2)
				turn = Math.PI / 2;
			if(turn < -Math.PI / 2)
				turn = -Math.PI / 2;
			
			if(speed >= 0)
				this.ang += turn;
			else
				this.ang -= turn;
			
			if(dist > protectiveZone || (homeDist > protectiveZone && dist > preferredDist / 2)) {
				
				phase = 2;
				
			}
			
		}
		
		if(phase == 2) {
			
			speed = 3 / (homeDist + 2);
			if(placeOnCat % 2 == 1)
				speed = -speed;
			
			this.dist = homeDist;
			this.ang = getAngleToPosition(homeSystem.x, homeSystem.y);
			
			double turn;
			
			turn = (homeDist - 2 * preferredDist) * Math.PI / 10;
			if(turn > Math.PI / 2)
				turn = Math.PI / 2;
			if(turn < -Math.PI / 2)
				turn = -Math.PI / 2;
			
			if(speed >= 0)
				this.ang += turn;
			else
				this.ang -= turn;
			
			if(dist <= preferredDist / 3)
				phase = 1;
			
			if(homeDist < 0.95 * protectiveZone)
				if(dist < 600 && angOffset < Math.PI / 6) {
					
					phase = 1;
					
				}
			
		}
		
		if(onCat) {
			
			if(dist > preferredDist + ((enemiesOnCats + 1) * betweenDist)) {
				
				onCat = false;
				takenSpots.put(placeOnCat, false);
				placeOnCat = -1;
				enemiesOnCats -= 1;
				
			}
			
			if(placeOnCat > 0)
				if(!(takenSpots.containsKey(placeOnCat - 1) && takenSpots.get(placeOnCat - 1))) {
					
					takenSpots.put(placeOnCat, false);
					takenSpots.put(placeOnCat - 1, true);
					placeOnCat -= 1;
					
				}
			
		} else {
			
			if(dist < preferredDist + ((enemiesOnCats + 1) * betweenDist)) {
				
				onCat = true;
				placeOnCat = enemiesOnCats;
				takenSpots.put(placeOnCat, true);
				enemiesOnCats += 1;
				
			}
			
		}
		
	}
	
	double distanceFrom(double x, double y) {
		
		double offsetX = this.x - x;
		double offsetY = this.y - y;
		
		return Math.sqrt(Math.pow(offsetX, 2) + Math.pow(offsetY, 2));
		
	}
	
	double getAngleToPosition(double x, double y) {
		
		double offsetX = this.x - x;
		double offsetY = this.y - y;
		
		if(offsetY == 0) {
			
			if(offsetX > 0) {
				
				return Math.PI / 2;
				
			} else if(offsetX < 0) {
				
				return -Math.PI / 2;
				
			} else {
				
				return this.ang;
				
			}
			
		}
		
		if(offsetY >= 0)
			return Math.atan(offsetX / offsetY);
		else
			return Math.PI + Math.atan(offsetX / offsetY);
		
	}

}
