package client;

import game.*;
import message.*;
import gui.*;

import java.util.concurrent.ArrayBlockingQueue;

public class ClientHandler extends Thread {
	 private Controller controller;
	 private ArrayBlockingQueue<Message> messagesReceived;
	 
	 public ClientHandler(Controller controller) {
	        this.controller = controller;
	        messagesReceived = controller.getClient().getMessagesReceived();
	 }
	 
	 @Override
	 public void run() {
		 Message msg;
	        try {
	            while ((msg = messagesReceived.take()) != null) {
	                Coordinates coordinates = (Coordinates) msg.getDataObj();
	                Integer row = (coordinates != null) ? coordinates.getRow() : null;
	                Integer col = (coordinates != null) ? coordinates.getCol() : null;

	                switch (msg.getMsgType()) {
	                    case SET_ID:
	                    	controller.setId(msg);
	                        break;
	                    case PLACE_SHIPS:
	                    	controller.placeShips();
	                        break;
	                    case MAKE_MOVE:
	                    	controller.makeMove();
	                        break;
	                    case WAIT_FOR_MOVE:
	                    	controller.waitForMove();
	                        break;
	                    case HIT_MAKE_MOVE:
	                    	controller.hitMakeMove(row, col);
	                        break;
	                    case HIT_WAIT_FOR_MOVE:
	                    	controller.hitWaitForMove(row, col);
	                        break;
	                    case MISS_MAKE_MOVE:
	                    	controller.missMakeMove(row, col);
	                        break;
	                    case MISS_WAIT_FOR_MOVE:
	                    	controller.missWaitForMove(row, col);
	                        break;
	                    case LOSE:
	                    	controller.handleLose(row, col);
	                        break;
	                    case WIN:
	                    	controller.handleWin(row, col);
	                        break;
					default:
						break;
	                }
	            }
	        } catch (InterruptedException e) {
	        	e.printStackTrace();
	        }
	 }
}
