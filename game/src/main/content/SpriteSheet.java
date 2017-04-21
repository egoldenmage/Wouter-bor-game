package main.content;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {
	
	private BufferedImage sheet;
	public int segmentHeight;
	public int segmentWidth;
	
	public SpriteSheet(String src,int w, int h) {
		segmentWidth = w;
		segmentHeight = h;
		try {
			sheet = ImageIO.read(getClass().getResourceAsStream(src));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage getSubImage(int nrx, int nry) {
		BufferedImage subImage;
		if (nry*segmentHeight <= 0) {
			nry = 0;
		} else if (nry*segmentHeight >= sheet.getHeight()) {
			nry = (sheet.getHeight() / segmentHeight) - 1;
		}
		if (nrx*segmentWidth <= 0) {
			nrx = 0;
		} else if (nrx*segmentWidth >= sheet.getWidth()) {
			nrx = (sheet.getWidth() / segmentWidth) - 1;
		}
		subImage = sheet.getSubimage(nrx * segmentWidth, nry * segmentHeight, segmentWidth, segmentHeight);
		return subImage;
	}
}
