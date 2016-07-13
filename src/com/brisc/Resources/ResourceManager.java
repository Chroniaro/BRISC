/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.Resources;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author Zac
 */
public abstract class ResourceManager {
    
    public enum Resources {
        catBlack, catBrown, catOrange, catTabby, catPurple, 
        backGround, spaceGeneric, crosshair,
        enemyBasic, enemyBig
    }
    
    public static URL getResource(String resource) {
        if(ResourceManager.class.getResource(resource) != null)
            return(ResourceManager.class.getResource(resource));
        else
            throw new Error("No such resource.");
    }
    
    public static BufferedImage getResource(Resources resource) {
        
        try {
            switch(resource) {

                case catBlack:
                    return ImageIO.read(getResource("images/cats/BRISC Black.png"));

                case catBrown:
                    return ImageIO.read(getResource("images/cats/BRISC Brown.png"));

                case catOrange:
                    return ImageIO.read(getResource("images/cats/BRISC Orange.png"));

                case catTabby:
                    return ImageIO.read(getResource("images/cats/BRISC Tabby.png"));

                case catPurple:
                    return ImageIO.read(getResource("images/cats/BRISC Purple.png"));
                    
                case backGround:
                    return ImageIO.read(getResource("images/Background.png"));
                    
                case spaceGeneric:
                    return ImageIO.read(getResource("images/SpaceTile.png"));
                    
                case crosshair:
                    return ImageIO.read(getResource("images/crosshair.png"));
                    
                case enemyBasic:
                	return ImageIO.read(getResource("images/enemies/enemy_basic.png"));
                	
                case enemyBig:
                	return ImageIO.read(getResource("images/enemies/enemy_big.png"));

                default:
                    return(null);
            }
        } catch(IOException e) {
            throw new Error("Failed to load resource: " + resource);
        }
        
    }
    
    public static BufferedImage getCat(int x) {
    	
    	return(new BufferedImage[] {
    			getResource(Resources.catBlack),
    			getResource(Resources.catBrown),
    			getResource(Resources.catOrange),
    			getResource(Resources.catPurple),
    			getResource(Resources.catTabby)
    	}[x]);
    	
    }
    
    public static BufferedImage getGodCat(float hue) {
    	
    	BufferedImage godCat;
    	
		try {
			
			godCat = ImageIO.read(getResource("images/cats/God BRISC.png"));
			
			Color current;
			
			for(int x = 0; x < 20; x++)
				for(int y = 0; y < 20; y++) {
					
					current = new Color(godCat.getRGB(x * 4, y * 4));
					if(current.getRed() > 0)
						if(current.getRed() == current.getBlue())
							if(current.getRed() == current.getGreen()) {
								
								int color = Color.HSBtoRGB(hue, 1f, current.getRed() / 255f);
								for(int px = 0; px < 4; px++)
									for(int py = 0; py < 4; py++)
										godCat.setRGB(x * 4 + px, y * 4 + py, color);
								
							}
					
				}
	    	
	    	return godCat;
			
		} catch (IOException e) {
			throw new Error("Failed to load resource: GodCat, hue: " + hue);
		}
    	
    }
    
}
