package main.content;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Background {
	private BufferedImage image;
	public int scale = 1;
	AffineTransform transform = new AffineTransform();
	
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
		transform.setToScale(scale, scale);
		g.setTransform(transform);
		g.drawImage(image, 0,0,null);
		transform.setToScale(1, 1);
		g.setTransform(transform);
	}
}
