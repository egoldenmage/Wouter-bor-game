package main.gamestates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import main.Audio;
import main.Game;
import main.networking.Connection;

public class ClientState extends GameState {
	private Audio gameSong = new Audio("/Audio/game.wav");
	private Connection connection;
	
	private Socket socket;
	private BufferedReader serverIn;
	private PrintWriter serverOut;
	
	public ClientState(GameStateManager gsm) {

		this.gsm = gsm;
	}
	
	public void init() {
		try {
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
	
	public static String addToPayload(String var, String value) {
		return var + ":" + value + "|";
	}
	
	public void update() {
		
	}
	
	public void draw(java.awt.Graphics2D g) {
	}
	
	public void mouseMove(int x, int y) {
		connection.x = x;
		connection.y = y;
		
	}
	
	public void keyPressed(int keyCode) {
		
	}
	
	public void keyReleased(int keyCode) {
		
	}
	
}
