package com.brisc.BRISC.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.brisc.BRISC.states.Game;

public class ResourceMenu extends GameMenu {

	Button test;
	
	public ResourceMenu(Game g) {
		
		super(g);
		
		test = new Button(10, 10, 100, 30, "Test");
		this.objects.add(test);
		
	}
	
	@Override
	public void update() {
		
		super.update();
		
		if(test.shouldExecute()) {
			
			System.out.println("test");
			
		}
		
	}

	@Override
	public void render(Graphics2D g2d) {
		
		Font font = new Font(Font.SERIF, Font.BOLD, 20);
        g2d.setFont(font);
        
        if(test.isHover())
            if(game.mouse == 1)
                g2d.setColor(Color.magenta);
            else
                g2d.setColor(Color.red);
        else
            g2d.setColor(Color.blue);
        
        g2d.fillRoundRect(test.x, test.y, test.width, test.height, 20, 20);

        g2d.setColor(Color.white);
        Rectangle2D r = font.getStringBounds(test.label, g2d.getFontRenderContext());
        g2d.drawString(test.label, test.x + (int)((test.width - r.getWidth()) / 2), test.y + (int)((r.getHeight() + test.height) / 2));
		
	}

}
