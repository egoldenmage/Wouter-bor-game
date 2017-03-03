package main.content;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Background {
	private BufferedImage image;
	
	public Background(String s) {
		try {
			image = ImageIO.read(getClass().getResourceAsStream(s));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(image, 0,0,null);
	}
}
