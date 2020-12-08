package client;

import message.*;

import java.io.ObjectInputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class MessageReceiver extends Thread {
    private ArrayBlockingQueue<Message> messagesReceived;
    private ObjectInputStream fromServer;

    MessageReceiver(ObjectInputStream fromServer) {
        this.messagesReceived = new ArrayBlockingQueue<Message>(10);
        this.fromServer = fromServer;
    }

    public ArrayBlockingQueue<Message> getMessagesReceived() {
        return messagesReceived;
    }

    @Override
    public void run() {
        Message msg;
        try {
            while ((msg = (Message) fromServer.readObject()) != null) {
                messagesReceived.add(msg);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
