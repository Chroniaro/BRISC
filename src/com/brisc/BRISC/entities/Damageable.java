package com.brisc.BRISC.entities;

import java.awt.Polygon;

public interface Damageable extends EntityInterface {

	public void takeDamage(double amount);
	public void heal(double amount);
	public double getHealth();
	public void die();
	public Polygon getHitBox();
	
}
