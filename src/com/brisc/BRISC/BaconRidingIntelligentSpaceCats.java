/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC;

import com.brisc.BRISC.states.GamePhase;
import com.brisc.BRISC.states.Menu;
import com.brisc.BRISC.states.Exit;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.*;
import javax.swing.*;


/**
 *
 * @author Zac
 */
public class BaconRidingIntelligentSpaceCats {
    
    public static String saveLocation;
    static boolean running = true;
    static JFrame frame;
    static Component visual;
    static GamePhase current;
    static boolean fullScreen;
    static long time;
    static Robot rob;
    
    /**
     * @param args the command line arguments
     * @throws Exception
     */
    public static void main(String[] args) /*throws Throwable*/ {
                
        frame = new JFrame();
        frame.setPreferredSize(new Dimension(1024,768));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new closeOperation());
        frame.setTitle("BRISC");
        frame.setVisible(true);
        frame.addComponentListener(new componentEvents());
        frame.addKeyListener(new keyEvents());
        frame.addMouseWheelListener(new wheeleEvents());
        
        try {
        	rob = new Robot();
        } catch(AWTException e) {
        	rob = null;
        }
        
        changePhase(new Menu());
        
        time = System.currentTimeMillis();
        
        while(running) {
            
            current.update();
            
            do {
            	
            	visual.repaint();
            	
            	if(rob != null && current.holdMouse()) {
            		
            		try {
            			
            			Point mousePo = MouseInfo.getPointerInfo().getLocation();
            			
            			if(mousePo.x < current.getBounds().getX() + 20)
            				rob.mouseMove(current.getBounds().x + 20, mousePo.y);
            			
            			if(mousePo.x > current.getBounds().getX() + current.getBounds().getWidth() - 20)
            				rob.mouseMove(current.getBounds().x + current.getBounds().width - 20, mousePo.y);
            			
            			if(mousePo.y < current.getBounds().getY() + 20)
            				rob.mouseMove(mousePo.x, current.getBounds().y + 20);
            			
            			if(mousePo.y > current.getBounds().getY() + current.getBounds().getHeight() - 20)
            				rob.mouseMove(mousePo.x, current.getBounds().y + current.getBounds().height - 20);
            			
            		} catch(NullPointerException e) {}
            		
            	}
            	
            } while(System.currentTimeMillis() - time < 5);
            
            time = System.currentTimeMillis();
            
        }
        
        frame.dispose();
    
    }
    
    public static void changePhase(GamePhase p) {
        
        try {
            
            frame.remove(visual);
            visual.removeAll();
            
        } catch(NullPointerException e) {
            
        }
        current = p;
        visual = new Component(p);
        frame.add(visual);
        frame.pack();
        updatePhaseBounds();
        visual.setCursor(p.getCursor());
        p.init();
        
    }
    
    public static void close() {
        
        running = false;
        
    }
    
    public static void updatePhaseBounds() {
        
        current.setBounds(new Rectangle(visual.getLocationOnScreen().x,visual.getLocationOnScreen().y,visual.getWidth(),visual.getHeight()));
        
    }
    
}

class closeOperation implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
            BaconRidingIntelligentSpaceCats.changePhase(new Exit());
        }

        @Override
        public void windowClosed(WindowEvent e) {
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }
}

class componentEvents implements ComponentListener {

    final static Component vis = BaconRidingIntelligentSpaceCats.visual;
    
    @Override
    public void componentResized(ComponentEvent e) {
        BaconRidingIntelligentSpaceCats.updatePhaseBounds();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        BaconRidingIntelligentSpaceCats.updatePhaseBounds();
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
    
}

class keyEvents implements KeyListener {
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        BaconRidingIntelligentSpaceCats.current.keyDown(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        BaconRidingIntelligentSpaceCats.current.keyUp(e);
    }
    
}

class wheeleEvents implements MouseWheelListener {

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation() > 0)
			BaconRidingIntelligentSpaceCats.current.mouseWheelUp(e);
		if(e.getWheelRotation() < 0)
			BaconRidingIntelligentSpaceCats.current.mouseWheelDown(e);
	}
	
}