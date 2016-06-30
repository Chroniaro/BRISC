/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.states;

import com.brisc.BRISC.entities.Entity;
import com.brisc.BRISC.entities.Laser;
import com.brisc.BRISC.menu.ExitMenu;
import com.brisc.BRISC.menu.GameMenu;
import com.brisc.BRISC.menu.ResourceMenu;
import com.brisc.BRISC.entities.Cat;
import com.brisc.BRISC.worldManager.World;
import com.brisc.Resources.ResourceManager;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.text.DecimalFormat;
import java.util.*;

/**
 *
 * @author Zac
 */
public class Game extends GamePhase {
    
    public static boolean showDebugInfo = false;
    public static boolean showDebugLines = false;
    ArrayList<Cat> swarm;
    private boolean bounce;
    private double bounceHeight;
    public double x, y, dx, dy;
    public static final Point centerOfMotion = new Point(512, 370);
    public double size = 1;
    double speed = 1;
    private Cursor cursor;
    private final static BufferedImage crosshair = ResourceManager.getResource(ResourceManager.Resources.crosshair);
    public short mouse = 0;
    BufferedImage spaceBits;
    ArrayList<GameMenu> openMenus;
    private boolean crosshairCursor = true;
    private boolean eMenuOpen;
    World world;
    public static final Dimension loadedChunkSpace = new Dimension(5, 5);
    long lastUpdate;
    int fpsCountTimer = 0;
    double fps;
    
    public Game(World w) {
    	
    	openMenus = new ArrayList<>();
        
        swarm = new ArrayList<>();
        swarm.add(new Cat(448,350,0));
        swarm.get(0).offSetX = -10;
        swarm.get(0).offSetY = 0;
        swarm.get(0).setVisible(true);
        
        swarm.add(new Cat(448,350,1));
        swarm.get(1).offSetX = 50;
        swarm.get(1).offSetY = 100;
        swarm.get(1).setVisible(true);
        
        bounceHeight = 0;
        bounce = false;
        
        setCursor(Color.green, crosshair);
        
        spaceBits = ResourceManager.getResource(ResourceManager.Resources.spaceGeneric);
        
        this.world = w;
        lastUpdate = System.currentTimeMillis();
        
    }
    
    @Override
	public void init() {
		
    	world.setLoadedChunks(new Rectangle(
        		(int)(World.region(x, y).x - (int)(loadedChunkSpace.getWidth()  / 2)),
        		(int)(World.region(x, y).y - (int)(loadedChunkSpace.getHeight() / 2)),
        		(int)loadedChunkSpace.getWidth(), (int)loadedChunkSpace.getHeight()
        ));
    	
	}
    
    @Override
    public void update() {
    	
    	if(openMenus.size() > 0 ) {
    		
    		for(GameMenu m : openMenus) {
    			m.update();
    		}
    		
    		return;
    	}
    	
    	Point mouseLocation = getMousePosition();
    	
    	if(super.getMousePosition().distance(centerOfMotion) > 100) {
            
            final double ratio;
            if(super.getMousePosition().distance(centerOfMotion) > 300) {
                ratio = 200 / super.getMousePosition().distance(centerOfMotion);
                
            } else {
                ratio = Math.pow(super.getMousePosition().distance(centerOfMotion) - 100 , 2)/200 / super.getMousePosition().distance(centerOfMotion);
            }
            dx = (super.getMousePosition().x - centerOfMotion.x) * ratio * speed /20;
            dy = (super.getMousePosition().y - centerOfMotion.y) * ratio * speed/20;
            
            
        } else {
            dx = dy = 0;
        }
        
        x += dx;
        y += dy;
        world.setLoadedChunks(new Rectangle(
        		(int)(World.region(x, y).x - (int)(loadedChunkSpace.getWidth()  / 2)),
        		(int)(World.region(x, y).y - (int)(loadedChunkSpace.getHeight() / 2)),
        		(int)loadedChunkSpace.getWidth(), (int)loadedChunkSpace.getHeight()
        ));
        
        world.update();
        
        if(bounce)
            bounceHeight += .1;
        else
            bounceHeight -= .1;
        
        if(bounceHeight > 2 || bounceHeight < -2)
            bounce = !bounce;
        
        for(Cat c: swarm) {
            
            c.bob += bounceHeight;
            c.x = x;
            c.y = y;
            
        }
                
        for(Cat c : swarm) {
            
            Laser l = c.laser(mouse == 1, mouseLocation, new double[] {dx, dy});
            if(l != null) {
            	world.addObject(l);
            }
            
        }
        
    }

