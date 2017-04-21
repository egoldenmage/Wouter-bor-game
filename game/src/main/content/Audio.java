package main.content;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Audio {
	
	private Clip clip;
	private AudioInputStream inputStream;
	private String src;
	
	public Audio(String src) {
		setClip(src);
	}
	
	public void play() {
		clip.stop();
		try {
			clip = AudioSystem.getClip();
			inputStream = AudioSystem.getAudioInputStream(Audio.class.getResource(src));
			clip.open(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		clip.start();
	}
	
	public void setClip(String s) {
		try {
			clip = AudioSystem.getClip();
			inputStream = AudioSystem.getAudioInputStream(Audio.class.getResource(s));
			clip.open(inputStream);
		} catch (Exception e) {
		}
		this.src = s;
	}
	
	public void loop() {
		clip.stop();
		clip.setFramePosition(0);
		clip.start();
	}
	public void stop() {
		clip.stop();
	}
}