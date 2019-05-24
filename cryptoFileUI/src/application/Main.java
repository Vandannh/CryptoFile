
package application;
import java.io.IOException;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.java.controller.Controller;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	private static Controller controller = new Controller();
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("CryptoFile");
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("../main/javafx/signInUI.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			UserInterfaceController uicontroller = (UserInterfaceController)loader.getController();
			uicontroller.setStage(primaryStage);
			uicontroller.setController(controller);
			Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);
			primaryStage.getIcons().add(new Image("https://i.ibb.co/3NR4277/icon.png"));
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent t) {
			        Platform.exit();
			        System.exit(0);
			    }
			});
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}
	
	public static Controller getController() {
		return controller;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}