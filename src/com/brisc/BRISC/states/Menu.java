/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.states;

import com.brisc.BRISC.BaconRidingIntelligentSpaceCats;
import com.brisc.Resources.ResourceManager;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import com.brisc.BRISC.menu.Button;
import com.brisc.BRISC.worldManager.Generator;
import com.brisc.BRISC.worldManager.World;

/**
 *
 * @author Zac
 */
public class Menu extends GamePhase {

    ArrayList<Button> buttons;
    boolean mouseDown;
    BufferedImage image;
    
    public Menu() {
        
        buttons = new ArrayList<>();
        
        int width = 150;
        int height = 60;
        buttons.add(new Button(1080/2 - width/2,575, width, height, "Play"));
        buttons.add(new Button(1080/2 - width/2,660, width, height, "Exit"));
        
        image = ResourceManager.getResource(ResourceManager.Resources.backGround);
        
    }
    
    @Override
    public void init() {
        
    }
    
    @Override
    public void update() {
        
        Point mouseLocation = getMousePosition();
        mouseLocation.x *= 1024 / getWidth();
        mouseLocation.y *= 768 / getHeight();

        for(Button b: buttons)
            b.update(mouseLocation, mouseDown);
        
        if(buttons.get(0).shouldExecute()) {
            
        	Generator g = new Generator("slurp");
            BaconRidingIntelligentSpaceCats.changePhase(new Game(new World("~/Desktop/wrld", g)));
            
        }
        
        if(buttons.get(1).shouldExecute()) {
            
            BaconRidingIntelligentSpaceCats.changePhase(new Exit());
            
        }
        
    }
    
    @Override
    public void render(Graphics2D g2d) {
    	
    	if(getBounds() != null)
    		g2d.scale(getWidth() / 1024, getHeight() / 768);
        
        g2d.drawImage(image, 0, 0, 1024, 768, null);
        
        Font font = new Font("Comic Sans MS", Font.BOLD, 40);
        g2d.setFont(font);
        g2d.setStroke(new BasicStroke(10));
        
        for(Button b:buttons) {
            
        	g2d.setColor(new Color(0, 0, 80));
        	g2d.fillRect(b.x, b.y, b.width, b.height);
        	
            if(b.isHover())
                if(mouseDown)
                    g2d.setColor(Color.magenta);
                else
                    g2d.setColor(Color.red);
        
            g2d.drawRect(b.x, b.y, b.width, b.height);

            g2d.setColor(Color.lightGray);
            int width = g2d.getFontMetrics().stringWidth(b.label);
            int height = g2d.getFontMetrics().getHeight();
            g2d.drawString(b.label, b.x + (int)((b.width - width) / 2), b.y + (int)((height - 20 + b.height) / 2));
            
        }
        
    }

    @Override
    public void mouseDown(MouseEvent e) {
        
        mouseDown = true;
        
    }

    @Override
    public void mouseUp(MouseEvent e) {
        
        mouseDown = false;
        
    }

    @Override
    public Cursor getCursor() {
        return(java.awt.Cursor.getDefaultCursor());
    }

    @Override
    public String toString() {
        return "Menu";
    }
    
}
