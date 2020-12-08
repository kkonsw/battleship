package server;

public enum GameState {
    INIT_STATE,
    WAIT_FOR_SECOND_PLAYER,
    WAIT_FOR_FIRST_READY,
    WAIT_FOR_SECOND_READY,
    WAIT_FOR_MOVE,
    END
}
