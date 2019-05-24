package application;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.controller.*;

public class UserInterfaceController{
	@FXML private AnchorPane signInRoot, signUpRoot, homeRoot, uploadRoot, filesRoot, succesfulRoot;
	@FXML private Label incorrectSignIn;
	@FXML private TextField username, password, usernameSignUp, passwordSignUp, emailSignUp, repasswordSignUp;
	@FXML private Button signIn, signUpNow, signUp, cancelSignUp, uploadButton, removeUploadFileButton, publicDirectoryUploadButton, privateDirectoryUploadButton, privateFilesButton, publicFilesButton;
	@FXML private Pane uploadList, downloadFileList;
	@FXML private Label usernameError, passwordError, emailError, repasswordError, uploadFail, fileToUpload, progressLabel;
	@FXML private ScrollPane scrollUploadList, scrollFileList;
	@FXML private ToggleGroup toggleGroupUpload;
	@FXML private ProgressBar progressbar;
//	@FXML private ListView<String> uploadFileList = new ListView<String>();
	private Controller controller;
	private Stage stage;
	private double layoutX = 30.0, layoutY = 19.0;
	private File file;
	private boolean publicDirectory, privateDirectory, selected=false;
	private ListView<String> listView;
	
	
	
	@FXML
	public void initialize() {
		if(filesRoot!=null) {
			setFileList();
			controller = Main.getController();
			double procentage = Math.round(controller.getUsedMemoryPercentage()*10)/10.0;
			progressbar.setProgress(procentage);
			progressLabel.setText(Double.toString(procentage)+"/5.0 GB used");
		}
		else if(uploadRoot!=null) {
			uploadFail.setAlignment(Pos.CENTER);
		}
	}
	
	@FXML
	public void signInButton() throws IOException {
		controller = Main.getController();
		if(controller.login(username.getText(), password.getText())) {
			Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/homePageUI.fxml"));
			signInRoot.getChildren().setAll(controllerPane);
		}else {
			incorrectSignIn.setText("Username/password is incorrect. Try again!");
		}
	}
	
	@FXML
	public void signOutButton() throws IOException {
		controller = Main.getController();
		if(controller.logout()) {
			Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/signInUI.fxml"));
			if(succesfulRoot==null)
				homeRoot.getChildren().setAll(controllerPane);
			else
				succesfulRoot.getChildren().setAll(controllerPane);
			System.out.println("Logged out");
		}
	}

	@FXML
	public void unregisterButton() throws IOException {
		controller = Main.getController();
		if(controller.unregisterUser()) {
			Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/signInUI.fxml"));
			homeRoot.getChildren().setAll(controllerPane);
			System.out.println("Unregistered");
		}
	}

	@FXML
	public void signUpButton() throws IOException, NoSuchAlgorithmException {
		controller = Main.getController();
		ArrayList<String> messages = controller.register(usernameSignUp.getText(), emailSignUp.getText(), passwordSignUp.getText());
		if(messages.get(0).equals("User created")) {
			if(controller.login(usernameSignUp.getText(), passwordSignUp.getText())) {
				Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/homePageUI.fxml"));
				signUpRoot.getChildren().setAll(controllerPane);
			}else {
				Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/signInUI.fxml"));
				signUpRoot.getChildren().setAll(controllerPane);
			}
		}
		else {
			usernameError.setText(messages.get(0));
			passwordError.setText(messages.get(2));
			emailError.setText(messages.get(1));
		}
	}

