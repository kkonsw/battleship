package server;

import message.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class Connection extends Thread {
	private int id;
	private ObjectOutputStream toClient;
	private ObjectInputStream fromClient;
	private Socket clientSocket;
	private ArrayBlockingQueue<Message> gameMessages;
	
	public Connection(int id, Socket clientSocket,  ArrayBlockingQueue<Message> gameMessages) {
		this.id = id;
		this.clientSocket = clientSocket;	
		this.gameMessages = gameMessages;
		
	    try {
	    	toClient = new ObjectOutputStream(clientSocket.getOutputStream());
	        fromClient = new ObjectInputStream(clientSocket.getInputStream());
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	void write(Message msg) {
        try {
            toClient.writeObject(msg);
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
	
	public void closeSocket() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
        write(new Message(MessageType.SET_ID, id));
        try {
        	Message msgFromClient;
            while ((msgFromClient = (Message) fromClient.readObject()) != null) {
                gameMessages.add(msgFromClient);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

}
