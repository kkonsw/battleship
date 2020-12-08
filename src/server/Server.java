package server; 

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class Server extends Thread {
	private int serverPort = 7896;
	private ServerSocket serverSocket;
	 private ServerHandler connectionsHandler;
	private int numOfConnected;
	
	public static void main(String[] args) {	
		Server server = new Server();
		server.start();
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(serverPort);
			System.out.println("Server started");
			connectionsHandler = new ServerHandler();
			connectionsHandler.start();
			
			while (true) {			
				Socket s = serverSocket.accept();
				System.out.println("Client connected");
				numOfConnected++;
				
				// temporary restriction for number of players
				if (numOfConnected > 2) {
					s.close();
					continue;
				}
				
				Connection connection = new Connection(numOfConnected, s, connectionsHandler.getGameMessages());
				connection.start();	
				
				connectionsHandler.addConnection(numOfConnected, connection);
			}
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	public void closeServer() {
        try {
            serverSocket.close();
            connectionsHandler.stopConnectionsThreads();
            connectionsHandler.interrupt();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}
