package main.content;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Background {
	private BufferedImage image;
	private BufferedImage tmpImage;
	public int scale = 1;
	private boolean subImage;
	public int x;
	public int y;
	public int w = 1280;
	public int h = 1024;
	
	AffineTransform transform = new AffineTransform();
	
	public Background(String s, boolean cutout) {
		if (cutout) {
			subImage = true;
		}
		try {
			image = ImageIO.read(getClass().getResourceAsStream(s));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(int... values) {
		if (values.length == 2) {
			x = values[0];
			y = values[1];
			//checken of de achtergrond wel op eht scherm valt...
			if (x < 0) {
				x = 0;
			}
			if (y < 0) {
				y = 0;
			}
			if (x > 736) {
				x = 736;
			}
			if (y > 991) {
				y = 991;
			}
		}
	}
	
	public void draw(Graphics2D g) {
		transform.setToScale(scale, scale);
		g.setTransform(transform);
		if (subImage) {
			tmpImage = image.getSubimage(x,y,w,h);
			g.drawImage(tmpImage, 0,0,null);
		} else {
			g.drawImage(image, 0,0,null);
		}
		transform.setToScale(1, 1);
		g.setTransform(transform);
	}
}
