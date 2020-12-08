package message;

import game.*;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable {
	private MessageType msgType;
	private int playerID;
	private Object dataObj;
	
	public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
    
    public Object getDataObj() {
        return dataObj;
    }

    public Message(MessageType msgType, int playerID, game.Map map) {
        this.msgType = msgType;
        this.playerID = playerID;
        this.dataObj = new Map(map);
    }

    public Message(MessageType msgType, int playerID, Coordinates coordinates) {
        this.msgType = msgType;
        this.playerID = playerID;
        this.dataObj = new Coordinates(coordinates);
    }

    public Message(MessageType msgType, int playerID) {
        this.msgType = msgType;
        this.playerID = playerID;
    }

	public Message() {
	}
}
