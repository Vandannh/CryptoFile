package main.java.design;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Controller {
	
	@FXML private AnchorPane signInRoot;
	@FXML private AnchorPane signUpRoot;
	@FXML private AnchorPane homeRoot;
	@FXML private AnchorPane uploadRoot;
	@FXML private AnchorPane filesRoot;

	
	@FXML private Label incorrectSignIn;
	
	@FXML private TextField username;
	@FXML private TextField password;
//	@FXML private TextField usernameSignUp;
//	@FXML private TextField passwordSignUp;
//	@FXML private TextField emailSignUp;
	
//	@FXML private Button signIn;
//	@FXML private Button signUpNow;
//	@FXML private Button signUp;
//	@FXML private Button cancelSignUp;
	
	public void signInButton() throws IOException {
		if(username.getText().equals("r") && password.getText().equals("b")) {
			Parent controllerPane = FXMLLoader.load(Main.class.getResource("filesUI.fxml"));
			signInRoot.getChildren().setAll(controllerPane);
		
		}else {
			incorrectSignIn.setText("User name or password is incorrect. Try again!");
		}
	}
	
	public void signUpNowButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("signUpUI.fxml"));
		signInRoot.getChildren().setAll(controllerPane);
		signInRoot.requestFocus();
	}
	
	public void signUpButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("homePageUI.fxml"));
		signUpRoot.getChildren().setAll(controllerPane);
	}
	
	public void cancelSignUpButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("signInUI.fxml"));
		signUpRoot.getChildren().setAll(controllerPane);
		signUpRoot.requestFocus();
	}
	
	public void upLoadButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("upLoadUI.fxml"));
		homeRoot.getChildren().setAll(controllerPane);
	}
	
	public void cancelUploadButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("homePageUI.fxml"));
		uploadRoot.getChildren().setAll(controllerPane);
	}
	
	public void uploadSuccesfulButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("uploadSuccesfulUI.fxml"));
		uploadRoot.getChildren().setAll(controllerPane);
	}
	

	
}