    @Override
    public void render(Graphics2D g2d) {
        
    	if(getBounds() == null) return;
    	
    	g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    	
    	//Scale
    	final Rectangle r = new Rectangle(
				(int)(getWidth() * (1-size) / 2), 
				(int)(getHeight() * (1-size) / 2), 
				(int)(getWidth() * size), 
				(int)(getHeight() * size)
		);
    	g2d.setClip(r);
    	AffineTransform at = g2d.getTransform();
    	AffineTransform or = g2d.getTransform();
    	at.scale(this.size, this.size);
    	at.translate(r.x/size, r.y/size);
    	g2d.setTransform(at);
        
        //Render Space
        for(int x = -1; x <= 2; x++) {
            
            for(int y = -1; y <= 2; y++) {
                
                final int xPos, yPos, width, height;
                if((x + (int)(this.x / 512)) % 2 == 0) {
                    xPos = -(int)(this.x % 512) + (512*x);
                    width = 512;
                }
                else {
                    xPos = 512 -(int)(this.x % 512) + (512*x);
                    width = -512;
                }
                if((y + (int)(this.y / 384)) % 2 == 0) {
                    yPos = -(int)(this.y % 384) + (384*y);
                    height = 384;
                }
                else {
                    yPos = 384 -(int)(this.y % 384) + (384*y);
                    height = -384;
                }
                g2d.drawImage(spaceBits, xPos, yPos, width, height, null);
                
            }
            
        }

        //Render Space Objects
        while(true) {
            try {
            	for(Entity o : world.getAllEntities(Entity.class)) {
	                if(o.isVisible()) {
	                    g2d.drawImage(o.getSprite(), centerOfMotion.x + (int)(o.x - x), centerOfMotion.y + (int)(o.y - y), null);
	                }
            	}
                break;
            } catch(ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
        
        //Render Cats
        for(Cat c: swarm) {
            if(c.isVisible()) {
                if(getMousePosition().x >= centerOfMotion.x + c.offSetX)
                    g2d.drawImage(c.getSprite(), (int)c.offSetX + centerOfMotion.x + (int)(c.x - x - c.getSprite().getWidth()/2), (int)c.offSetY + centerOfMotion.y + (int)(c.y - y - c.getSprite().getHeight()/2) + (int)c.bob, null);
                else {
                    g2d.drawImage(c.getSprite(), (int)c.offSetX + centerOfMotion.x + (int)(c.x - x - c.getSprite().getWidth()/2) + c.getSprite().getWidth(), (int)c.offSetY + centerOfMotion.y + (int)(c.y - y - c.getSprite().getHeight()/2) + (int)c.bob, -c.getSprite().getWidth(), c.getSprite().getHeight(), null);
                }
            }
        }
        
        //Scaled debug
        if(showDebugLines) {
            
            //Draw Cat lines
            for(Cat c: swarm) {
                if(c.isVisible()) {
                    g2d.setColor(new Color(0,0,1,(float)1));
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawLine(centerOfMotion.x + (int)c.offSetX, 0, centerOfMotion.x + (int)c.offSetX, (int)(getHeight()));
                    g2d.drawLine(0, (int)(centerOfMotion.y + (int)c.offSetY + c.bob), (int)(getWidth()), (int)(centerOfMotion.y + c.offSetY + c.bob));
                    g2d.fillOval(centerOfMotion.x + (int)c.offSetX - 5,(int)(centerOfMotion.y + (int)c.offSetY + c.bob) - 5, 10, 10);
                    g2d.setColor(Color.white);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRect((int)c.offSetX + centerOfMotion.x - (int)(c.getSprite().getWidth()/2), (int)c.offSetY + centerOfMotion.y - (int)(c.getSprite().getHeight()/2) + (int)c.bob, c.getSprite().getWidth(), c.getSprite().getHeight());
                }
            }
            
            //Draw Object Boxes
            g2d.setColor(Color.white);
            g2d.setStroke(new BasicStroke(3));
            while(true) {
                try {
                    for(Entity o : world.getAllEntities(Entity.class)) {
                        if(o.isVisible()) {
                            g2d.drawRect(centerOfMotion.x + (int)(o.x - x), centerOfMotion.y + (int)(o.y - y), o.getSprite().getWidth(), o.getSprite().getHeight());
                        }
                    }
                    break;
                } catch(ConcurrentModificationException e) {
                	e.printStackTrace();
                }
            }
            
            //Draw Chunk Borders
            g2d.setColor(Color.green);
            Point region = World.region(x + centerOfMotion.x, y + centerOfMotion.y);
            int lines = (int)Math.ceil(getWidth() / (double)World.regionsize);
            for(int x = (int)(-lines / 2); x <= (int)(lines / 2); x++) {
            	
            	int lineX = (centerOfMotion.x + (int)((region.x + x) * World.regionsize - this.x));
            	g2d.drawLine(lineX, 0, lineX, (int)getHeight());
            	
            }
            lines = (int)Math.ceil(getHeight() / (double)World.regionsize);
            for(int y = (int)(-lines / 2); y <= (int)(lines / 2); y++) {
            	
            	int lineY = (centerOfMotion.y + (int)((region.y + y) * World.regionsize - this.y));
            	g2d.drawLine(0, lineY, (int)getWidth(), lineY);
            	
            }
            
        }
        
        //Reset scaling
        g2d.setTransform(or);
        g2d.setClip(new Rectangle(0, 0, (int)getWidth(), (int)getHeight()));
        
        //Unscaled debug
        if(showDebugLines) {
	        //Draw Mouse Circles
	        g2d.setColor(new Color(1,0,0,(float)0.5));
	        g2d.setStroke(new BasicStroke(5));
	        g2d.drawOval(centerOfMotion.x - 100, centerOfMotion.y - 100, 200, 200);
	        g2d.drawOval(centerOfMotion.x - 300, centerOfMotion.y - 300, 600, 600);
	        g2d.fillOval(centerOfMotion.x - 10, centerOfMotion.y - 10, 20, 20);
	        
	        //Draw Crosshair
	        g2d.drawLine(centerOfMotion.x, 0, centerOfMotion.x, (int)(getHeight()));
	        g2d.drawLine(0, centerOfMotion.y, (int)(getWidth()), centerOfMotion.y);
	        
        }
        
        //Debug text
        if(showDebugInfo || showDebugLines) {
        	
	        DecimalFormat format = new DecimalFormat("#0.00##");
	        Point chunk = World.region(x, y);
	        String[] text = new String[] {
	        		"Pos: (" + format.format(x) + ", " + format.format(y) + ")",
	        		"Chunk: (" + chunk.x + ", " + chunk.y + ")",
	        		"",
	        		"X Motion: " + format.format(dy),
	        		"Y Motion: " + format.format(dx),
	        		"Total Motion: " + format.format(Math.sqrt((dx*dx) + (dy*dy))),
	        		"",
	        		"View Scale: " + size,
	        		"",
	        		"Entities: " + world.getAllEntities(Entity.class).size(),
	        		"FPS: " + fps
	        };
	        
	        final FontMetrics metrics = g2d.getFontMetrics();
	        g2d.setColor(new Color(.1f, .1f, .1f, .3f));
	        for (int i = 0; i < text.length; i++) {
	        	
				g2d.fillRect(5, i * (metrics.getHeight() + 5) + 5, metrics.stringWidth(text[i]), metrics.getHeight());
				
			}
	        g2d.setColor(new Color(.9f, .9f, .9f, 1f));
	        for (int i = 0; i < text.length; i++) {
	        	
				g2d.drawString(text[i], 5, (i + 1) * (metrics.getHeight() + 5));
				
			}
	        
        }
        
    	while(true) {
    		try {
		        for(GameMenu m : openMenus) {
		        	m.draw(g2d);
		        }
		        break;
    		} catch(ConcurrentModificationException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	fpsCountTimer++;
    	if(fpsCountTimer >= 10) {
    		
    		fps = 10000 / (System.currentTimeMillis() - lastUpdate);
    		lastUpdate = System.currentTimeMillis();
    		fpsCountTimer = 0;
    	}
        
    }

    @Override
    public void mouseDown(MouseEvent e) {
        
        mouse = (short)e.getButton();
        
        if(e.getButton() == 2) {
        	size = 1;
        }
        
        if(e.getButton() == 1) {
        	if(isCrosshairCursor()) setCursor(Color.red, crosshair);
        }
        
        if(e.getButton() == 3) {
        	if(isCrosshairCursor()) setCursor(Color.yellow, crosshair);
        }
        
    }

    @Override
    public void mouseUp(MouseEvent e) {
        
    	if(isCrosshairCursor()) setCursor(Color.green, crosshair);
	    mouse = 0;
        
    }
    
    public void setCrosshairCursor(boolean cc) {
    	
		this.crosshairCursor = cc;
		if(cc)  setCursor(Color.green, crosshair);
		
	}
    
    public boolean isCrosshairCursor() {
		return crosshairCursor;
	}
    
    public void setCursor(Color mouseColor, BufferedImage img) {
        
        Toolkit tk = Toolkit.getDefaultToolkit();
        
        int height = img.getHeight();
        int width = img.getWidth();
        WritableRaster raster = img.getRaster();
        
        for(int x = 0; x < width; x++) {
            
            for(int y = 0; y < height; y++) {
                
                int[] pixels = raster.getPixel(x, y, (int[]) null);
                pixels[0] = mouseColor.getRed();
                pixels[1] = mouseColor.getGreen();
                pixels[2] = mouseColor.getBlue();
                raster.setPixel(x, y, pixels);
                
            }
            
        }
        
        
        this.cursor = tk.createCustomCursor(img, new Point(25,25), "Crosshair");
        
    }
    
    @Override
    public Cursor getCursor() {
        return this.cursor;
    }

    @Override
    public String toString() {
        return "Game";
    }

    @Override
    public void keyDown(KeyEvent e) {
        
        switch(e.getKeyCode()) {
            
            case KeyEvent.VK_F3:
                showDebugLines = !showDebugLines;
                break;
                
            case KeyEvent.VK_F4:
            	showDebugInfo = !showDebugInfo;
            	break;
                
            case KeyEvent.VK_SPACE:
            	if(eMenuOpen) break; 
            	if(!openMenus.isEmpty()) {
            		setCrosshairCursor(true);
            		openMenus.clear();
            		break;
            	}
            	setCrosshairCursor(false);
            	cursor = Cursor.getDefaultCursor();
            	ResourceMenu menu = new ResourceMenu(this);
            	menu.setRelativeBounds(new Dimension(600,400));
            	this.openMenus.add(menu);
            	break;
            	
            case KeyEvent.VK_ESCAPE:
            	if(!openMenus.isEmpty()) {
            		setCrosshairCursor(true);
            		openMenus.clear();
            		if(eMenuOpen) eMenuOpen = false;
            		break;
            	}
            	eMenuOpen = true;
            	setCrosshairCursor(false);
            	cursor = Cursor.getDefaultCursor();
            	ExitMenu emenu = new ExitMenu(this);
            	emenu.setRelativeBounds(new Dimension(120,200));
            	this.openMenus.add(emenu);
            	break;
            
        }
        
    }
    
    @Override
    public void mouseWheelUp(MouseWheelEvent e) {
    	size /= Math.pow(2, 0.1);
    	size = Math.max(size, 0.5);
    }
    
    @Override
    public void mouseWheelDown(MouseWheelEvent e) {
    	size *= Math.pow(2, 0.1);
    	size = Math.min(size, 4);
    }
    
    @Override
    public Point getMousePosition() {
    	Point mouseLocation = super.getMousePosition();
    	return new Point((int)(mouseLocation.x / size - getWidth()*(1-size)/(2*size)), (int)(mouseLocation.y / size - getHeight()*(1-size)/(2*size)));
    }
    
}