	@FXML
	public void chooseFileButton() throws IOException{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			fileToUpload.setText(file.getName());
			fileToUpload.setLayoutX(layoutX);
			fileToUpload.setLayoutY(layoutY);
			removeUploadFileButton.setVisible(true);
		}
	}

	@FXML
	public void uploadSuccesfulButton() throws IOException {
		controller = Main.getController();
		if(file != null) {
			if(selected) {
				String message = controller.uploadFile(file, getChosenDirectory());
				if(!message.contains("has been uploaded"))
					uploadFail.setText(message);
				else {
					Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/uploadSuccesfulUI.fxml"));
					uploadRoot.getChildren().setAll(controllerPane);
				}
			}
			else 
				uploadFail.setText("Please select a directory to upload into");
		}
	}
	
	public void returnToHomePageFromSuccessfulUploadButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/homePageUI.fxml"));
		succesfulRoot.getChildren().setAll(controllerPane);
	}
	

	@FXML
	public void fileButton() throws IOException{
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/filesUI.fxml"));
		homeRoot.getChildren().setAll(controllerPane);
	}

	@FXML
	public void returnToHomePageFromFilesButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/homePageUI.fxml"));
		filesRoot.getChildren().setAll(controllerPane);
	}

	@FXML
	public void signUpNowButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/signUpUI.fxml"));
		signInRoot.getChildren().setAll(controllerPane);
	}

	@FXML
	public void cancelSignUpButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/signInUI.fxml"));
		signUpRoot.getChildren().setAll(controllerPane);
	}

	@FXML
	public void uploadButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/upLoadUI.fxml"));
		homeRoot.getChildren().setAll(controllerPane);
	}

	@FXML
	public void cancelUploadButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("../main/javafx/homePageUI.fxml"));
		uploadRoot.getChildren().setAll(controllerPane);
	}
	@FXML
	public void removeUploadFile() {
		fileToUpload.setText("");
		removeUploadFileButton.setVisible(false);
		file=null;
	}
	
	@FXML
	public void choosePublicDirectory() {
		selected=true;
		publicDirectory=true;
		privateDirectory=false;
		publicDirectoryUploadButton.setStyle("-fx-background-color: rgb(90, 51, 103);");
		publicDirectoryUploadButton.setTextFill(Color.WHITE);
		privateDirectoryUploadButton.setStyle("-fx-border-color: rgb(90, 51, 103); -fx-background-color: rgb(255,255,255); -fx-border-radius: 3;");
		privateDirectoryUploadButton.setTextFill(Color.web("rgb(90, 51, 103)"));
	}
	
	@FXML
	public void choosePrivateDirectory() {
		selected=true;
		privateDirectory=true;
		publicDirectory=false;
		privateDirectoryUploadButton.setStyle("-fx-background-color: rgb(90, 51, 103);");
		privateDirectoryUploadButton.setTextFill(Color.WHITE);
		publicDirectoryUploadButton.setStyle("-fx-border-color: rgb(90, 51, 103); -fx-background-color: rgb(255,255,255); -fx-border-radius: 3;");
		publicDirectoryUploadButton.setTextFill(Color.web("rgb(90, 51, 103)"));
	}
	@FXML
	public void deleteFileButton(){
		String filename = listView.getSelectionModel().getSelectedItem()+".enc";
		String result = controller.deleteFile(getChosenDirectory(), filename);
		Label resultLabel = new Label(result);
		resultLabel.setFont(Font.font("Helvetica Neue Bold", 14.0));
		resultLabel.setLayoutY(435);
		resultLabel.setLayoutX(50);
		resultLabel.setAlignment(Pos.CENTER);
		filesRoot.getChildren().add(resultLabel);
		System.out.println(filename+" - "+getChosenDirectory());
	}
	@FXML
	public void downloadButton(){
		controller = Main.getController();
		String filename = listView.getSelectionModel().getSelectedItem().split(" ")[0]+".enc";
		String result = controller.downloadFile(getChosenDirectory(), filename);
		Label resultLabel = new Label(result);
		resultLabel.setFont(Font.font("Helvetica Neue Bold", 14.0));
		resultLabel.setLayoutY(435);
		resultLabel.setLayoutX(50);
		resultLabel.setAlignment(Pos.CENTER);
		filesRoot.getChildren().add(resultLabel);
		System.out.println(filename+" - "+getChosenDirectory());
	}
	@FXML
	public void setFileList() {
		selectPrivateFilesButton();
	}
	@FXML
	public void selectPublicFilesButton() {
		publicDirectory=true;
		privateDirectory=false;
		publicFilesButton.setStyle("-fx-background-color: rgb(90, 51, 103);");
		publicFilesButton.setTextFill(Color.WHITE);
		privateFilesButton.setStyle("-fx-border-color: rgb(90, 51, 103); -fx-background-color: rgb(255,255,255); -fx-border-radius: 3;");
		privateFilesButton.setTextFill(Color.web("rgb(90, 51, 103)"));
		setFileList("public");
	}
	@FXML
	public void selectPrivateFilesButton() {
		privateDirectory=true;
		publicDirectory=false;
		privateFilesButton.setStyle("-fx-background-color: rgb(90, 51, 103);");
		privateFilesButton.setTextFill(Color.WHITE);
		publicFilesButton.setStyle("-fx-border-color: rgb(90, 51, 103); -fx-background-color: rgb(255,255,255); -fx-border-radius: 3;");
		publicFilesButton.setTextFill(Color.web("rgb(90, 51, 103)"));
		setFileList("private");
	}
	
	
	public void setStage(Stage stage) {
		this.stage=stage;
	}
	public void setController(main.java.controller.Controller controller) {
		this.controller=controller;
	}
	private String getChosenDirectory() {
		if(privateDirectory) return "private";
		return "public";
	}
	
	private void setFileList(String directory) {
		controller = Main.getController();
		String files = controller.getFiles(directory);
		listView = new ListView<String>();
		listView.setPrefHeight(200);
		listView.setPrefWidth(400);
		if(listView.getItems().size()>0)
			for(int i=listView.getItems().size();i>=0;i--) {
				listView.getItems().remove(i);
			}
		if(!files.isEmpty()) {
			for(String file : files.split("\n")) {
				String filename = file.split("\t\t")[0];
				listView.getItems().add(filename.replace(".enc", ""));
			}
		}
		downloadFileList.getChildren().add(listView);
	}
}
