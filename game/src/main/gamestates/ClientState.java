package main.gamestates;

import java.awt.Color;
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

public class ClientState extends GameState {
	
	private Audio gameSong = new Audio("/Audio/game.wav");
	private static ArrayList<ArrayList> clients = new ArrayList();
	private Background bg = new Background("/Backgrounds/gamebg.png");
	private Audio fire1 = new Audio("/Audio/light-gunshot-1.wav");
	
	AffineTransform transform = new AffineTransform();
	AffineTransform oldAT;
	
	private static String localip;
	private static String externalip;
	
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
		try {
			player = ImageIO.read(getClass().getResourceAsStream("/Sprites/player.png"));
			image = ImageIO.read(getClass().getResourceAsStream("/Icons/crosshair.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		bg.scale = 3;
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
		double finalspeed = 0;
		double vertical = 0;
		double horizontal = 0;
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
		if ((horizontal != 0) && (vertical != 0)) {
			horizontal *= 0.70710678118;
			vertical *= 0.70710678118;
		}
		
		if (gsm.keysDown.contains(16)) {
			finalspeed = speed * sprintMultiplier;;
		} else {
			finalspeed = speed;
		}
		
		if ((ypos + vertical * finalspeed * (GameThread.elapsed/10000000)) < 0) {
			ypos = 0;
		} else if ((ypos + vertical * finalspeed * (GameThread.elapsed/10000000)) > main.Game.HEIGHT) {
			ypos = main.Game.HEIGHT;
		} else {
			ypos += vertical * finalspeed * (GameThread.elapsed/10000000);
		}
		if ((xpos + horizontal * finalspeed * (GameThread.elapsed/10000000)) < 0) {
			xpos = 0;
		} else if ((xpos + horizontal * finalspeed * (GameThread.elapsed/10000000)) > main.Game.WIDTH) {
			xpos = main.Game.WIDTH;
		} else {
			xpos += horizontal * finalspeed * (GameThread.elapsed/10000000);
		}
		
		rotation = Math.atan2(xpos + 35 - mousex, ypos + 26.5 - mousey);
	}
	
	public void draw(java.awt.Graphics2D g) {
		//Dingen die gedrawd worden:
		
		//Achtergrond
		bg.draw(g);
		
		//Deze player
		oldAT = g.getTransform();
	    g.translate(35 + xpos, 26.5 + ypos);
	    g.rotate(-rotation-1.75);
	    g.translate(-35, -26.5);
	    g.drawImage(player, 0, 0, null);
	    g.setTransform(oldAT);
		
		//Andere players
		for (ArrayList<String> a : clients) {
			g.setColor(new Color(255,255,255));
			try {
				if ((!a.get(0).contains(externalip)) && (!a.get(1).contains(localip))) {
				    g.translate(35 + Integer.parseInt(a.get(2)), 26.5 + Integer.parseInt(a.get(3)));
				    g.rotate(Double.parseDouble(a.get(4))-1.75);
				    System.out.println(Double.parseDouble(a.get(4)));
				    g.translate(-35, -26.5);
				    g.drawImage(player, 0, 0, null);
				    g.setTransform(oldAT);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
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
	
	public void mouseClicked(int x, int y) {
		fire1.play();
	}
	
	public void keyPressed(int keyCode) {
		
	}
	
	public void keyReleased(int keyCode) {
		
	}
	
}
