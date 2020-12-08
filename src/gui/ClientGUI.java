package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;

public class ClientGUI extends Application {
	private Stage primaryStage;
    private BorderPane layout;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Client");
        
        initLayout();
    }

    private void initLayout() {
    	try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Controller.class.getResource("client.fxml"));
            layout = loader.load();

            Scene scene = new Scene(layout);
            primaryStage.setScene(scene);
            primaryStage.show();

            Controller controller = loader.getController();

            scene.getWindow().setOnCloseRequest((WindowEvent event) -> {
            	controller.close();
                Platform.exit();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {
		launch(args);
	}
}
