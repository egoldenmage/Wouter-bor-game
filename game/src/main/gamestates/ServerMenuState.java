package main.gamestates;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import main.content.MenuOptions;
import main.Audio;
import main.content.Background;

public class ServerMenuState extends GameState {
		
		private Background bg = new Background("/Backgrounds/menu.png");
		private Font titleFont;
		private Audio selectSound = new Audio("/Audio/select.wav");
		private Audio typingRightSound = new Audio("/Audio/select.wav");
		private Audio typingWrongSound = new Audio("/Audio/typingWrongSound.wav");
		private MenuOptions menu = new MenuOptions(new  String[] {"   Set Name:", "Set Password:", "  Start Server", "        Back"}, new int[]{2});
		private Font serverInfoFont = new Font("Calibri", Font.ITALIC, 80);
		private Font serverInfoTypeFont = new Font("Calibri", Font.BOLD, 80);
		
		private double titleScale = 75;
		private boolean growing = true;
		private boolean entering = false;
		private boolean shiftPressed = false;
		
		private String serverName = "";
		private String serverPass = "";
		private int enterState = 0;
		
		public ServerMenuState(GameStateManager gsm) {
			menu.width = 240;
			menu.xPos = 640-(menu.width/2);
			this.gsm = gsm;
		}
		
		public void init() {
			menu.currentChoice = 0;
		}
		
		private void select() {
			selectSound.play();
			if (entering) {
				menu.blinking = false;
				entering = false;
				if (serverName.length() >= 1 && serverPass.length() >= 1) {
					menu.setEnabled(2);
				} else {
					menu.setDisabled(2);
				}
			} else {
			switch (menu.currentChoice) {
			case 0:
				enteringMode(1);
				break;
			case 1:
				enteringMode(2);
				break;
			case 2:
				//gsm.setState(3);
				break;
			case 3:
				back();
				break;
			}
			}
		}
		
		private void back() {
			selectSound.play();
			if (entering) {
				entering = false;
				menu.blinking = false;
			} else {
				gsm.setState(0);
			}
		}
		
		private void enteringMode(int type) {
			menu.blinking = true;
			entering = true;
			enterState = type;
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
			bg.update();
			menu.update();
		}
		
		public void draw(java.awt.Graphics2D g) {
			bg.draw(g);
			menu.draw(g);
			g.setColor(new Color(20,20,20));
			g.setFont(titleFont);
			g.drawString("Tankz", (int) (645-1.8*titleScale),250);			
			if (entering) {
				g.setFont(serverInfoTypeFont);
				if (enterState == 1) {
					g.setColor(new Color(0,0,0, 200));
					g.fillRect(0, 0, 1280, 1024);
					g.setColor(new Color(190,190,190, 160));
					g.drawString("Server name:", 400, 380);
					g.setColor(new Color(160,160,160, 180));
					g.fillRoundRect(200, 400, 880, 80, 20, 20);
					g.setColor(new Color(40,40,40, 180));
					g.drawRoundRect(200, 400, 880, 80, 20, 20);
					g.setColor(new Color(0,0,0,180));
					g.setFont(serverInfoFont);
					if (System.currentTimeMillis() % 1000 <= 500) {
						g.drawString(serverName + "_", (int) 250,465);
					} else {
						g.drawString(serverName, (int) 250,465);
					}
				} else if (enterState == 2) {
					g.setColor(new Color(0,0,0, 200));
					g.fillRect(0, 0, 1280, 1024);
					g.setColor(new Color(190,190,190, 160));
					g.drawString("Server Password:", 400, 380);
					g.setColor(new Color(160,160,160, 180));
					g.fillRoundRect(200, 400, 880, 80, 20, 20);
					g.setColor(new Color(40,40,40, 180));
					g.drawRoundRect(200, 400, 880, 80, 20, 20);
					g.setColor(new Color(0,0,0,180));
					g.setFont(serverInfoFont);
					if (System.currentTimeMillis() % 1000 <= 500) {
						g.drawString(serverPass + "_", (int) 250,465);
					} else {
						g.drawString(serverPass, (int) 250,465);
					}
				}
				
			} else {
				//TODO ook strignsdrawen, maar op andre plek.
			}
		}
		
		public void keyPressed(int keyCode) {
			if (keyCode == KeyEvent.VK_ENTER) {
				select();
			} else if (keyCode == KeyEvent.VK_ESCAPE) {
				back();
			} else if ((keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) && !entering) {
				menu.moveUp();			
			} else if ((keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) && !entering)  {
				menu.moveDown();
			} else if (keyCode == KeyEvent.VK_SHIFT) {
				shiftPressed = true;
			}
			if (entering) {
				if (Character.isAlphabetic((char) keyCode) || Character.isDigit((char) keyCode)){
					if (enterState == 1) { //check welke string wordt geedit
						if (serverName.length() < 14) {
							typingRightSound.play();
							if (shiftPressed) {
								serverName += String.valueOf((char) keyCode).toUpperCase();
							} else {
								serverName += String.valueOf((char) keyCode).toLowerCase();
							}

						} else {
							typingWrongSound.play();
						}
					} else if (enterState == 2) {
						if (serverPass.length() < 14) {
							typingRightSound.play();
							if (shiftPressed) {
								serverPass += String.valueOf((char) keyCode).toUpperCase();
							} else {
								serverPass += String.valueOf((char) keyCode).toLowerCase();
							}
						} else {
							typingWrongSound.play();
						}
					}
				} else if (keyCode == KeyEvent.VK_BACK_SPACE) {
					if (enterState == 1 && serverName.length() >= 1) {
						typingRightSound.play();
						serverName = serverName.substring(0, serverName.length()-1);
					} else if (enterState == 2 && serverPass.length() >= 1) {
						typingRightSound.play();
						serverPass = serverPass.substring(0, serverPass.length()-1);
					} else {
						typingWrongSound.play();
					}
				}
			}
		}
		
		
		public void keyReleased(int keyCode) {
			if (keyCode == KeyEvent.VK_SHIFT) {
				shiftPressed = false;
			}
		}
		
		public void mouseMove(int x,int y) {
			//TODO clickable buttons (checken of op pos, welke pointer en welke currentCount) > halen uit array van menuoptions??
		}
		
		
		public void mouseClicked(int x, int y) {
			//TODO als muis > knop > select
		}

}
