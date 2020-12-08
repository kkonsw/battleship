package client;

import message.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class MessageSender extends Thread {
    private ArrayBlockingQueue<Message> messagesToSend;
    private ObjectOutputStream toServer;

    MessageSender(ObjectOutputStream toServer) {
        this.messagesToSend = new ArrayBlockingQueue<Message>(10);
        this.toServer = toServer;
    }

    public ArrayBlockingQueue<Message> getMessagesToSend() {
        return messagesToSend;
    }

    private void send(Message msg) {
        try {
            toServer.writeObject(msg);
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Message msg;
        try {
            while ((msg = messagesToSend.take()) != null) {
                send(msg);       
            }
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
    }
}
