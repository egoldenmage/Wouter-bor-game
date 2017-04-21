package main.gamestates;


public class GameState {
	protected GameStateManager gsm;
	protected boolean init;
	
	protected void init() {
		init = true;
	}
	
	protected void update() {
	}
	
	protected void draw(java.awt.Graphics2D g) {
	}
	
	public void keyPressed(int keyCode) {
		
	}
	
	public void keyReleased(int keyCode) {
		
	}
	
	public void mouseMove(int x, int y) {
		
	}
	
	protected void mouseClicked(int x, int y, int btn) {
		
	}
	
	protected void mouseReleased(int x, int y, int btn) {
		
	}
	
	protected void mouseDragged(int x, int y) {
		
	}

	protected void stopAudio() {
	}

	protected void mouseScrolled(double preciseWheelRotation) {
	}
}
