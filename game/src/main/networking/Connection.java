package main.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection extends Thread {
	Socket serverSocket;
	Connection(Socket socket) {
		Server.connections.add(this);
		this.serverSocket = socket;
	}
	
	public void run() {
		try {
			String data = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			PrintWriter pw = new PrintWriter(serverSocket.getOutputStream(), true);
			while (!serverSocket.isClosed()) {
				try {
					data = br.readLine();
					Server.handleData(data);
					pw.println("");//TODO response > client
					//TODO incoming DATA van client
				} catch (Exception e) {
					System.out.println("User wit IP " + serverSocket.getInetAddress().getHostAddress() + " disconnected.");
					serverSocket.close();
					Server.connections.remove(this);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
