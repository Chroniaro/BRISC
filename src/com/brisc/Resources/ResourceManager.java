/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.Resources;

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
        catBlack, catBrown, catOrange, catTabby, catPurple, backGround, spaceGeneric, crosshair
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
                    return ImageIO.read(getResource("images/BackGround.png"));
                    
                case spaceGeneric:
                    return ImageIO.read(getResource("images/SpaceTile.png"));
                    
                case crosshair:
                    return ImageIO.read(getResource("images/crosshair.png"));

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
    
}
