package main.gamestates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import main.Game;
import main.GameThread;
import main.content.Audio;
import main.content.Background;
import main.content.MenuOptions;
import main.content.SpriteSheet;
import main.networking.Connection;
import main.content.weapons.*;

public class ClientState extends GameState {
	//Config
	private static boolean drawHeatmap = false;
	
	//Program-Vars
	private static ArrayList<ArrayList> clients = new ArrayList();
	
	private Background bg = new Background("/Backgrounds/gamebg.png", true);
	
	private Background bgo1 = new Background("/Backgrounds/bgo1.png", true);
	private Background bgo2 = new Background("/Backgrounds/bgo2.png", true);
	private Background bgo3 = new Background("/Backgrounds/bgo3.png", true);
	private Background bgo4 = new Background("/Backgrounds/bgo4.png", true);
	private Background bgo5 = new Background("/Backgrounds/bgo5.png", true);
	
	private Background bgow1 = new Background("/Backgrounds/bgow1.png", true);
	private Background bgow2 = new Background("/Backgrounds/bgow2.png", true);
	private Background bgow3 = new Background("/Backgrounds/bgow3.png", true);
	private Background bgow4 = new Background("/Backgrounds/bgow4.png", true);
	private Background bgow5 = new Background("/Backgrounds/bgow5.png", true);
	
	private long bgoLastChangeTime;
	private int bgoChangeTime = 400;
	private int bgoInt;
	
	private static Rectangle r1;
	private static Line2D l1;
	
	private Background currentBgo;
	private Background currentBgow;
	private Background bgheat = new Background("/Backgrounds/bgheat.png", true);
	
	private SpriteSheet magnumAmmo = new SpriteSheet("/Sprites/44sheet.png",192,47);
	private SpriteSheet m9Ammo = new SpriteSheet("/Sprites/m9sheet.png",208,102);
	private SpriteSheet arAmmo = new SpriteSheet("/Sprites/arsheet.png",300,106);
	private SpriteSheet healthSheet = new SpriteSheet("/Sprites/healthsheet.png",360,24);
	
	private MenuOptions menu = new MenuOptions(new  String[] {"Disconnect","      Quit " }, new int[]{2});
	private Font connectingFont = new Font("Consolas", Font.ITALIC, (int) 120);
	
	private Weapon m9 = new Weapon("/Sprites/m9.png");
	private Weapon magnum = new Weapon("/Sprites/revolver.png");
	private Weapon ar15 = new Weapon("/Sprites/ar.png");
	
	
	public Weapon currentWeapon;
	
	AffineTransform transform = new AffineTransform();
	AffineTransform oldAT;
	
	private static String localip;
	private static String externalip;
	private static String lastShotWeapon;
	
	public static Audio m9Sound = new Audio("/Audio/fire1-1.wav");
	public static Audio magnumSound = new Audio("/Audio/fire2-1.wav");
	public static Audio ar15Sound = new Audio("/Audio/fire3-1.wav");
	public static Audio hurtSound = new Audio("/Audio/hurt.wav");
	public static Audio deathSound = new Audio("/Audio/death.wav");
	public static Audio nearDeathSound = new Audio("/Audio/heartbeat.wav");
	public static Audio healSound = new Audio("/Audio/heal.wav");
	
	private boolean nearDeathPlaying;
	
	private double rc;
	public static int fired = 4;
	private double firedAngle;
	private int firedX;
	private int firedY;
	
	private boolean menuOpen; 
	private boolean init; 
	public static boolean connected; 
	public static boolean checkCol;
	private static boolean hurt;
	
	private long healTime = 2000;
	private long lastHealTime;
	
	private static int mousex;
	private static int mousey;
	public static double xpos;
	public static double ypos;
	private static double drawposy;
	private static double drawposx;
	public static double bufferXpos;
	public static double bufferYpos;
	public static double rotation;
	
