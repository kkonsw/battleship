package client;

import game.*;
import message.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class Client {
	private int serverPort = 7896;
	private String serverName;
	private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    
    private MessageReceiver messageReceiver;
    private MessageSender messageSender;
	
	Map map;
	
	public Client() {
		connectToServer(serverName, serverPort);
		
		messageReceiver = new MessageReceiver(fromServer);
		messageSender = new MessageSender(toServer);
		
        messageReceiver.start();
        messageSender.start();
	}
	
	private void connectToServer(String name, int port) {
		try {
			socket = new Socket(name, port);
			fromServer = new ObjectInputStream(socket.getInputStream());
			toServer = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public ArrayBlockingQueue<Message> getMessagesReceived() {
        return messageReceiver.getMessagesReceived();
    }

    public ArrayBlockingQueue<Message> getMessagesToSend() {
        return messageSender.getMessagesToSend();
    }
    
    public void closeConnection() {
    	if (messageReceiver.isAlive()) {
            messageReceiver.interrupt();
        }

        if (messageSender.isAlive()) {
            messageSender.interrupt();
        }
    	
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }

}
