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
import com.brisc.BRISC.BaconRidingIntelligentSpaceCats;
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
import java.util.ArrayList;
import java.util.Collections;
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
    double bounceOffset;
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
    public static final Dimension loadedChunkSpace = new Dimension(7, 7);
    long lastUpdate;
    int fpsCountTimer = 0;
    int motionTimer = 0;
    long lastMotion;
    double fps;
    public double catFood;
    public double foodEatRate;
    public double catNip;
    public double nipEatRate;
    Robot mouseMover;
    List<AffineTransform> matrices;
    BufferedImage godImg;
    
    public Game(World w) {
    	
    	try {
			mouseMover = new Robot();
		} catch (AWTException e) {
			mouseMover = null;
		}
    	
    	openMenus = new CopyOnWriteArrayList<>();
        
    	this.world = w;
    	
        world.swarm.add(new Cat(448,350,0));
        world.swarm.get(0).offSetX = -100;
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
        lastMotion = System.currentTimeMillis();
        
        catFood = 100;
        catNip = 10;
        foodEatRate = 1.0;
        nipEatRate = 0.0;
        
        matrices = new ArrayList<>();
        
        godImg = ResourceManager.getGodCat(0.9f);
        bounceOffset = 0;
        
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
    	
    	catFood = Math.max(0, (catFood - foodConsumed()));
        if(catFood == 0)
        	foodEatRate = 0;
        
        catNip = Math.max(0, (catNip - nipConsumed()));
        if(catNip == 0)
        	nipEatRate = 0;
    	
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
        
        x += BaconRidingIntelligentSpaceCats.UPDATE_SPEED / 5.0 * dx;
        y += BaconRidingIntelligentSpaceCats.UPDATE_SPEED / 5.0 * dy;
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
        bounceOffset += bounceHeight;
        for(Cat c: list) {
            
        	c.screenLocation.x = (int) x;
        	c.screenLocation.y = (int) y;
        	
        	c.bob += bounceHeight;
            
            Laser l = c.laser(mouse == 1, mp, new double[] {dx, dy});
            if(l != null) {
            	world.addObject(l);
            }
            
            c.heal(getRegenRate() * 0.005);
            
            if(c.getHealth() <= 0.01) {
            	
            	c.die(this);
            	world.swarm.remove(c);
            	
            }
            
        }
        
        for(Laser l : world.getAllEntities(Laser.class)) {
        	
        	l.checkCollisions(world);
        	
        }
        
        for(Damageable d : world.getAllEntities(Damageable.class)) {
        	
        	if(d.getHealth() <= 0) {
        		
        		d.die(this);
        		world.removeObject(d.asEntity());
        		
        	}
        	
        }
        
        motionTimer = 0;
        lastMotion = System.currentTimeMillis();
        
    }

	@Override
    public void render(Graphics2D g2d) {
        
		final double speedMulti = ((System.currentTimeMillis() - lastMotion) / 90.0);
		final double catX = x + dx * speedMulti;
		final double catY = y + dy * speedMulti;
		
    	if(getBounds() == null) return;
    	
    	List<Entity> entities = world.getAllEntities(Entity.class);
    	Collections.sort(entities);
    	
    	Point offset = new Point((int)((getWidth() - 1024) / 2), (int) ((getHeight() - 768) / 2));
    	
    	//Scale
    	final Rectangle r = new Rectangle(
				(int)(offset.x + 1024 * (1-size) / 2), 
				(int)(offset.y + 768 * (1-size) / 2), 
				(int)(1024 * size), 
				(int)(768 * size)
		);
    	g2d.setClip(r);
    	pushMatrix(g2d);
    	g2d.scale(this.size, this.size);
    	g2d.translate(r.x/size, r.y/size);
        
        //Render Space
        for(int x = -1; x <= 2; x++) {
            
            for(int y = -1; y <= 2; y++) {
                
                final int xPos, yPos, width, height;
                if((x + (int)(catX / 512)) % 2 == 0) {
                    xPos = -(int)(catX % 512) + (512*x);
                    width = 512;
                }
                else {
                    xPos = 512 -(int)(catX % 512) + (512*x);
                    width = -512;
                }
                if((y + (int)(catY / 384)) % 2 == 0) {
                    yPos = -(int)(catY % 384) + (384*y);
                    height = 384;
                }
                else {
                    yPos = 384 -(int)(catY % 384) + (384*y);
                    height = -384;
                }
                g2d.drawImage(spaceBits, xPos, yPos, width, height, null);
                
            }
            
        }

        //Render Space Objects
        pushMatrix(g2d);
        g2d.translate(centerOfMotion.x - catX, centerOfMotion.y - catY);;
    	for(Entity o : entities) {
            if(o.isVisible()) {
            	AffineTransform prev = g2d.getTransform();
            	if(Orbitor.class.isAssignableFrom(o.getClass())) {
            		
            		g2d.rotate(-((Orbitor) o).speed * speedMulti,
            				(int)(o.x - Math.sin(((Orbitor) o).ang) * ((Orbitor) o).dist),
            				(int)(o.y - Math.cos(((Orbitor) o).ang) * ((Orbitor) o).dist)
            		);
            		
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
            	g2d.drawImage(o.getSprite(), (int)(o.x + speedMulti * o.dx), (int)(o.y + speedMulti * o.dy), null);
                g2d.setTransform(prev);
                if(Damageable.class.isAssignableFrom(o.getClass())) {
                	
                	drawHealthBar(g2d, (Damageable) o);
                	
                }
            }
    	}
        
    	popMatrix(g2d);
    	
        //Render Cats
    	if(getMousePosition().x >= offset.x + centerOfMotion.x)
    		g2d.drawImage(godImg, centerOfMotion.x - 40, (int) (centerOfMotion.y - 40 + bounceOffset), null);
    	else
    		g2d.drawImage(godImg, centerOfMotion.x + 40, (int) (centerOfMotion.y - 40 + bounceOffset), -80, 80, null);
    	Cat[] l = new Cat[world.swarm.size()];
    	world.swarm.toArray(l);
        for(Cat c: l) {
            if(c.isVisible()) {
                if(getMousePosition().x >= offset.x + centerOfMotion.x + c.offSetX)
                    g2d.drawImage(c.getSprite(), centerOfMotion.x + (int)c.offSetX - (int)(c.getSprite().getWidth()/2), centerOfMotion.y + (int)c.offSetY - (int)(c.getSprite().getHeight()/2) + (int)bounceOffset, null);
                else {
                    g2d.drawImage(c.getSprite(), centerOfMotion.x + (int)c.offSetX + (int)(c.getSprite().getWidth()/2), centerOfMotion.y + (int)c.offSetY - (int)(c.getSprite().getHeight()/2) + (int)bounceOffset, -c.getSprite().getWidth(), c.getSprite().getHeight(), null);
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
            pushMatrix(g2d);
        	g2d.translate(centerOfMotion.x - catX, centerOfMotion.y - catY);
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
            popMatrix(g2d);
                    
            //Draw Chunk Borders
            g2d.setColor(Color.green);
            Point region = World.region(catX + centerOfMotion.x, catY + centerOfMotion.y);
            int lines = (int)Math.ceil(getWidth() / (double)World.regionsize);
            for(int x = (int)(-lines / 2); x <= (int)(lines / 2); x++) {
            	
            	int lineX = (centerOfMotion.x + (int)((region.x + x) * World.regionsize - catX));
            	g2d.drawLine(lineX, 0, lineX, (int)getHeight());
            	
            }
            lines = (int)Math.ceil(getHeight() / (double)World.regionsize);
            for(int y = (int)(-lines / 2); y <= (int)(lines / 2); y++) {
            	
            	int lineY = (centerOfMotion.y + (int)((region.y + y) * World.regionsize - catY));
            	g2d.drawLine(0, lineY, (int)getWidth(), lineY);
            	
            }
            
        }
        
        //UI
        g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        
        pushMatrix(g2d);
        
        final Rectangle compBounds = new Rectangle((int)(getWidth() - 160), (int)(getHeight() - 160), 120, 120);
        
        g2d.translate(compBounds.getCenterX(), compBounds.getCenterY());
        g2d.scale(compBounds.width / 100, compBounds.height / 100);
        
        g2d.setColor(new Color(.2f, .2f, .2f, .4f));
        g2d.fillOval(-50, -50, 100, 100);
        g2d.setColor(Color.red);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(-50, -50, 100, 100);
        
        final Polygon needle = new Polygon();
        needle.addPoint(0, 50);
        needle.addPoint(5,  0);
        needle.addPoint(-5,  0);
        
        final double ang;
        if(catY > 0)
        	ang = Math.PI + Math.atan((double) -catX / catY);
        else
        	ang = Math.atan((double) -catX / catY);
        
        pushMatrix(g2d);
        
        g2d.rotate(ang);
        g2d.setColor(Color.green);
        g2d.fill(needle);
        g2d.rotate(Math.PI);
        g2d.setColor(Color.red);
        g2d.fill(needle);
        
        popMatrix(g2d);
        
        drawText(g2d, new String[] {
        		
        	"Pos: (" + Math.round(catX / 10) / 100.0 + " , " + Math.round(catY / 10) / 100.0 + ")",
        	"Distance: " + (int) Math.round(Math.sqrt(Math.pow(catX / 1000, 2) + Math.pow(catY / 1000, 2)))
        		
        }, -50, 50);
        
        popMatrix(g2d);
        
        pushMatrix(g2d);
        
        drawText(g2d, new String[] {
        
        	"Food: " + (int) catFood,
        	"Catnip: " + (int) catNip
        		
        }, 10, getHeight() - 80);
        
        popMatrix(g2d);
        
        //Reset scaling
        popMatrix(g2d);
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
	        
	        drawText(g2d, new String[] {
	        		
	        		"Pos: (" + format.format(catX) + ", " + format.format(catY) + ")",
	        		"Chunk: (" + chunk.x + ", " + chunk.y + ")",
	        		"",
	        		"X Motion: " + format.format(dy),
	        		"Y Motion: " + format.format(dx),
	        		"Total Motion: " + format.format(Math.sqrt((dx*dx) + (dy*dy))),
	        		"",
	        		"View Scale: " + size,
	        		"",
	        		"Food: " + format.format(catFood),
	        		"Nip: " + format.format(catNip),
	        		"Regen Rate: " + format.format(getRegenRate()),
	        		"",
	        		"Entities: " + entities.size(),
	        		"FPS: " + fps
	        		
	        }, 0, 0);
	        
        }
        
        for(GameMenu m : openMenus) {
        	m.draw(g2d);
        }
    	
    	fpsCountTimer++;
    	if(fpsCountTimer >= 20) {
    		
    		fps = 10000 / (System.currentTimeMillis() - lastUpdate);
    		lastUpdate = System.currentTimeMillis();
    		fpsCountTimer = 0;
    	}
    	
    	motionTimer++;
        
    }
	
	void pushMatrix(Graphics2D g) {
		
		matrices.add(g.getTransform());
		
	}
	
	boolean pushPrevious(Graphics2D g, int stepsBack) {
		
		if(matrices.size() >= stepsBack) {
		
			pushMatrix(g);
			g.setTransform(matrices.get(matrices.size() - 1 - stepsBack));
		
		} else
			return false;
		
		return true;
		
	}
	
	AffineTransform popMatrix(Graphics2D g) {
		
		AffineTransform at = g.getTransform();
		g.setTransform(matrices.get(matrices.size() - 1));
		matrices.remove(matrices.size() - 1);
		return at;
		
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
    
    public static void drawText(Graphics2D g2d, String[] text, double x, double y, Color textColor, Color back, float alphaBack) {
    	
    	final FontMetrics metrics = g2d.getFontMetrics();
        g2d.setColor(new Color(back.getRed() / 255f, back.getGreen() / 255f, back.getBlue() / 255f, alphaBack * (back.getAlpha() / 255f)));
        for (int i = 0; i < text.length; i++) {
        	
			g2d.fillRect((int) x - 5, (int) y + i * (metrics.getHeight() + 5) + 5, metrics.stringWidth(text[i]) + 10, metrics.getHeight());
			
		}
        g2d.setColor(textColor);
        for (int i = 0; i < text.length; i++) {
        	
			g2d.drawString(text[i], (int) x, (int) y + (i + 1) * (metrics.getHeight() + 5));
			
		}
    	
    }
    
    public static void drawText(Graphics2D g2d, String[] text, double x, double y) {
    	
    	drawText(g2d, text, x, y, Color.white, Color.black, 0.2f);
    	
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
            	ResourceMenu menu = new ResourceMenu(this, 600, 400);
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
            	ExitMenu emenu = new ExitMenu(this, 120, 200);
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
    
    public double getRegenRate() {
    	
    	double rate = foodEatRate + nipEatRate / 10;
    	
    	if(catFood == 0)
    		rate = 0;
    	
    	return(.1 * StrictMath.log(rate + 0.2));
    	
    }
    
    public double nipConsumed() {
		return nipEatRate * nipEatRate * world.swarm.size() * 0.00002;
	}

	public double foodConsumed() {
		return foodEatRate * foodEatRate * world.swarm.size() * 0.0002;
	}
    
    @Override
    public boolean holdMouse() {
    	
    	return openMenus.size() == 0;
    	
    }
    
}