package main.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import main.Game;
import main.gamestates.ClientState;

public class Connection extends Thread{
	private int port = 4444;
	private String ip = "83.162.43.100";
	private int tickrate = 60;
	
	public int mousex;
	public int mousey;
	private int xpos;
	private int ypos;
	private double rotation;
	
	public static String data;
	public static String incoming;
	
	
	public Connection() throws UnknownHostException, IOException {
		
	}
	
	public void run() {
		Socket socket;
		try {
			socket = new Socket(InetAddress.getByName(ip), port);
			BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));//Input van de server
			PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true); //output naar server
			while (true) {
				if (System.currentTimeMillis() % ((int) (1000/tickrate)) == 0) {
					xpos = (int) ClientState.xpos;
					ypos = (int) ClientState.ypos;
					rotation = ClientState.rotation;
					serverOut.println("type:clientdata|" + addToPayload("machineip", InetAddress.getLocalHost().getHostAddress()) + addToPayload("servername", Game.serverUser) + addToPayload("playerposx", Integer.toString(xpos)) + addToPayload("playerposy", Integer.toString(ypos)) + addToPayload("rotation", Double.toString(rotation)));
					String data = serverIn.readLine();
					if (data != null){
						if (data.indexOf("serverdata") != -1) {
							ClientState.updateClient(getServerData(data));
						} else {
							System.out.println(data);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}
	
	public static String addToPayload(String var, String value) {
		return var + ":" + value + "|";
	}
	
	private static ArrayList getServerData(String data) {
		ArrayList<ArrayList> clientsin = new ArrayList<ArrayList>();
		while (data.contains("client:")) {
			String tmpdata = data;
			ArrayList<String> values = new ArrayList<String>();
			tmpdata = data.substring(data.indexOf("client:") + 7, data.indexOf("|", data.indexOf("client:")));
			while (tmpdata.contains("*")) {
				values.add((tmpdata.substring(0,tmpdata.indexOf("*"))));
				tmpdata = tmpdata.substring(tmpdata.indexOf("*")+1);
			}
			values.add(tmpdata);
			clientsin.add(values);
			data = data.substring(data.indexOf("|", data.indexOf("client:")));
		}
		return clientsin;
	}

}
