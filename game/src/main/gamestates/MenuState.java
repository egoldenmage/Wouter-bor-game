package main.gamestates;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import main.Game;
import main.content.Audio;
import main.content.Background;
import main.content.MenuOptions;

public class MenuState extends GameState {
	private Background bg = new Background("/Backgrounds/menu.png", false);
	private Background bgo1 = new Background("/Backgrounds/bgob1.png", false);
	private Background bgo2 = new Background("/Backgrounds/bgob2.png", false);
	private Background bgo3 = new Background("/Backgrounds/bgob3.png", false);
	private Background bgo4 = new Background("/Backgrounds/bgob4.png", false);
	private Background bgo5 = new Background("/Backgrounds/bgob5.png", false);
	
	public Audio menuSong = new Audio("/Audio/menu.wav");
	private Audio selectSound = new Audio("/Audio/select.wav");
	private MenuOptions menu = new MenuOptions(new  String[] {" Join Game", "Start Server", "      Quit", "      Help"}, new int[]{});
	
	private Font titleFont;
	private double titleScale = 75;
	private boolean growing = true;
	private boolean menuSongPlaying = false;
	private Background currentBgo;
	private long bgoLastChangeTime;
	private int bgoChangeTime = 400;
	private int bgoInt;

	
	
	public MenuState(GameStateManager gsm) {
		this.gsm = gsm;

	}
	
	public void init() {
		currentBgo = bgo1;
		init = true;
		if (!menuSongPlaying) {
			menuSong.loop();
			menuSongPlaying = true;
		}
	}
	
	public void update() {
		if (System.currentTimeMillis() - bgoLastChangeTime >= bgoChangeTime) {
			bgoLastChangeTime = System.currentTimeMillis();
			switch (bgoInt) {
			case 0: 
				currentBgo = bgo1;
				bgoInt = 1;
				break;
			case 1: 
				currentBgo = bgo2;
				bgoInt = 2;
				break;
			case 2: 
				currentBgo = bgo3;
				bgoInt = 3;
				break;
			case 3: 
				currentBgo = bgo4;
				bgoInt = 4;
				break;
			case 4: 
				currentBgo = bgo5;
				bgoInt = 0;
				break;
			}

		}
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
		currentBgo.draw(g);
		menu.draw(g);
		g.setColor(new Color(20,20,20));
		g.setFont(titleFont);
		g.drawString("Gunfighters", (int) (525-1.8*titleScale),250);
	}
	
	public void stopAudio() {
		menuSong.stop();
	}
	
	public void select() {
		selectSound.play();
		switch (menu.currentChoice) {
		case 0:
			gsm.setState(1);
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
	
	public void mouseMove(int x, int y) {
		if (menu.mouseHover(x, y) == true) {
			if (Game.cursor != 12) {
				menu.changeSound.play();
			}
			Game.setCursor(12);
		} else {
			Game.setCursor(0);
		}
	}
	
	public void mouseClicked(int x, int y, int btn) {
		if (menu.mouseHover(x, y) == true) {
			select();
		}
	}
}
