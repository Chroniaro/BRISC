package com.brisc.BRISC.menu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.brisc.BRISC.states.Game;

public abstract class GameMenu {
	
	public enum Alignment {
		center, topleft, cursor
	}
	
	Alignment alignment;
	
	Point absoluteLocation;
	Dimension relativeBounds;
	ArrayList<MenuObject> objects;
	Game game;
	
	public GameMenu(Game g) {
		
		objects = new ArrayList<>();
		this.game = g;
		this.alignment = Alignment.center;
		
	}
	
	public void update() {
		
		Point mousePos;
		switch(alignment) {
		
		case center:
			mousePos = new Point(game.getMousePosition().x - (int)(game.getWidth() - relativeBounds.getWidth())/2,
					game.getMousePosition().y - (int)(game.getHeight() - relativeBounds.getHeight())/2);
			break;
			
		default:
			mousePos = new Point(game.getMousePosition().x = 10, game.getMousePosition().y - 10);
		
		}
		
		
		for(MenuObject o : objects) {
			
			o.update(mousePos, game.mouse == 1);
			
		}
		
	}
	
	public final void draw(Graphics2D g2d) {
		
		//Rectangle phaseBounds = game.getBounds();
		
		BufferedImage img = new BufferedImage(relativeBounds.width, relativeBounds.height, BufferedImage.TYPE_4BYTE_ABGR);
		
		this.render(img.createGraphics());
		
		Point pos;
		
		switch(alignment) {
		
		case center:
			pos = new Point((int)(game.getWidth() - relativeBounds.getWidth())/2, (int)(game.getHeight() - relativeBounds.getHeight())/2);
			break;
			
		default:
			pos = new Point(10, 10);
		
		}
		
		g2d.setColor(new Color((float)1.0, (float)1.0, (float)1.0, (float)0.8));
		g2d.fillRoundRect(pos.x, pos.y, relativeBounds.width, relativeBounds.height, 20, 20);
		g2d.setColor(Color.white);
		g2d.setStroke(new BasicStroke(10));
		g2d.drawRoundRect(pos.x, pos.y, relativeBounds.width, relativeBounds.height, 20, 20);
		
		g2d.drawImage(img, pos.x, pos.y, null);
		
	}
	
	public abstract void render(Graphics2D g2d);
	
	public void setAbsoluteLocation(Point absoluteLocation) {
		this.absoluteLocation = absoluteLocation;
	}
	
	public void setRelativeBounds(Dimension relativeBounds) {
		this.relativeBounds = relativeBounds;
	}
	
	public Point getAbsoluteLocation() {
		return absoluteLocation;
	}
	
	public Dimension getRelativeBounds() {
		return relativeBounds;
	}
	
}
