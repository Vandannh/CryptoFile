
package application;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.java.Client;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	private static Client client;
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("CryptoFile");
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("signInUI.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			UserInterfaceController uicontroller = (UserInterfaceController)loader.getController();
			client = new Client(uicontroller);
			uicontroller.setStage(primaryStage);
			Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);
			primaryStage.getIcons().add(new Image("https://i.ibb.co/3NR4277/icon.png"));
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent t) {
			    	try {
						client.logout();
					} catch (IOException e) {
						e.printStackTrace();
					}
			        Platform.exit();
			        System.exit(0);
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Client getClient() {
		return client;
	}

	public static void main(String[] args) {
		launch(args);
	}
}