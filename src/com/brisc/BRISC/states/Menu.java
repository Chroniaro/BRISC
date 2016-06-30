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
import java.awt.geom.Rectangle2D;
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
    
    public Menu() {
        
        buttons = new ArrayList<>();
        
        buttons.add(new Button(400,508,204, 80, "Play"));
        buttons.add(new Button(400,608,204, 80, "Exit"));
        
    }
    
    @Override
    public void init() {
        
    }
    
    @Override
    public void update() {
        
        Point mouseLocation = getMousePosition();

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
        
        g2d.drawImage(ResourceManager.getResource(ResourceManager.Resources.backGround), 0, 0, null);
        
        Font font = new Font(Font.SERIF, Font.BOLD, 40);
        g2d.setFont(font);
        
        for(Button b:buttons) {
            
            if(b.isHover())
                if(mouseDown)
                    g2d.setColor(Color.magenta);
                else
                    g2d.setColor(Color.red);
            else
                g2d.setColor(Color.blue);
            
            g2d.fillRoundRect(b.x, b.y, b.width, b.height, 20, 20);

            g2d.setColor(Color.white);
            Rectangle2D r = font.getStringBounds(b.label, g2d.getFontRenderContext());
            g2d.drawString(b.label, b.x + (int)((b.width - r.getWidth()) / 2), b.y + (int)((r.getHeight() + b.height) / 2));
            
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
