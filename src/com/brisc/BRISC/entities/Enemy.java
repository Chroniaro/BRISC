package com.brisc.BRISC.entities;

import java.awt.Point;
import java.awt.Polygon;
import java.util.*;

import com.brisc.BRISC.states.Game;
import com.brisc.BRISC.worldManager.World;
import com.brisc.Resources.ResourceManager;

public class Enemy extends Orbitor implements Damageable {
	
	int phase;
	int preferredDist = 250;
	double health = 1;
	public static int enemiesOnCats = 0;
	int placeOnCat = -1;
	boolean onCat = false;
	final static int betweenDist = 75;
	final static Map<Integer, Boolean> takenSpots = new HashMap<>();
	Point homeSystem;
	int homeDist;
	final static int protectiveZone = 2000;
	double shotTime = 100;
	public Enemy_Laser laser;
	Random rand;
	
	public Enemy(double x, double y, double dist, double ang, double speed, Point homeSystem) {
		
		super(ResourceManager.getResource(ResourceManager.Resources.enemyBasic), x, y, dist, ang, speed);
		phase = 0;
		this.homeSystem = homeSystem;
		rand = new Random();
		
	}
	
	public static String getType() {
		
		return "enemy";
		
	}
	
	public void doAIStuff(double catX, double catY, World w) {
		
		final double dist = distanceFrom(catX, catY);
		final double homeDist = distanceFrom(homeSystem.x, homeSystem.y);
		final double angToCat = getAngleToPosition(catX, catY);
		double diff = angToCat + Math.PI * 1/2 - this.ang;
		while(diff > 2 * Math.PI)
			diff -= 2 * Math.PI;
		final double angOffset = Math.min(Math.abs(diff), Math.abs(2 * Math.PI + diff));
		
		if(phase == 0) {
			
			if((angOffset + 1) * dist < 800 || getHealth() < 1) {
				
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
			
			if(w.swarm.size() <= 0)
				phase = 2;
			
		}
		
		if(phase == 2) {
			
			speed = 3 / (homeDist + 2);
			
			this.dist = homeDist;
			this.ang = getAngleToPosition(homeSystem.x, homeSystem.y);
			
			double turn;
			
			turn = (homeDist - this.homeDist) * Math.PI / 10;
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
			
			if(w.swarm.size() > 0)
				if(homeDist < 0.95 * protectiveZone)
					if(dist < 600 && angOffset < Math.PI / 6) {
						
						phase = 1;
						
					}
			
		}
		
		if(onCat) {
			
			if(dist > preferredDist + ((placeOnCat + 1) * betweenDist) || phase != 1) {
				
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
			
			shoot(new Point((int)catX, (int)catY), w, 30 + (int)Math.round(Math.random() * 150), 20, 0.02);
			
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
	
	void shoot(World w, double dx, double dy, double damage) {
		
		Laser laser = new Enemy_Laser(x + getEye().x, y + getEye().y, dx * 30, dy * 30, dx * 8, dy * 8, damage);
		w.addObject(laser);
		
	}
	
	void shoot(Point exactTarget, World w, int time, int accuracy, double damage) {
		
		Point target = new Point(exactTarget.x + (int)(Math.random() * accuracy), exactTarget.y + (int)(Math.random() * accuracy));
		
		shotTime--;
		
		if(shotTime <= 0) {
			
			shotTime = time;
			Point from = new Point((int) (x + getEye().x), (int) (y + getEye().y));
			
			double ldx, ldy;
			ldx = (target.x - from.x) / target.distance(from);
			ldy = (target.y - from.y) / target.distance(from);
			
			shoot(w, ldx, ldy, damage);
			
		}
		
	}
	
	double distanceFrom(double x, double y) {
		
		double offsetX = this.x + getEye().x - x;
		double offsetY = this.y + getEye().y - y;
		
		return Math.sqrt(Math.pow(offsetX, 2) + Math.pow(offsetY, 2));
		
	}
	
	double getAngleToPosition(double x, double y) {
		
		double offsetX = this.x + getEye().x - x;
		double offsetY = this.y + getEye().y - y;
		
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
	public void die(Game g) {
		
		if(onCat) {
			
			onCat = false;
			takenSpots.put(placeOnCat, false);
			placeOnCat = -1;
			enemiesOnCats -= 1;
			
		}
		this.setVisible(false);
		
		g.catFood += 20 * Math.pow(rand.nextGaussian(), 2);
		g.catNip += 0.5 * Math.max(Math.floor(10 * Math.pow(rand.nextGaussian(), 2) - 2), 0);
		
	}
	
	Point getEye() {
		
		return new Point(32, 32);
		
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
