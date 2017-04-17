package main.content;

import java.awt.Color;
import java.awt.Font;

public class MenuOptions {
	public String[] options = {};
	private int[] disabledOptions;
	
	public Audio changeSound = new Audio("/Audio/change.wav");
	private Font menuFont = new Font("Calibri", Font.PLAIN, 38);
	public boolean blinking = false;
	
	public int currentChoice = 0;

	public int width = 205;
	public int height= 40;
	public int xPos = 538;
	public int yPos = 370;
	public int ySpacing = 60;
	
	public MenuOptions(String[] optionsArray, int[] disabledOptionsArray) {
		this.disabledOptions = new int[optionsArray.length];
		this.disabledOptions = disabledOptionsArray;
		this.options = optionsArray;
	}
	
	
	public void update() {
	}
	
	public void draw(java.awt.Graphics2D g) {
		g.setFont(menuFont);
		for (int i = 0; i < options.length; i++) {
			if (i == currentChoice) {
				g.setColor(new Color(120,120,120,200));
				g.fillRoundRect(xPos, (yPos + i * ySpacing), width, height, 8, 8);
				if (System.currentTimeMillis() % 400 <= 200 && blinking) {
					g.setColor(new Color(250,250,250, 180));
				} else {
					g.setColor(new Color(40,40,40, 180));
				}
				int[] x = {xPos-20,xPos-40,xPos-40};
				int[] y = {yPos+20+i*ySpacing,yPos+30+i*ySpacing,yPos +10+i*ySpacing};
				g.fillPolygon(x, y, 3);
				g.setColor(new Color(70,70,70));
			} else {
				g.setColor(new Color(50,50,50, 220));
				for (int j = 0; j < disabledOptions.length; j++) {
					if (disabledOptions[j] == i) {
						g.setColor(new Color(50,50,50,60));
					}
				}
				g.fillRoundRect(xPos, (yPos + i * ySpacing), width, height, 8, 8);
				g.setColor(new Color(140,140,140));
				for (int j = 0; j < disabledOptions.length; j++) {
					if (disabledOptions[j] == i) {
						g.setColor(new Color(140,140,140, 200));
					}
				}
			}
			g.drawString(options[i], xPos+10, (yPos+ 30 + i * ySpacing));
		}
	}
	
	public void setDisabled(int toDisable) {
		disabledOptions[disabledOptions.length - 1] = toDisable;
	}
	
	public void setEnabled(int toEnable) {
		for (int j = 0; j < disabledOptions.length; j++) {
			if (disabledOptions[j] == toEnable) {
				disabledOptions[j] = -1;
			}
		}
	}
	
	public void moveUp() {
			changeSound.play();
			if (currentChoice == 0) {
				currentChoice = options.length-1;
			} else {
				currentChoice--;
			}
			for (int j = 0; j < disabledOptions.length; j++) {
				if (disabledOptions[j] == currentChoice) {
					moveUp();
				}
			}
			
	}
	
	public boolean mouseHover(int x, int y) {
		for (int i = 0; (i < options.length); i++) {

			if ((x >= xPos) && x <= xPos + width) {
				if ((y >= (yPos + i*ySpacing)) && (y <= yPos + i*ySpacing + height)) {
					for (int j = 0; j < disabledOptions.length; j++) {
						if (disabledOptions[j] == i) {
							return false;
						}
					}
					currentChoice = i;
					return true;
				}
			}
		}
		return false;
	}
	
	public void moveDown() {
		changeSound.play();
		if (currentChoice == options.length-1) {
			currentChoice = 0;
		} else {
			currentChoice++;
		}
		for (int j = 0; j < disabledOptions.length; j++) {
			if (disabledOptions[j] == currentChoice) {
				moveDown();
			}
		}
	}
}
