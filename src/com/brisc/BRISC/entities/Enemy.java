package com.brisc.BRISC.entities;

import java.awt.Point;
import java.awt.Polygon;
import java.util.*;

import com.brisc.BRISC.worldManager.World;
import com.brisc.Resources.ResourceManager;

public class Enemy extends Orbitor implements Damageable {
	
	int phase;
	int preferredDist = 150;
	double health = 1;
	public static int enemiesOnCats = 0;
	int placeOnCat = -1;
	boolean onCat = false;
	final static int betweenDist = 100;
	final static Map<Integer, Boolean> takenSpots = new HashMap<>();
	Point homeSystem;
	final static int protectiveZone = 2000;
	double shotTime = 100;
	public Enemy_Laser laser;

	public Enemy(double x, double y, double dist, double ang, double speed, Point homeSystem) {
		
		super(ResourceManager.getResource(ResourceManager.Resources.enemyBasic), x, y, dist, ang, speed);
		phase = 0;
		this.homeSystem = homeSystem;
		
	}
	
	public static String getType() {
		
		return "enemy";
		
	}
	
	public void doAIStuff(double catX, double catY, World w) {
		
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
			
			if(dist > preferredDist + ((enemiesOnCats + 1) * betweenDist) || phase != 1) {
				
				onCat = false;
				takenSpots.put(placeOnCat, false);
				placeOnCat = -1;
				enemiesOnCats -= 1;
				
			}
			
			if(placeOnCat > 0)
				if(!(takenSpots.containsKey(placeOnCat - 1) && takenSpots.get(placeOnCat - 1)) && phase == 1) {
					
					takenSpots.put(placeOnCat, false);
					takenSpots.put(placeOnCat - 1, true);
					placeOnCat -= 1;
					
				}
			
			shoot(new Point((int)catX, (int)catY), w);
			
		} else {
			
			shotTime = Math.min(100, shotTime + 0.5);
			
			if(dist < preferredDist + ((enemiesOnCats + 1) * betweenDist)) {
				
				onCat = true;
				placeOnCat = enemiesOnCats;
				takenSpots.put(placeOnCat, true);
				enemiesOnCats += 1;
				
			}
			
		}
		
	}
	
	void shoot(Point exactTarget, World w) {
		
		Point target = new Point(exactTarget.x + (int)(Math.random() * 100), exactTarget.y + (int)(Math.random() * 100));
		
		shotTime--;
		
		if(shotTime <= 0) {
			
			shotTime = 20 + (int)Math.round(Math.random() * 100);
			
			double ldx, ldy;
			Point eye = new Point((int) (this.x + 20), (int) (this.y + 20));
			ldx = (target.x - eye.x) / target.distance(eye);
			ldy = (target.y - eye.y) / target.distance(eye);
			
			Laser laser = new Enemy_Laser(eye.x, eye.y, ldx * 30, ldy * 30, ldx * 12, ldy * 12);
			w.addObject(laser);
			
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

	@Override
	public void takeDamage(double amount) {
		
		health -= amount;
		
	}

	@Override
	public void heal(double amount) {
		
		health += amount;
		
	}

	@Override
	public double getHealth() {
		
		return health;
		
	}

	@Override
	public void die() {
		
		if(onCat) {
			
			onCat = false;
			takenSpots.put(placeOnCat, false);
			placeOnCat = -1;
			enemiesOnCats -= 1;
			
		}
		this.setVisible(false);
		
	}

	@Override
	public Polygon getHitBox() {
		
		Polygon p = new Polygon();
		p.addPoint((int)this.x, (int)this.y);
		p.addPoint((int)this.x + 64, (int)this.y);
		p.addPoint((int)this.x + 64, (int)this.y + 64);
		p.addPoint((int)this.x, (int)this.y + 64);
		
		return p;
		
	}

	@Override
	public void update(World w, Point location) {
		
		super.update(w, location);
		doAIStuff(location.x, location.y, w);
		
	}

}
