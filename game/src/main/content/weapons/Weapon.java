package main.content.weapons;

import main.content.Audio;

public class Weapon {
	public double damage;
	public boolean magFed;
	
	
	public int fireDelay;
	public int reloadDelay;
	public int maxCapacity;
	public int currentCapacity;
	public long lastFire;
	public long lastReload;
	private Audio fireSound = new Audio("/Audio/fire1.wav");
	private Audio fireSound2 = new Audio("/Audio/fire1.wav");
	private Audio emptySound = new Audio("/Audio/empty.wav");
	private Audio reloadSound = new Audio("/Audio/fire1.wav");
	//TODO cocingsound als niet magfed
	
	public void fire() {
		if ((currentCapacity >= 1) && (System.currentTimeMillis() - lastFire >= fireDelay) && (System.currentTimeMillis() - lastReload >= reloadDelay)) {
			lastFire = System.currentTimeMillis();
			currentCapacity--;
			if (!magFed && currentCapacity > 0) {
				fireSound2.play();
			} else {
				fireSound.play();
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
		if ((currentCapacity == 0) && magFed) {
			currentCapacity = maxCapacity -1;
		} else {
			currentCapacity = maxCapacity;
		}

	}
}
