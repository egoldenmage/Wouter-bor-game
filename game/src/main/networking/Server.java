package main.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Server {
	public static int port = 4444; //de standaard port voor de server
	public static ArrayList<Connection> connections = new ArrayList<Connection>();
	
	public Server(int portToUse) throws IOException {
		if (portToUse > 0 && portToUse < 65535) {
			Server.port = portToUse;
		}
		System.out.println("Starting server...");
		runServer();
	}
	
	public void runServer() throws IOException {
		ServerSocket server = new ServerSocket(port);//Maak een nieuwe server socket aan.
		System.out.println("Started server @");
		System.out.println("local IP:   " + getIp(true));
		System.out.println("ext. IP:    " + getIp(false) + ":" + port);
		System.out.println("");
		
		while (true) {
			Socket socket = server.accept();	//De code staat hier stil, totdat er een verbinding wordt geopend.
			new Connection(socket).start();	//Hier wordt een nieuwe thread gemaakt waarmee dan gepraat kan worden met deze client
			System.out.println("User with IP " + socket.getInetAddress().getHostAddress() + " connected.");
		}
	}
	
	
	
	public static void handleData(String data) {
		System.out.println(data);
		//TODO start return message.
	}
	
	
	//Een method om je externe en interne IP te krijgen, zodat een andere gebruiker weet waarmee hij/zij kan verbinden.
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

	
}
