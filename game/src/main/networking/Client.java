package main.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private static int port = 4444;
	private static String ip = "83.162.43.100";
	public static String data = "ree";
	public static String incoming;
	
	
	public Client() throws UnknownHostException, IOException {
		Socket socket = new Socket(InetAddress.getByName(ip), port);		
		BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));//Input van de server
		PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true); //output naar server
		while (true) {
			serverOut.println(data); //data van client > server
			Server.handleData(serverIn.readLine()); //TODO data van server > client
		}
	}

}
