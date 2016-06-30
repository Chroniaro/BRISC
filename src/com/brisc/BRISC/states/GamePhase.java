/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.states;

import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Zac
 */
public abstract class GamePhase {
    
    private Rectangle bounds;
    public abstract void update();
    public abstract void render(Graphics2D g2d);
    public abstract void init();
    
    @Override
    public abstract String toString();
    
    public void mouseDown(MouseEvent e) {}
    public void mouseUp(MouseEvent e) {}
    public void keyDown(KeyEvent e) {}
    public void keyUp(KeyEvent e) {}
    public void mouseWheelUp(MouseWheelEvent e) {}
    public void mouseWheelDown(MouseWheelEvent e) {}
    
    public Cursor getCursor() {
        
        return(java.awt.Cursor.getDefaultCursor());
        
    }
    
    public final void setBounds(Rectangle r) {
        
        this.bounds = r;
        
    }
    
    public final Rectangle getBounds() {
        
        return this.bounds;
        
    }
    
    public final double getWidth() {
    	
        return this.bounds.getWidth();
        
    }
    
    public final double getHeight() {
        
        return this.bounds.getHeight();
        
    }
    
    public final double getX() {
        return getLocationOnScreen().x;
    }
    
    public final double getY() {
        return getLocationOnScreen().y;
    }
    
    public final Point getLocationOnScreen() {
        
        while(true)
            try {
                return this.bounds.getLocation();
            } catch(Exception e){
                System.out.println(e.getMessage() + " GamePhase, 74");
            }
        
    }
    
    public Point getMousePosition() {
        
        while(true)
            try {
                return new Point((int)(MouseInfo.getPointerInfo().getLocation().getX() - bounds.getX()), 
                    (int)(MouseInfo.getPointerInfo().getLocation().getY() - bounds.getY()));
            } catch(NullPointerException e) {
            	bounds = new Rectangle(0,0,1,1);
            } catch(Exception e) {
                System.out.println(e.getMessage() + " GamePhase, 86");
            }
        
    }
    
}
