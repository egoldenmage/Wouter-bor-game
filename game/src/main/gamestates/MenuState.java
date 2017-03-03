package main.gamestates;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import main.Audio;
import main.content.Background;
import main.content.MenuOptions;

public class MenuState extends GameState {
	private Background bg = new Background("/Backgrounds/menu.png");
	private Audio menuSong = new Audio("/Audio/menu.wav");
	private Audio selectSound = new Audio("/Audio/select.wav");
	private MenuOptions menu = new MenuOptions(new  String[] {" Join Game", "Start Server", "      Quit", "      Help"}, new int[]{});
	
	private Font titleFont;
	
	private double titleScale = 75;
	private boolean growing = true;
	private boolean menuSongPlaying = false;
	
	
	public MenuState(GameStateManager gsm) {
		this.gsm = gsm;

	}
	
	public void init() {
		if (!menuSongPlaying) {
			menuSong.loop();
			menuSongPlaying = true;
		}
	}
	
	public void update() {
		if(growing) {
			if(titleScale <= 85) {
				titleScale += 0.1;
			} else {
				growing = false;
			}
		} else {
			if(titleScale >= 75) {
				titleScale -= 0.1;
			} else {
				growing = true;
			}
		}
		titleFont = new Font("Verdana", Font.BOLD, (int) titleScale);
		menu.update();
		bg.update();
	}
	
	public void draw(java.awt.Graphics2D g) {
		bg.draw(g);
		menu.draw(g);
		g.setColor(new Color(20,20,20));
		g.setFont(titleFont);
		g.drawString("Tankz", (int) (645-1.8*titleScale),250);
	}
	
	public void select() {
		selectSound.play();
		switch (menu.currentChoice) {
		case 0:
			//gsm.setState(1);
			break;
		case 1:
			gsm.setState(2);
			break;
		case 2:
			System.exit(0);
			break;
		}
	}
	
	private void back() {
		System.exit(0);
	}
	
	public void keyPressed(int keyCode) {
		if (keyCode == KeyEvent.VK_ENTER) {
			select();
		} 
		
		if (keyCode == KeyEvent.VK_ESCAPE) {
			back();
		} 
		if ((keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP)) {
			menu.moveUp();			
		} 
		if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN)  {
			menu.moveDown();
		}
	}
	
	public void keyReleased(int keyCode) {
		
	}
}