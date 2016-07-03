/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.states;

import com.brisc.BRISC.entities.Entity;
import com.brisc.BRISC.entities.Laser;
import com.brisc.BRISC.entities.Orbitor;
import com.brisc.BRISC.menu.ExitMenu;
import com.brisc.BRISC.menu.GameMenu;
import com.brisc.BRISC.menu.ResourceMenu;
import com.brisc.BRISC.entities.AbstractEntity;
import com.brisc.BRISC.entities.Cat;
import com.brisc.BRISC.entities.Damageable;
import com.brisc.BRISC.entities.Enemy;
import com.brisc.BRISC.worldManager.World;
import com.brisc.Resources.ResourceManager;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Zac
 */
public class Game extends GamePhase {
    
    public static boolean showDebugInfo = false;
    public static boolean showDebugLines = false;
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
    List<GameMenu> openMenus;
    private boolean crosshairCursor = true;
    private boolean eMenuOpen;
    World world;
    public static final Dimension loadedChunkSpace = new Dimension(5, 5);
    long lastUpdate;
    int fpsCountTimer = 0;
    double fps;
    
    public Game(World w) {
    	
    	openMenus = new CopyOnWriteArrayList<>();
        
    	this.world = w;
    	
        world.swarm.add(new Cat(448,350,0));
        world.swarm.get(0).offSetX = -10;
        world.swarm.get(0).offSetY = 0;
        world.swarm.get(0).setVisible(true);
        world.swarm.get(0).screenLocation = new Point(0, 0);
        
        world.swarm.add(new Cat(448,350,1));
        world.swarm.get(1).offSetX = 50;
        world.swarm.get(1).offSetY = 100;
        world.swarm.get(1).setVisible(true);
        world.swarm.get(1).screenLocation = new Point(0, 0);
        
        bounceHeight = 0;
        bounce = false;
        
        setCursor(Color.green, crosshair);
        
        spaceBits = ResourceManager.getResource(ResourceManager.Resources.spaceGeneric);
        
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
    	
    	Point offset = new Point((int)((getWidth() - 1024) / 2), (int) ((getHeight() - 768) / 2));
    	
    	if(openMenus.size() > 0 ) {
    		
    		for(GameMenu m : openMenus) {
    			m.update();
    		}
    		
    		return;
    	}
    	
    	Point mouseLocation = getMousePosition();
    	
    	Point mp = super.getMousePosition();
    	mp.x -= offset.x;
    	mp.y -= offset.y;
    	if(mp.distance(centerOfMotion) > 100) {
            
    		double multi = 1;
    		if(Enemy.enemiesOnCats >= 1)
    			multi = .25;
    		
            final double ratio;
            if(mp.distance(centerOfMotion) > 300) {
                ratio = 200 / mp.distance(centerOfMotion);
                
            } else {
                ratio = Math.pow(mp.distance(centerOfMotion) - 100 , 2)/200 / mp.distance(centerOfMotion);
            }
            dx = multi * (mp.x - centerOfMotion.x) * ratio * speed / 20;
            dy = multi * (mp.y - centerOfMotion.y) * ratio * speed / 20;
            
            
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
        
        world.update((int)x, (int)y);
        
        if(bounce)
            bounceHeight += .1;
        else
            bounceHeight -= .1;
        
        if(bounceHeight > 2 || bounceHeight < -2)
            bounce = !bounce;
        
        Cat[] list = new Cat[world.swarm.size()];
        world.swarm.toArray(list);
        for(Cat c: list) {
            
        	c.screenLocation.x = (int) x;
        	c.screenLocation.y = (int) y;
        	
            c.bob += bounceHeight;
            
            Laser l = c.laser(mouse == 1, mouseLocation, new double[] {dx, dy});
            if(l != null) {
            	world.addObject(l);
            }
            
        }
        
        for(Laser l : world.getAllEntities(Laser.class)) {
        	
        	l.checkCollisions(world);
        	
        }
        
        for(Damageable d : world.getAllEntities(Damageable.class)) {
        	
        	if(d.getHealth() <= 0) {
        		
        		d.die();
        		world.removeObject(d.asEntity());
        		
        	}
        	
        }
        
    }

    @Override
    public void render(Graphics2D g2d) {
        
    	if(getBounds() == null) return;
    	
    	List<Entity> entities = world.getAllEntities(Entity.class);
    	sort(entities);
    	
    	Point offset = new Point((int)((getWidth() - 1024) / 2), (int) ((getHeight() - 768) / 2));
    	
    	//Scale
    	final Rectangle r = new Rectangle(
				(int)(offset.x + 1024 * (1-size) / 2), 
				(int)(offset.y + 768 * (1-size) / 2), 
				(int)(1024 * size), 
				(int)(768 * size)
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
        AffineTransform old = g2d.getTransform();
        g2d.translate(centerOfMotion.x - x, centerOfMotion.y - y);;
    	for(Entity o : entities) {
            if(o.isVisible()) {
            	AffineTransform prev = g2d.getTransform();
            	if(Orbitor.class.isAssignableFrom(o.getClass())) {
            		
            		if(((Orbitor) o).speed >= 0)
                		g2d.rotate(
                				2*Math.PI - ((Orbitor) o).ang, 
                				(int)o.x + (o.getSprite().getWidth() / 2), 
                				(int)o.y + (o.getSprite().getHeight() / 2)
                		);
            		else
            			g2d.rotate(
                				3*Math.PI - ((Orbitor) o).ang, 
                				(int)o.x + (o.getSprite().getWidth() / 2), 
                				(int)o.y + (o.getSprite().getHeight() / 2)
                		);
            	}
            	g2d.drawImage(o.getSprite(), (int)o.x, (int)o.y, null);
                g2d.setTransform(prev);
                if(Damageable.class.isAssignableFrom(o.getClass())) {
                	
                	drawHealthBar(g2d, (Damageable) o);
                	
                }
            }
    	}
        
    	g2d.setTransform(old);
    	
        //Render Cats
    	Cat[] l = new Cat[world.swarm.size()];
    	world.swarm.toArray(l);
        for(Cat c: l) {
            if(c.isVisible()) {
                if(getMousePosition().x >= centerOfMotion.x + c.offSetX)
                    g2d.drawImage(c.getSprite(), centerOfMotion.x + (int)c.offSetX - (int)(c.getSprite().getWidth()/2), centerOfMotion.y + (int)c.offSetY - (int)(c.getSprite().getHeight()/2) + (int)c.bob, null);
                else {
                    g2d.drawImage(c.getSprite(), centerOfMotion.x + (int)c.offSetX + (int)(c.getSprite().getWidth()/2), centerOfMotion.y + (int)c.offSetY - (int)(c.getSprite().getHeight()/2) + (int)c.bob, -c.getSprite().getWidth(), c.getSprite().getHeight(), null);
                }
                
                if(c.getHealth() < 1) {
                	
                	
                	drawHealthBar(g2d, c);
                	
                }
                
            }
        }
        
        //Scaled debug
        if(showDebugLines) {
            
            //Draw Cat lines
            for(Cat c: world.swarm) {
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
            g2d.setStroke(new BasicStroke(3));
            old = g2d.getTransform();
        	g2d.translate(centerOfMotion.x - x, centerOfMotion.y - y);
        	g2d.setColor(Color.white);
        	for(Entity e : entities) {
        		g2d.drawRect((int)e.x, (int)e.y, e.getSprite().getWidth(), e.getSprite().getHeight());
        	}
        	g2d.setColor(Color.yellow);
            for(Damageable d : world.getAllEntities(Damageable.class)) {
            	g2d.draw(d.getHitBox());
            }
            for(Cat c : world.swarm) {
            	g2d.draw(c.getHitBox());
            }
            g2d.setTransform(old);
                    
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
	        g2d.drawOval(offset.x + centerOfMotion.x - 100, offset.y + centerOfMotion.y - 100, 200, 200);
	        g2d.drawOval(offset.x + centerOfMotion.x - 300, offset.y + centerOfMotion.y - 300, 600, 600);
	        g2d.fillOval(offset.x + centerOfMotion.x - 10, offset.y + centerOfMotion.y - 10, 20, 20);
	        
	        //Draw Crosshair
	        g2d.drawLine(offset.x + centerOfMotion.x, offset.y,offset.x + centerOfMotion.x, offset.y + (int)(getHeight()));
	        g2d.drawLine(offset.x, offset.y + centerOfMotion.y, offset.x + (int)(getWidth()), offset.y + centerOfMotion.y);
	        
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
	        		"Entities: " + entities.size(),
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
        
        for(GameMenu m : openMenus) {
        	m.draw(g2d);
        }
    	
    	fpsCountTimer++;
    	if(fpsCountTimer >= 10) {
    		
    		fps = 10000 / (System.currentTimeMillis() - lastUpdate);
    		lastUpdate = System.currentTimeMillis();
    		fpsCountTimer = 0;
    	}
        
    }

    public static void drawHealthBar(Graphics2D g2d, Damageable d) {
    	
    	final Color full = Color.green;
    	final Color empty = Color.red;
    	final Color bound = Color.red;
    	Rectangle surBox = d.getHitBox().getBounds();
    	Rectangle area = new Rectangle();
    	area.height = 10;
    	area.width = surBox.width;
    	area.x = surBox.x;
    	area.y = surBox.y + surBox.height + area.height + 5;
    	g2d.setColor(empty);
    	g2d.fill(area);
    	g2d.setColor(full);
    	g2d.fillRect(area.x, area.y, (int)Math.round(d.getHealth() * area.width), area.height);
    	g2d.setColor(bound);
    	g2d.setStroke(new BasicStroke(3));
    	g2d.draw(area);
    	
    }
    
    public static void drawHealthBar(Graphics2D g2d, Cat c) {
    	
    	final Color full = Color.green;
    	final Color empty = Color.red;
    	final Color bound = Color.white;
    	Rectangle surBox = c.getHitBox().getBounds();
    	Rectangle area = new Rectangle();
    	area.height = 10;
    	area.width = surBox.width;
    	area.x = (int) c.offSetX + centerOfMotion.x - surBox.width / 2;
    	area.y = (int) c.offSetY + centerOfMotion.y + surBox.height / 2 + area.height + 5;
    	g2d.setColor(empty);
    	g2d.fill(area);
    	g2d.setColor(full);
    	g2d.fillRect(area.x, area.y, (int)Math.round(c.getHealth() * area.width), area.height);
    	g2d.setColor(bound);
    	g2d.setStroke(new BasicStroke(3));
    	g2d.draw(area);
    	
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
    
    public static <T extends AbstractEntity> void sort(List<T> list) {
    	
    	sort(list, 0, list.size() - 1);
    	
    }
    
    public static <T extends AbstractEntity> void sort(List<T> list, int lowIndex, int highIndex) {
    	
    	int l = lowIndex;
    	int u = highIndex;
    	int pivot = (int)Math.round(l / 2 + u / 2);
    	
    	while(l <= u) {
    		
    		while(list.get(l).layer < list.get(pivot).layer)
    			l++;
    		while(list.get(u).layer > list.get(pivot).layer)
    			u--;
    		
    		if(l <= u) {
    			
    			T e = list.get(l);
    			list.set(l, list.get(u));
    			list.set(u, e);
    			
    			l++;
    			u--;
    			
    		}
    		
    	}
    	
    	if(lowIndex < u)
			sort(list, lowIndex, u);
		
		if(highIndex > l)
			sort(list, l, highIndex);
    	
    }
    
}