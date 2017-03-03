package main;


import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import main.gamestates.GameStateManager;


@SuppressWarnings("serial")
public class Game extends JPanel {
	
	public static JPanel gamePane = new JPanel();
	private static GameInput input = new GameInput();
	public static GameStateManager gsm = new GameStateManager();
	private static JFrame window = new JFrame("Game");
	
	private static BufferedImage image;
	private static Graphics2D g;
	public static GameThread gamethread;
	
	public static int cursor;
	public static int WIDTH = 1280; //breedte van scherm/bufferedImage
	public static int HEIGHT = 1024; //breedte van scherm/bufferedImage
	
	
	public static void main(String[] args) {
		window.setLocation(0,0); //TODO bepaal waar scherm opent. Standaard 0,0 nu op 2e scherm.
		window.setUndecorated(true); 
		window.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		window.setContentPane(gamePane);//zet de Jpanel als de contentpane (waarop alles wordt gedrawt)
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Zorg dat de game afsluit als je op kruisje kilt (alleen mét borderless mode
		window.setAlwaysOnTop(false);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
		start();
	}
	
	public static void setCursor(int cursorInt) {
		window.setCursor(Cursor.getPredefinedCursor(cursorInt));
		cursor = cursorInt;
	}
	
	public static void start() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(rh);
		gamethread = new GameThread();
		gamePane.addKeyListener(input);
		gamePane.setFocusable(true);
		gamePane.requestFocus();
	}
	
	//TODO handle input/outputvan Server
	
	
	public static void update() {
		gsm.update();
	}
	
	
	public static void draw() {
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, 1280, 1024);
		gsm.draw(g);
	}
	
	public static void drawToScreen() {
		Graphics g2 = gamePane.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}
	
}
