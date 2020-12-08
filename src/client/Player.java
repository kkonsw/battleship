package client;

import game.*;

public class Player {
    private Map playerMap;
    private int playerId;

    public Player(int playerId) {
        this.playerMap = new Map();
        this.playerId = playerId;
    }

    public Map getPlayerMap() {
        return playerMap;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}