	//Spawn posities (x & y)
	private static int[] spawnX = {144,459,823,855,575,107,282,317,321,616,946,1118,
			817,1053,1228,1461,1682,1725,1825,1314,1309,1759,1449,1735,1063};
	private static int[] spawnY = {1833,1604,1883,1527,1124,1261,875,622,146,
			869,898,1326,595,219,622,902,1517,614,1083,1560,1926,1914,196,328,1604};
	
	//Player waardes
	private static boolean firing;
	private static double health = 10;
	private static double speed = 3;
	private static double sprintMultiplier = 1.3;
	
	//classes die niet meteen geinitiate worden
	
	//networking
	public static Connection connection;	
	
	//afbeeldingen
	private static BufferedImage image;
	private static BufferedImage player;
	private static BufferedImage heatmap;
	private static BufferedImage hotbarImage;
	
	private static Socket socket;
	private static BufferedReader serverIn;
	private static PrintWriter serverOut;
	
	public ClientState(GameStateManager gsm) {
		this.gsm = gsm;
	}
	
	public void init() {	
		if (!init) {
			currentBgo = bgo1;
			currentBgow = bgow1;

			bgoLastChangeTime = System.currentTimeMillis();
			int rnd = new Random().nextInt(25);
			xpos = spawnX[rnd];
			ypos = spawnY[rnd];
			bufferXpos = xpos;
			bufferYpos = ypos;
			init = true;
			//Wapen waardes aanpassen
		
			//M9
			m9.maxCapacity = 16;
			m9.currentCapacity = 16;
			m9.damage = 0.5;
			m9.fireDelay = 250;
			m9.reloadDelay = 3350;
			m9.magFed = true;
			m9.automatic = false;
			m9.name = "m9";
		
			//.44 Magnum
			magnum.maxCapacity = 6;
			magnum.currentCapacity = 6;
			magnum.damage = 2;
			magnum.fireDelay = 900;
			magnum.reloadDelay = 5735;
			magnum.magFed = false;
			magnum.automatic = false;
			magnum.setFireSound("/Audio/fire2-1.wav");
			magnum.setFireSound2("/Audio/fire2-2.wav");
			magnum.setReloadSound("/Audio/load2.wav");
			magnum.name = "magnum";

			
			//AR-15
			ar15.maxCapacity = 30;
			ar15.currentCapacity = 30;
			ar15.damage = 1;
			ar15.fireDelay = 150;
			ar15.reloadDelay = 5600;
			ar15.magFed = true;
			ar15.automatic = true;
			ar15.randomDelayMax = 100;
			ar15.setFireSound("/Audio/fire3-1.wav");
			ar15.setFireSound2("/Audio/fire3-2.wav");
			ar15.setReloadSound("/Audio/load3.wav");
			ar15.name = "ar15";
		
			currentWeapon = m9;
		
			//Afbeeldingen inladen
			try {
				player = ImageIO.read(getClass().getResourceAsStream("/Sprites/player.png"));
				image = ImageIO.read(getClass().getResourceAsStream("/Icons/crosshair.png"));
				heatmap = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/heatmap.png"));
				hotbarImage = ImageIO.read(getClass().getResourceAsStream("/Icons/weaponoverlay.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Game.setCursor(20);
				localip = getIp(true);
				externalip = getIp(false);
				
				socket = new Socket(InetAddress.getByName("83.162.43.100"), 4444);	
				serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));//InputReader van de server
				serverOut = new PrintWriter(socket.getOutputStream(), true); //outputWriter naar server
				
				//Stuur data naar de server
				serverOut.println("type:connect|servername:" + Game.serverUser + "|serverpass:" + Game.serverPass + "|"  + addToPayload("machineip", InetAddress.getLocalHost().getHostAddress())); //data van client > server
				String data = serverIn.readLine();
				
				//Parse binnengekomen data
				if (data.indexOf("connected") != -1) {
					System.out.println("Connected!");
					socket.close();
					connection = new Connection();
					connection.start();
				} else if (data.indexOf("wrongpass") != -1) {
					gsm.setState(1);
				} else if (data.indexOf("nosuchserver") != -1) {
				gsm.setState(1);
				} else if (data.indexOf("duplicate") != -1){
					gsm.setState(1);
				}
				
			} catch (IOException e) {
				gsm.setState(gsm.MENUSTATE);
				e.printStackTrace();
			}
		}
	}
	
