package server;

import game.*;
import message.*;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ServerHandler extends Thread {
	private ArrayBlockingQueue<Message> gameMessages;
    private ConcurrentHashMap<Integer, Connection> connections;
    private GameState gameState;
    private HashMap<Integer, Map> playersMaps;

    ServerHandler() {
        this.gameMessages = new ArrayBlockingQueue<Message>(10);
        this.connections = new ConcurrentHashMap<>();
        this.gameState = GameState.INIT_STATE;
        this.playersMaps = new HashMap<>();
    }
    
    ArrayBlockingQueue<Message> getGameMessages() {
        return gameMessages;
    }
     
    void addConnection(int id, Connection connectionThread) {
        if (!connections.contains(id))
            connections.put(id, connectionThread);
    }

    void stopConnectionsThreads() {
        connections.forEach((id, connection) -> {
            connection.closeSocket();
            connection.interrupt();
        });
    }
    
    @Override
    public void run() {
        try {
            Message msg;
            while ((msg = gameMessages.take()) != null) {
                handleMessage(msg);
                gameMessages.poll();
            }
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
    }
    
    public void handleMessage(Message clientMsg) {
        switch (clientMsg.getMsgType()) {
            case ID_IS_SET:
                idIsSet(clientMsg);
                break;

            case SHIPS_PLACED:
                shipsPlaced(clientMsg);
                break;

            case WAITING:
                break;

            case SHOT_PERFORMED:
                handle_shot_performed(clientMsg);
                break;
		default:
			break;
        }
    }
    
    private void send(Message answer) {
        connections.get(answer.getPlayerID()).write(answer);
    }

    private void sendBroadcast(Message answer) {
        for (int i = 1; i <= connections.size(); ++i)
            connections.get(i).write(answer);
    }

    private void idIsSet(Message clientMsg) {
    	Message answer = new Message();

        if (gameState == GameState.INIT_STATE) {
            gameState = GameState.WAIT_FOR_SECOND_PLAYER;
        } else {
            gameState = GameState.WAIT_FOR_FIRST_READY;
            answer.setMsgType(MessageType.PLACE_SHIPS);
            sendBroadcast(answer);
        }
    }

    private void shipsPlaced(Message clientMsg) {
    	Message answer = new Message();

        int id = clientMsg.getPlayerID();
        Map clientMap = (Map) clientMsg.getDataObj();
        playersMaps.put(id, clientMap);

        if (gameState == GameState.WAIT_FOR_FIRST_READY) {
            gameState = GameState.WAIT_FOR_SECOND_READY;
        } else {
            gameState = GameState.WAIT_FOR_MOVE;

            answer.setPlayerID(1);
            answer.setMsgType(MessageType.MAKE_MOVE);
            send(answer);

            answer.setPlayerID(2);
            answer.setMsgType(MessageType.WAIT_FOR_MOVE);
            send(answer);
        }
    }

    private void handle_shot_performed(Message clientMsg) {
    	Message answer;

        int activePlayerId = clientMsg.getPlayerID();
        int waitingPlayerId = (activePlayerId) % 2 + 1;
        Coordinates coordinates = new Coordinates((Coordinates) clientMsg.getDataObj());

        int isHit = playersMaps.get(waitingPlayerId).updateMap(coordinates);

        boolean isLoser = (playersMaps.get(waitingPlayerId).countFields(Field.SHIP) == 0);
        if (isLoser) {
            gameState = GameState.END;
            
            answer = new Message(MessageType.WIN, activePlayerId, coordinates);
            send(answer);
            answer = new Message(MessageType.LOSE, waitingPlayerId, coordinates);
            send(answer);

            return;
        }

        if (isHit == 1) {
            answer = new Message(MessageType.HIT_MAKE_MOVE, activePlayerId, coordinates);
            send(answer);

            answer = new Message(MessageType.HIT_WAIT_FOR_MOVE, waitingPlayerId, coordinates);
            send(answer);

            return;
        }
        
        if (isHit == 0) {
        	answer = new Message(MessageType.MISS_WAIT_FOR_MOVE, activePlayerId, coordinates);
        	send(answer);

        	answer = new Message(MessageType.MISS_MAKE_MOVE, waitingPlayerId, coordinates);
        	send(answer);
        	
        	return;
        }
    }
}
