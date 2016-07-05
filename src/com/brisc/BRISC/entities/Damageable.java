package com.brisc.BRISC.entities;

import java.awt.Polygon;

import com.brisc.BRISC.states.Game;

public interface Damageable extends EntityInterface {

	public void takeDamage(double amount);
	public void heal(double amount);
	public double getHealth();
	public void die(Game g);
	public Polygon getHitBox();
	
}