	public static void updateClient(ArrayList a) {
		clients = a;
	}
	
	public static String addToPayload(String var, String value) {
		return var + ":" + value + "|";
	}
	
	public void update() {
		//Als Schiet ingedrukt wordt gehouden, blijf schieten
		if (health <= 0) {
			deathSound.play();
			respawn();
		} else if (health <= 3.5) {
			if (!nearDeathPlaying) {
				nearDeathSound.play();
				nearDeathPlaying = true;
			}
		} else {
			if (nearDeathPlaying) {
				nearDeathSound.stop();
				nearDeathPlaying = false;
			}
		}
		
		if (health < 20) {
			if (System.currentTimeMillis() - lastHealTime >= healTime) {
				health += 0.5;
				healSound.play();
				lastHealTime = System.currentTimeMillis();
			}
		}

		if (firing && currentWeapon.automatic) {
			currentWeapon.fire();
		}
		double finalspeed = 0;
		double vertical = 0;
		double horizontal = 0;
		
		//Check welke toetsen worden ingedrukt en pas dan snelheid aan
		if (gsm.keysDown.contains(87) ) {
			vertical -= 1;
		}
		if (gsm.keysDown.contains(83)) {
			vertical += 1;
		}
		if (gsm.keysDown.contains(68)) {
			horizontal += 1;
		}
		if (gsm.keysDown.contains(65)) {
			horizontal -= 1;
		}
		//Schuin exat even snel, dus dan de x en y componenten met 1/2Sqrt(2) vermenigvuldigen
		if ((horizontal != 0) && (vertical != 0)) {
			horizontal *= 0.70710678118;
			vertical *= 0.70710678118;
		}
		
		//Als shift dan sprint (hogere snelheid)
		if (gsm.keysDown.contains(16)) {
			finalspeed = speed * sprintMultiplier;;
		} else {
			finalspeed = speed;
		}
		
		//Limits voor positie waar je mag zijn, vermenigvuldigd met Frame-Draw tijd, dat je neit trager bent met een lager framerate
		if ((ypos + vertical * finalspeed * (GameThread.elapsed/10000000)) < 0) {
			ypos = 0;
		} else if ((ypos + vertical * finalspeed * (GameThread.elapsed/10000000)) > 1970) {
			ypos = 1970;
		} else {
			ypos += vertical * finalspeed * (GameThread.elapsed/10000000);
		}
		if ((xpos + horizontal * finalspeed * (GameThread.elapsed/10000000)) < 0) {
			xpos = 0;
		} else if ((xpos + horizontal * finalspeed * (GameThread.elapsed/10000000)) > 1950) {
			xpos = 1950;
		} else {
			xpos += horizontal * finalspeed * (GameThread.elapsed/10000000);
		}
		
		//Checken of je op de volgende positie wel kan staan, voor het X & Y component.
		switch (posCheck((int) xpos,(int) ypos)) {
			case 1:
				bufferXpos = xpos;
				ypos = bufferYpos;
				break;
			case 2:
				bufferXpos = xpos;
				bufferYpos = ypos;
				break;
			case 3:
				xpos = bufferXpos;
				ypos = bufferYpos;
				break;
			case 4:
				xpos = bufferXpos;
				bufferYpos = ypos;
				break;
		}
		//update achtergrondanimatie
		if (System.currentTimeMillis() - bgoLastChangeTime >= bgoChangeTime) {
			bgoLastChangeTime = System.currentTimeMillis();
			switch (bgoInt) {
			case 0: 
				currentBgo = bgo1;
				currentBgow = bgow1;
				bgoInt = 1;
				break;
			case 1: 
				currentBgo = bgo2;
				currentBgow = bgow2;
				bgoInt = 2;
				break;
			case 2: 
				currentBgo = bgo3;
				currentBgow = bgow3;
				bgoInt = 3;
				break;
			case 3: 
				currentBgo = bgo4;
				currentBgow = bgow4;
				bgoInt = 4;
				break;
			case 4: 
				currentBgo = bgo5;
				currentBgow = bgow5;
				bgoInt = 0;
				break;
			}

		}
		
		//Update posities van de achtergronden die meebewegen
		bg.update((int) xpos-640, (int) ypos-512);
		currentBgo.update((int) xpos-640, (int) ypos-512);
		currentBgow.update((int) xpos-640, (int) ypos-512);
		bgheat.update((int) xpos-640, (int) ypos-512);
		
		
		//Positie waar gedrawt meot worden uitrekenen. (bij randen meot camera verschuiven)
		drawposx = 640;
		drawposy = 512;
		if (ypos <= 512) {
			drawposy = ypos;
		} else if (ypos >= 1504) {
			drawposy = 512 + (ypos-1504);
		}
		if (xpos <= 640) {
			drawposx = xpos;
		} else if (xpos >= 1376) {
			drawposx = 640 + (xpos-1376);
		}
		if (checkCol) {
			checkCol = false;
			if (rotation >= 0.065 && rotation <= 3.07) {
				l1 = new Line2D.Float((int) (drawposx+35),(int) (drawposy+27) ,0,(int) (drawposy + ((mousey - drawposy)/(mousex - drawposx))*(0 - drawposx)));
			} else {
				l1 = new Line2D.Float((int) (drawposx+35),(int) (drawposy+27) ,1280,(int) (drawposy + ((mousey - drawposy)/(mousex - drawposx))*(1280 - drawposx)));	
			}
			for (ArrayList<String> a : clients) {
				try {
					if ((!a.get(0).contains(externalip)) && (!a.get(1).contains(localip))) {
						r1 = new Rectangle((int) Integer.parseInt(a.get(2))-bg.x,(int) Integer.parseInt(a.get(3))-bg.y, 65, 65);
						if (l1.intersects(r1))	{
							connection.addInstanceValue("hit", a.get(0)+a.get(1));
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void draw(java.awt.Graphics2D g) {
		
		//Dingen die gedrawd worden:
		
		//Achtergrond
		bg.draw(g);
		
		//De "Standaard" transformatie (verplaatsing, vergroting, rotatie etc.) van de frameBuffer, om hem later te kunnen resetten
		oldAT = g.getTransform();
		
		currentBgow.draw(g);
		
		//Kogel
		if (fired < 4) {
			g.setColor(new Color(220,220-fired*30,40,255-fired*50));
			if (currentWeapon.name == "m9") {
				g.setStroke(new BasicStroke(2));
			} else if (currentWeapon.name == "magnum") {
				g.setStroke(new BasicStroke(4));
			} else if (currentWeapon.name == "ar15") {
				g.setStroke(new BasicStroke(3));
			}
			g.drawLine((int) mousex, (int)mousey, (int)drawposx+35,(int) drawposy+27);
			fired++;
		}
		
		//Andere players
				for (ArrayList<String> a : clients) {
					try {
						if ((!a.get(0).contains(externalip)) && (!a.get(1).contains(localip))) {
						    g.translate(Integer.parseInt(a.get(2)) - bg.x + 35,Integer.parseInt(a.get(3)) - bg.y + 26.5);
						    g.rotate(-Double.parseDouble(a.get(4))-1.75);
						    g.translate(-35, -26.5);
						    g.drawImage(player, 0, 0, null);
						    g.setTransform(oldAT);						    
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				
		//Deze player
		rotation = Math.atan2(drawposx + 35 - mousex, drawposy + 26.5 - mousey);
		g.translate(35 + drawposx, 26.5 + drawposy);
	    g.rotate(-rotation-(0.5*Math.PI));
	    g.translate(-35, -26.5);
	    g.drawImage(player, 0, 0, null);
	    g.setTransform(oldAT);
	    g.setColor(new Color(255,255,255));
	   
		//2e deeel achtergrond (waar men achter kan)
		currentBgo.draw(g);
		
		//menu (als open)
		if (menuOpen) {
			g.setColor(new Color(10,10,10,200));
			g.fillRect(0, 0, 1280, 1024);
			menu.draw(g);
		}
		
		//HUD dingen
		
		g.drawImage(m9.image, 401, 940, null);
		g.drawImage(magnum.image, 577, 940, null);
		g.drawImage(ar15.image, 753, 940, null);
		
		//Munitie, check welk wapen, vraag dan deel van spritesheet horende bij ammo aantal op.
		if (currentWeapon == magnum) {
			g.drawImage(magnumAmmo.getSubImage(0, magnum.currentCapacity), 1040, 932, null);
			g.drawImage(hotbarImage, 577, 936, null);
		} else if (currentWeapon == m9) {
			g.drawImage(hotbarImage, 401, 936, null);
			g.drawImage(m9Ammo.getSubImage(0, m9.currentCapacity), 1032, 892, null);
		} else if (currentWeapon == ar15) {
			g.drawImage(hotbarImage, 753, 936, null);
			g.drawImage(arAmmo.getSubImage(0, ar15.currentCapacity), 950, 892, null);
		}
		
		//health
		g.drawImage(healthSheet.getSubImage(0, (20-(int) (health*2))), 40, 20, null);
		
		//Crosshair
		g.scale(0.2, 0.2);
		g.drawImage(image,(int) (mousex/0.2 - 200),(int) (mousey/0.2 - 200), null);
		g.scale(1,1);
		g.setTransform(oldAT);
		
		if (drawHeatmap) {
			bgheat.draw(g);
			g.setColor(new Color(30,30,30));
			g.fillRect((int) (drawposx+34), (int) (drawposy+26.5), 3, 3);
		}
		
		if (!connected) {
			g.setColor(new Color(0,0,0));
			g.setPaint(new GradientPaint(0,0,new Color(70,70,70),1280, 1024,new Color(30,30,30)));
			g.fillRect(0, 0, 1280, 1024);
			g.setPaint(new GradientPaint(0,0,new Color(160,160,160),1280, 1024,new Color(240,240,240)));
			g.setFont(connectingFont);
			g.drawString("CONNECTING...", 250, 450);
		}
		if (hurt) {
			hurt = false;
			g.setColor(new Color(255,0,0));
			g.fillRect(0,0,1280,1024);
		}
		
	}
	
	public static void shotFired(String weapon) {
		if (weapon.indexOf("m9") != -1) {
			lastShotWeapon = "m9";
			System.out.println(weapon);
			m9Sound.play();
		} else if (weapon.indexOf("magnum") != -1) {
			lastShotWeapon = "magnum";
			System.out.println(weapon);
			magnumSound.play();
		} else if (weapon.indexOf("ar15") != -1) {
			lastShotWeapon = "ar15";
			System.out.println(weapon);
			ar15Sound.play();
		}
	}
	
	public static void hit() {
		hurtSound.play();
		hurt = true;
		if (lastShotWeapon.indexOf("m9") != -1) {
			health -= 0.5;
		} else if (lastShotWeapon.indexOf("magnum") != -1) {
			health -= 2;
		} else if (lastShotWeapon.indexOf("ar15") != -1) {
			health -= 1;
		}
		
	}
	
	public String getIp(boolean local) throws UnknownHostException, IOException {
		if (local) {
			//intern ip wordt via standaard InetAddress methods opgehaald
			return InetAddress.getLocalHost().getHostAddress();
		} else {
			//extern ip wordt via een amazon API opgevraagd
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			return in.readLine();
		}
	}
	
	public void mouseMove(int x, int y) {
		//Als verbonden met server, muispositie doorgeven en updaten
		if (connected) {
			this.mousex = x;
			this.mousey = y;
			connection.mousex = x;
			connection.mousey = y;
			if (menuOpen) {
				menu.mouseHover(x, y);
			} 
		}
	}
	
	public void mouseClicked(int x, int y, int btn) {
		
		//wanneer LMB ingedrukt: Checkt of er geschoten moet worden / het menu moet worden gebruikt.
		if (btn == 1) {
			if (menuOpen) {
				if (menu.currentChoice == 0) {
					try {
						connection.socket.close();
						gsm.setState(1);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (menu.currentChoice == 1) {
					System.exit(0);
				}
			}  else {
				currentWeapon.fire();
				firing = true;
			}
			
		}
	}
	
	public void mouseReleased(int x, int y, int btn) {
		if (btn == 1) {
			firing = false;
		}
	}
		
	public int posCheck(int x, int y) {
		//Heatmap inlanden wanneer nog neit gedaan
		if (heatmap == null) {
			try {
				heatmap = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/heatmap.png"));
			} catch (IOException e) {
			}
		}
		
		//Kleur uit heatmap halen
		Color clrHorizontal =  new Color(heatmap.getRGB(subVal(x+34), subVal((int) (bufferYpos + 26.5))));
		Color clrVertical =  new Color(heatmap.getRGB(subVal((int) bufferXpos+34), subVal((int) (y + 26.5))));
		
		//Luminantie bepalen
		int lumaHorizontal = (int) (0.33*clrHorizontal.getRed() + 0.5*clrHorizontal.getRed() + 0.16*clrHorizontal.getBlue());
		int lumaVertical = (int) (0.33*clrVertical.getRed() + 0.5*clrVertical.getRed() + 0.16*clrVertical.getBlue());
		
		if (lumaHorizontal < 128) {
			if (lumaVertical < 128) {
				return 3;
			} else {
				return 4;
			}
		} else {
			if (lumaVertical < 128) {
				return 1;
			} else {
				return 2;
			}
		}
		
	}
	
	public int subVal(int val) {
		//Returnt de waarde op schaal van de heatmap
		return (val)/48;
	}
	
	public void respawn() {
		deathSound.play();
		health = 20;
		int rnd = new Random().nextInt(26);
		xpos = spawnX[rnd];
		ypos = spawnY[rnd];
		bufferXpos = xpos;
		bufferYpos = ypos;
	}
	
	public void keyPressed(int keyCode) {
		if (keyCode == 82) {
			currentWeapon.reload();
		} else if (keyCode == 27) {
			menuOpen = !menuOpen;
		} else if (keyCode == 49) {
			currentWeapon = m9;
		} else if (keyCode == 50) {
			currentWeapon = magnum;
		} else if (keyCode == 51) {
			currentWeapon = ar15;
		}
		
	}
	
	public void mouseScrolled(double amount) {
		if (amount > 0) {
			if (currentWeapon == m9) {
				currentWeapon = magnum;
			} else if (currentWeapon == magnum) {
				currentWeapon = ar15;
			} else if (currentWeapon == ar15) {
				currentWeapon = m9;
			}
		} else if (amount < 0) {
			if (currentWeapon == m9) {
				currentWeapon = ar15;
			} else if (currentWeapon == magnum) {
				currentWeapon = m9;
			} else if (currentWeapon == ar15) {
				currentWeapon = magnum;
			}
		}
	}
	
	public void keyReleased(int keyCode) {
		
	}


	
}
