/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC;

import com.brisc.BRISC.states.GamePhase;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 *
 * @author Zac
 */
public class Component extends JComponent {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4558454032434944513L;
	GamePhase phase;
    
    public Component(GamePhase p) {
        
        System.out.println("Entering Phase " + p);
        this.phase = p;
        
        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                phase.mouseDown(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                phase.mouseUp(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
            
        });
        
    }
    
    @Override
    public void paintComponent(Graphics g) {
                
        super.paintComponent(g);
        
        setCursor(phase.getCursor());
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        phase.render(g2d);
        
    }
    
}
