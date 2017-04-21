package main.content.weapons;

import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;

import main.content.Audio;
import main.gamestates.ClientState;

public class Weapon {
	public double damage;
	public boolean magFed;
	public boolean automatic;
	public boolean random = false;
	public String name;
	
	
	public int fireDelay;
	public int reloadDelay;
	public int maxCapacity;
	public int currentCapacity;
	private int randomDelay = 0;
	public int randomDelayMax = 0;
	public long lastFire;
	public long lastReload;
	
	public BufferedImage image;
	private Audio fireSound = new Audio("/Audio/fire1-1.wav");
	private Audio fireSound2 = new Audio("/Audio/fire1-2.wav");
	private Audio emptySound = new Audio("/Audio/empty.wav");
	private Audio reloadSound = new Audio("/Audio/load1.wav");
	
	public Weapon(String src) {
		try {
			image = ImageIO.read(getClass().getResourceAsStream(src));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fire() {
		if ((currentCapacity >= 1) && (System.currentTimeMillis() - lastFire >= fireDelay + randomDelay) && (System.currentTimeMillis() - lastReload >= reloadDelay)) {
			lastFire = System.currentTimeMillis();
			if (randomDelayMax > 0) {
				randomDelay = new Random().nextInt(randomDelayMax);
			}
			currentCapacity--;
			ClientState.checkCol = true;
			ClientState.fired = 0;
			ClientState.connection.addInstanceValue("fired", name);
			if (!magFed && currentCapacity > 0) {
				fireSound2.play();
			} else {
				if (magFed && currentCapacity == 0) {
					fireSound2.play();
				} else {
					fireSound.play();
				}	
			}
		} else if ((currentCapacity == 0)) {
			emptySound.play();
		}
	}
	
	public void setFireSound(String s) {
		fireSound.setClip(s);
	}
	
	public void setFireSound2(String s) {
		fireSound2.setClip(s);
	}
	
	public void setEmptySound(String s) {
		emptySound.setClip(s);
	}
	
	public void setReloadSound(String s) {
		reloadSound.setClip(s);
	}
	
	public void reload() {
		lastReload = System.currentTimeMillis();
		reloadSound.play();
		currentCapacity = maxCapacity;
	}
}
