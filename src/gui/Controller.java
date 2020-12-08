package gui;

import client.*;
import message.*;
import game.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.HashMap;

public class Controller {
	private Player player;
	private Client client;
	private ClientHandler msgHandler;
	private int shipLength;
    private int currentNumOfFieldsTaken;
    private int numOfShipsPlaced;
	
    @FXML
    private GridPane yourGrid;

    @FXML
    private GridPane enemyGrid;

    @FXML
    private Label status;

    @FXML
    private Button connectButton;

    @FXML
    private Button finishedButton;

    @FXML
    private MenuButton shipsMenuBar;
    
    public Controller() {
        player = new Player(0);
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setId(Message msg) {
        player.setPlayerId(msg.getPlayerID());
        client.getMessagesToSend().add(new Message(MessageType.ID_IS_SET, msg.getPlayerID()));

        Platform.runLater(() -> {
            status.setText("waiting for game to begin...");
            connectButton.setDisable(true);
        });
    }
    
    public void placeShips() {
        Platform.runLater(() -> {
            shipsMenuBar.setDisable(false);
            status.setText("place ships");
        });
    }
    
    public void makeMove() {
    	Platform.runLater(() -> {
            status.setText("your turn");
            setGridIsDisable(enemyGrid, false);
        });
    }
    
    public void waitForMove() {
        client.getMessagesToSend().add(new Message(MessageType.WAITING, player.getPlayerId()));

        Platform.runLater(() -> { 
            status.setText("opponent's turn");
        });
    }
    
    public void hitMakeMove(Integer row, Integer col) {
        updateGUI("you have hit the enemy, keep firing", enemyGrid, enemyGrid, false, "red",
                row, col);
    }

    public void hitWaitForMove(Integer row, Integer col) {
        client.getMessagesToSend().add(new Message(MessageType.WAITING, player.getPlayerId()));
        updateGUI("you have been shot, still opponent's turn", yourGrid, enemyGrid, true, "red",
                row, col);
    }

    public void missMakeMove(Integer row, Integer col) {
        updateGUI("opponent missed, your turn", yourGrid, enemyGrid, false, "black",
                row, col);
    }

    public void missWaitForMove(Integer row, Integer col) {
        client.getMessagesToSend().add(new Message(MessageType.WAITING, player.getPlayerId()));
        updateGUI("you missed, opponent's turn", enemyGrid, enemyGrid, true, "black",
                row, col);
    }

    public void handleLose(Integer row, Integer col) {
        updateGUI("you lost", yourGrid, enemyGrid, true, "red", row, col);
    }

    public void handleWin(Integer row, Integer col) {
        updateGUI("you won", enemyGrid, enemyGrid, true, "red", row, col);
    }
    
    void updateGUI(String statusVal, GridPane gridToUpdate, GridPane gridToChangeIsDisable, boolean newGridState,
            String color, Integer row, Integer col) {
    		Platform.runLater(() -> {
    			status.setText(statusVal);

    			gridToUpdate.getChildren().get(row * 10 + col).setStyle("-fx-background-color: " + color);
    			setGridIsDisable(gridToChangeIsDisable, newGridState);
    		});
    }
    
    private Integer getShipLength(MenuItem menuItem) {
        HashMap<String, Integer> tmp = new HashMap<>();
        tmp.put("Carrier[Size 5]", 5);
        tmp.put("Battleship[Size 4]", 4);
        tmp.put("Cruiser[Size 3]", 3);
        tmp.put("Submarine[Size 3]", 3);
        tmp.put("Destroyer[Size 2]", 2);

        return tmp.get(menuItem.getText());
    }
    
    private void setGridIsDisable(GridPane gridPane, boolean isGridDisable) {
        for (Node node : gridPane.getChildren()) {
            node.setDisable(isGridDisable);
        }
    }
    
    @FXML
    private void initialize() {
        finishedButton.setDisable(true);
        shipsMenuBar.setDisable(true);
        status.setText("not connected");
    }
    
    @FXML
    private void connect() {
    	client = new Client();
    	status.setText("connecting...");
    	
    	msgHandler = new ClientHandler(this);
        msgHandler.start();
    }
    
    @FXML
    private void handleFinishedButton() {
        Message answer = new Message(MessageType.SHIPS_PLACED, player.getPlayerId(), player.getPlayerMap());
        client.getMessagesToSend().add(answer);

        status.setText("waiting...");
        finishedButton.setDisable(true);
        setGridIsDisable(yourGrid, true);
    }    

    @FXML
    private void handleMenuItemSelected(ActionEvent event) {
        shipsMenuBar.setDisable(true);
        setGridIsDisable(yourGrid, false);
        MenuItem menuItem = (MenuItem) event.getSource();
        menuItem.setDisable(true);
        shipLength = getShipLength(menuItem);
    }

    @FXML
    private void handleEnemyGridCellButtonFired(ActionEvent event) {
    	setGridIsDisable(enemyGrid, false);

        Node node = (Node) event.getSource();

        Coordinates shotCoordinates = new Coordinates(GridPane.getRowIndex(node), GridPane.getColumnIndex(node));
        Message shotMsg = new Message(MessageType.SHOT_PERFORMED, player.getPlayerId(), shotCoordinates);

        client.getMessagesToSend().add(shotMsg);
    }

    @FXML
    private void handleYourGridCellButtonFired(ActionEvent event) {
        Node node = (Node) event.getSource();
        node.setDisable(true);
        node.setStyle("-fx-background-color: deepskyblue");
        ++currentNumOfFieldsTaken;

        player.getPlayerMap().setField(GridPane.getRowIndex(node), GridPane.getColumnIndex(node), Field.SHIP);

        if (currentNumOfFieldsTaken == shipLength) {
            currentNumOfFieldsTaken = 0;
            ++numOfShipsPlaced;

            shipsMenuBar.setDisable(false);
            setGridIsDisable(yourGrid, true);

            if (numOfShipsPlaced == 5) {
                shipsMenuBar.setDisable(true);
                finishedButton.setDisable(false);
            }
        }
    }
    
    public void close() {
        if (client != null) {
            client.closeConnection();
        }

        if (msgHandler != null && msgHandler.isAlive()) {
            msgHandler.interrupt();
        }
    }
}
