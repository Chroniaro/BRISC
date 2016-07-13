package com.brisc.BRISC.entities;

import java.awt.Point;
import java.awt.Polygon;

import com.brisc.BRISC.states.Game;
import com.brisc.BRISC.worldManager.World;
import com.brisc.Resources.ResourceManager;

public class EnemyBig extends Enemy {
	
	final static Point eye = new Point(64, 64);
	int spaTime = 0;
	int checkTime = 0;

	public EnemyBig(double x, double y, double dist, double ang, double speed, Point homeSystem) {
		
		super(x, y, dist, ang, speed, homeSystem);
		setSprite(ResourceManager.getResource(ResourceManager.Resources.enemyBig));
		
	}
	
	@Override
	public void doAIStuff(double catX, double catY, World w) {
		
		final double dist = distanceFrom(catX, catY);
		final double homeDist = distanceFrom(homeSystem.x, homeSystem.y);
		final double angToCat = getAngleToPosition(catX, catY);
		double diff = angToCat + Math.PI * 1/2 - this.ang;
		while(diff > 2 * Math.PI)
			diff -= 2 * Math.PI;
		final double angOffset = Math.min(Math.abs(diff), Math.abs(2 * Math.PI + diff));
		
		if(phase == 0) {
			
			if((angOffset + 1) * dist < 1200 || getHealth() < 1) {
				
				phase = 1;
				
			}
			
		}
		
		if(phase == 1) {
			
			speed = 1 / (dist + 2);
			if(placeOnCat % 2 == 1)
				speed = -speed;
			
			this.dist = dist;
			this.ang = angToCat;
			
			double turn;
			
			if(onCat)
				turn = (dist - (preferredDist + (placeOnCat + 1) * betweenDist)) * Math.PI / 20;
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
			
			spaTime += Math.floor(1 / getHealth());
			checkTime += Math.floor(1 / getHealth());
			
			if(Math.abs(dist - (preferredDist + (placeOnCat + 1) * betweenDist)) < 50) {
				
				if(Math.min(checkTime, spaTime) >= 200) {
					
					checkTime = 0;
					
					if(Math.random() >  1/(Math.pow(2, (spaTime * spaTime / 2000000)))) {
						
						phase = 3;
						
					}
					
					if(Math.random() >  1/(Math.pow(2, (spaTime * spaTime / 3000000)))) {
						
						spaTime = 0;
						phase = 5;
						
					}
					
				}
			}
			
		}
		
		if(phase == 2) {
			
			speed = 1 / (homeDist + 2);
			
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
			
			if(w.swarm.size() > 0)
				if(homeDist < 0.95 * protectiveZone)
					if(dist < 600 && angOffset < Math.PI / 6) {
						
						phase = 1;
						
					}
			
		}
		
		if(phase == 3) {
			
			this.dist = dist;
			this.ang = angToCat;
			speed = 1.5 / (Math.pow(dist, 0.8) + 2);
			
			if(speed >= 0)
				this.ang -= Math.PI / 2;
			else
				this.ang += Math.PI / 2;
			
			if(dist > preferredDist + (placeOnCat + 10) * betweenDist) {
				
				phase = 4;
				spaTime = -1000;
				
			}
			
		}
		
		if(phase == 4) {
			
			final int shots = 5;
			
			this.dist = dist;
			this.ang = angToCat;
			speed = 1 / (Math.pow(dist, 0.6) + 2);
			
			if(speed >= 0)
				this.ang += Math.PI / 2;
			else
				this.ang -= Math.PI / 2;
			
			if(dist < preferredDist / 2) {
				
				for(int i = 0; i < shots; i++) {
					
					double ang = 2 * Math.PI * i / shots;
					shoot(w, Math.sin(ang), Math.cos(ang), 0.1);
					
				}
				
				phase = 1;
				
			}
			
			if(dist > preferredDist + (placeOnCat + 20) * betweenDist) {
				
				phase = 1;
				
			}
			
		}
		
		if(phase == 5) {
			
			spaTime++;
			
			if(spaTime >= 800 || dist > preferredDist + (placeOnCat + 5) * betweenDist) {
				
				phase = 1;
				spaTime = -1000;
				
			} else {
				
				speed = 0.05;
				this.dist = 0;
				if(spaTime % 5 == 0)
					shoot(w, Math.sin(this.ang + Math.PI / 2), Math.cos(this.ang + Math.PI / 2), 0.2);
				
			}
			
		}
		
		if(onCat) {
			
			if((dist > preferredDist + ((placeOnCat + 2) * betweenDist)) || phase != 1) {
				
				onCat = false;
				takenSpots.put(placeOnCat, false);
				takenSpots.put(placeOnCat + 1, false);
				takenSpots.put(placeOnCat + 2, false);
				placeOnCat = -1;
				enemiesOnCats -= 3;
				
			} else if(Math.abs(dist - (preferredDist + (placeOnCat + 1) * betweenDist)) < 100)
				shoot(new Point((int)catX, (int)catY), w, 20 + (int)Math.round(Math.random() * 100), 10, 0.08);
			
			if(placeOnCat > 0)
				if(!(takenSpots.containsKey(placeOnCat - 1) && takenSpots.get(placeOnCat - 1)) && phase == 1) {
					
					takenSpots.put(placeOnCat + 2, false);
					takenSpots.put(placeOnCat - 1, true);
					placeOnCat -= 1;
					
				}
			
		} else {
			
			shotTime = Math.min(100, shotTime + 0.5);
			
			if(phase == 1)
				if(dist < preferredDist + ((enemiesOnCats + 1) * betweenDist)) {
					
					onCat = true;
					placeOnCat = enemiesOnCats;
					takenSpots.put(placeOnCat, true);
					takenSpots.put(placeOnCat + 1, true);
					takenSpots.put(placeOnCat + 2, true);
					enemiesOnCats += 3;
					
				}
			
		}
		
	}
	
	@Override
	public void takeDamage(double amount) {
		
		super.takeDamage(amount * 0.2);
		
	}
	
	@Override
	public Polygon getHitBox() {
		
		Polygon p = new Polygon();
		p.addPoint((int)this.x, (int)this.y);
		p.addPoint((int)this.x + 128, (int)this.y);
		p.addPoint((int)this.x + 128, (int)this.y + 128);
		p.addPoint((int)this.x, (int)this.y + 128);
		
		return p;
		
	}
	
	@Override
	public void die(Game g) {
		
		if(onCat) {
			
			onCat = false;
			takenSpots.put(placeOnCat, false);
			takenSpots.put(placeOnCat + 1, false);
			takenSpots.put(placeOnCat + 2, false);
			placeOnCat = -1;
			enemiesOnCats -= 3;
			
		}
		this.setVisible(false);
		
		g.catFood += 20 * Math.pow(rand.nextGaussian(), 2);
		g.catNip += 20 * Math.pow(rand.nextGaussian(), 2);
		
	}
	
	@Override
	Point getEye() {
		
		return new Point(64, 64);
		
	}

}
