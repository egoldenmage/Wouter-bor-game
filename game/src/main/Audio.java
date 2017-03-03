package main;

import java.applet.Applet;
import java.applet.AudioClip;

public class Audio {
	
	private AudioClip clip;
	
	public Audio(String src) {
		try {
			clip = Applet.newAudioClip(Audio.class.getResource(src));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		clip.play();
	}
	public void loop() {
		clip.loop();
	}
	public void stop() {
		clip.stop();
	}
}
