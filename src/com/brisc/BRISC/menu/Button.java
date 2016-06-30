/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.menu;

import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author Zac
 */
public class Button implements MenuObject {
    
    public int x,y,width,height;
    public String label;
    
    private boolean hover;
    private boolean mouseWasDown;
    private boolean execute;
    
    public Button(int x, int y, int width, int height, String label) {
        
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        
    }
    
    @Override
    public void update(Point mousePosition, boolean mouseDown) {
        
        hover = (new Rectangle((int)x, (int)y, (int)width, (int)height)).contains(mousePosition);
        execute = (mouseDown && !mouseWasDown);
        mouseWasDown = mouseDown;
        
    }
    
    public boolean shouldExecute() {
        return execute && hover;
    }

    public boolean isHover() {
        return hover;
    }
    
}
