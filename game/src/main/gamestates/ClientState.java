package main.gamestates;

import java.awt.geom.AffineTransform;
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

import javax.imageio.ImageIO;

import main.Game;
import main.GameThread;
import main.content.Audio;
import main.content.Background;
import main.networking.Connection;
import main.content.weapons.*;

public class ClientState extends GameState {
	
	private Audio gameSong = new Audio("/Audio/game.wav");
	private static ArrayList<ArrayList> clients = new ArrayList();
	private Background bg = new Background("/Backgrounds/gamebg.png", true);
	private Background bgoverlay = new Background("/Backgrounds/bgoverlay.png", true);
	private Audio fire1 = new Audio("/Audio/light-gunshot-1.wav");
	private Weapon m9 = new Weapon();
	private Weapon magnum = new Weapon();
	private Weapon ar15 = new Weapon();
	
	private Weapon currentWeapon;
	
	AffineTransform transform = new AffineTransform();
	AffineTransform oldAT;
	
	private static String localip;
	private static String externalip;
	private static boolean firing;
	
	private static int mousex;
	private static int mousey;
	public static double xpos = 500;
	public static double ypos = 500;
	public static double rotation;
	
	private static double speed = 3;
	private static double sprintMultiplier = 1.3;
	
	//classes die niet meteen geinitiate worden
	private static Connection connection;	
	private static BufferedImage image;
	private static BufferedImage player;
	private static Socket socket;
	private static BufferedReader serverIn;
	private static PrintWriter serverOut;
	
	public ClientState(GameStateManager gsm) {
		this.gsm = gsm;
	}
	
	public void init() {
		//Wapen waardes aanpassen
		
		//M9
		m9.maxCapacity = 16;
		m9.currentCapacity = 16;
		m9.damage = 0.5;
		m9.fireDelay = 250;
		m9.reloadDelay = 2000;
		m9.magFed = true;
		
		//.44 Magnum
		magnum.maxCapacity = 6;
		magnum.currentCapacity = 6;
		magnum.damage = 3;
		magnum.fireDelay = 900;
		magnum.reloadDelay = 5735;
		magnum.magFed = false;
		magnum.setFireSound("/Audio/fire2-1.wav");
		magnum.setFireSound2("/Audio/fire2-2.wav");
		magnum.setReloadSound("/Audio/load2.wav");
		
		//AR-15
		ar15.maxCapacity = 31;
		ar15.currentCapacity = 31;
		ar15.damage = 1;
		ar15.fireDelay = 200;
		ar15.reloadDelay = 3000;
		ar15.magFed = true;
		//ar15.setFireSound("/Audio/fire2-1.wav");
		ar15.setReloadSound("/Audio/load2.wav");
		
		currentWeapon = magnum;
		
		//Afbeeldingen inladen
		try {
			player = ImageIO.read(getClass().getResourceAsStream("/Sprites/player.png"));
			image = ImageIO.read(getClass().getResourceAsStream("/Icons/crosshair.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Game.setCursor(20);
			localip = getIp(true);
			externalip = getIp(false);
			socket = new Socket(InetAddress.getByName("83.162.43.100"), 4444);	
			serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));//Input van de server
			serverOut = new PrintWriter(socket.getOutputStream(), true); //output naar server
			serverOut.println("type:connect|servername:" + Game.serverUser + "|serverpass:" + Game.serverPass + "|"  + addToPayload("machineip", InetAddress.getLocalHost().getHostAddress())); //data van client > server
			String data = serverIn.readLine();
			if (data.indexOf("connected") != -1) {
				System.out.println("Connected!");
				gameSong.loop();
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
			e.printStackTrace();
		}
	}
	
	public static void updateClient(ArrayList a) {
		clients = a;
	}
	
	public static String addToPayload(String var, String value) {
		return var + ":" + value + "|";
	}
	
	public void update() {
		if (firing) {
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
		bg.update((int) xpos-640, (int) ypos-512);
		bgoverlay.update((int) xpos-640, (int) ypos-512);
	}
	
	public void draw(java.awt.Graphics2D g) {
		//Dingen die gedrawd worden:
		
		//Achtergrond
		bg.draw(g);

		oldAT = g.getTransform();
		
		//Als "camera" bij rand komt, player laten bewegen ipv camera.
		double drawposx = 640;
		double drawposy = 512;
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
		rotation = Math.atan2(drawposx + 35 - mousex, drawposy + 26.5 - mousey);
		g.translate(35 + drawposx, 26.5 + drawposy);
	    g.rotate(-rotation-1.6);
	    g.translate(-35, -26.5);
	    g.drawImage(player, 0, 0, null);
	    g.setTransform(oldAT);
		
		//Andere players
		for (ArrayList<String> a : clients) {
			try {
				if ((!a.get(0).contains(externalip)) && (!a.get(1).contains(localip))) {
					int clientx = 0;
					int clienty = 0;
					if ((Integer.parseInt(a.get(2)) >= xpos - 640) && (Integer.parseInt(a.get(2)) <= xpos + 640)) { 
						if ((Integer.parseInt(a.get(3)) >= ypos - 512) && (Integer.parseInt(a.get(2)) <= ypos + 512)) {
							//g.drawImage(player,Integer.parseInt(a.get(2)) - bg.x,Integer.parseInt(a.get(3)) - bg.y,null);
						    g.translate(Integer.parseInt(a.get(2)) - bg.x,Integer.parseInt(a.get(3)) - bg.y);
						    g.rotate(Double.parseDouble(a.get(4))-1.75);
						    g.translate(-35, -26.5);
						    g.drawImage(player, 0, 0, null);
						    g.setTransform(oldAT);						    
						}
					}

				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		//2e deeel achtergrond (waar ej achter kunt)
		bgoverlay.draw(g);
		
		
		//UI dingen
		
		//Crosshair
		g.scale(0.2, 0.2);
		g.drawImage(image,(int) (mousex/0.2 - 100),(int) (mousey/0.2 - 100), null);
		g.scale(1,1);
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
		this.mousex = x;
		this.mousey = y;
		connection.mousex = x;
		connection.mousey = y;
	}
	
	public void mouseClicked(int x, int y, int btn) {
		if (btn == 1) {
			firing = true;
		}
	}
	
	public void mouseReleased(int x, int y, int btn) {
		if (btn == 1) {
			firing = false;
		}
	}
	
	public void keyPressed(int keyCode) {
		if (keyCode == 82) {
			currentWeapon.reload();
		}
		
	}
	
	public void keyReleased(int keyCode) {
		
	}
	
}
