package com.brisc.BRISC.menu;

import java.awt.*;
import java.text.DecimalFormat;

import com.brisc.BRISC.states.Game;

public class ResourceMenu extends GameMenu {

	Slider foodSlider, nipSlider;
	
	public ResourceMenu(Game g, int width, int height) {
		
		super(g, width, height);
		
		foodSlider = new Slider(30, 120, width - 60, 10, 50, "", g.foodEatRate, 0, 5, 51);
		nipSlider  = new Slider(30, 180, width - 60, 10, 50, "",  g.nipEatRate, 0, 5, 51);
		this.objects.add(foodSlider);
		this.objects.add(nipSlider);
		
	}
	
	@Override
	public void update() {
		
		super.update();
		game.foodEatRate = foodSlider.getValue();
		game.nipEatRate = nipSlider.getValue();
		
	}

	@Override
	public void render(Graphics2D g2d) {
		
		DecimalFormat percentageFormat = new DecimalFormat("#.####%");
		DecimalFormat absoluteFormat = new DecimalFormat("#.###");
		Font font = new Font(Font.SERIF, Font.BOLD, 20);
        g2d.setFont(font);
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(Color.darkGray);
        g2d.fill(foodSlider.getSlider());
        g2d.drawLine(foodSlider.x, foodSlider.y + foodSlider.height / 2, foodSlider.x + foodSlider.width, foodSlider.y + foodSlider.height / 2);
        g2d.fill(nipSlider.getSlider());
        g2d.drawLine(nipSlider.x, nipSlider.y + nipSlider.height / 2, nipSlider.x + nipSlider.width, nipSlider.y + nipSlider.height / 2);
        if(game.getRegenRate() < 0)
        	g2d.setColor(Color.red);
        g2d.drawString("Cat food consumption rate: " + percentageFormat.format(foodSlider.getValue()), 60, foodSlider.y - 5);
        g2d.drawString("Regen: " + percentageFormat.format(game.getRegenRate()) + " every second.", 50, 230);
        g2d.setColor(Color.darkGray);
        g2d.drawString("Cat nip consumption rate: " + percentageFormat.format(nipSlider.getValue()), 60, nipSlider.y - 5);
        g2d.drawString("Cat Food: " + (int)game.catFood, 30, 40);
        g2d.drawString("Cat Nip: " + (int)game.catNip, 30, 65);
        g2d.drawString("" + absoluteFormat.format(game.foodConsumed() * 200) + " food and " + absoluteFormat.format(game.nipConsumed() * 200) + " catnip will be eaten every second.", 30, relativeBounds.height - 10);
        
	}

}
