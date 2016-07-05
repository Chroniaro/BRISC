package com.brisc.BRISC.menu;

import java.awt.Point;
import java.awt.Rectangle;

public class Slider implements MenuObject {
	
	public int x,y,width,height,barWidth;
    public String label;
    public double min, max;
    public int steps;
    

    private double value;
    boolean mouseWasDown;
    boolean dragging;
    int grabX;
    boolean hover;

	public Slider(int x, int y, int width, int height, int barWidth, String label, double startingValue, double min, double max, int steps) {
		
		mouseWasDown = true;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.barWidth = barWidth;
		this.label = label;
		this.min = min;
		this.max = max;
		this.steps = steps;
		setValue(startingValue);
		
	}
	
	public Slider(int x, int y, int width, int height, double min, double max, String label) {
		
		this(x, y, width, height, width / 5, label, min / 2 + max / 2, min, max, (int)Math.min(5, max - min));
		
	}
	
	public Slider(int x, int y, int width) {
		
		this(x, y, width, 20, 0, 1, "");
		
	}

	@Override
	public void update(Point mousePosition, boolean mouseDown) {
		
		hover = getSlider().contains(mousePosition);
		
		if(mouseDown) {
		
			if(!mouseWasDown) {
				
				if(hover) {
					
					dragging = true;
					grabX = mousePosition.x - getSlider().x;
					
				}
				
			} else if(dragging) {
				
				double offset = mousePosition.x - grabX - this.x;
				value = offset / (this.width - barWidth);
				final double stepSize = (double) 1 / (steps - 1);
				final int stepsUp = (int)(value / stepSize);
				value = stepsUp * stepSize;
				value = value > 1 ? 1 : (value < 0 ? 0 : value);
				
			}
		
		} else {
			
			dragging = false;
			
		}
			
		mouseWasDown = mouseDown;
		
	}
	
	public Rectangle getSlider() {
		
		Rectangle r = new Rectangle();
		
		r.width = this.barWidth;
		r.height = this.height;
		r.y = this.y;
		r.x = this.x + (int)Math.round((this.width - this.barWidth) * value);
		
		return r;
		
	}
	
	public void setValue(double value) {
		
		if(value < min)
			value = min;
		if(value > max)
			value = max;
		this.value = (value - min) / (max - min);
		
	}
	
	public double getValue() {
		
		return value * (max - min) + min;
		
	}

}
