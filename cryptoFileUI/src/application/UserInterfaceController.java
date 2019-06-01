package application;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javafx.application.Platform;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.Client;


public class UserInterfaceController{
	@FXML private AnchorPane signInRoot, signUpRoot, homeRoot, uploadRoot, filesRoot, succesfulRoot, searchRoot, unregisterRoot;
	@FXML private TextField username, password, usernameSignUp, passwordSignUp, emailSignUp, searchBar;
	@FXML private Button signIn, signUpNow, signUp, cancelSignUp, uploadButton, removeUploadFileButton, publicDirectoryUploadButton, privateDirectoryUploadButton, privateFilesButton, publicFilesButton;
	@FXML private Pane uploadList, downloadFileList, searchList;
	@FXML private Label incorrectSignIn, usernameError, passwordError, emailError, repasswordError, uploadFail, fileToUpload, progressLabel;
	@FXML private ScrollPane scrollUploadList, scrollFileList;
	@FXML private ToggleGroup toggleGroupUpload;
	@FXML private ProgressBar progressbar;
	private Stage stage;
	private File file;
	private boolean publicDirectory, privateDirectory, selected=false;
	private ListView<String> listView;
	private Label resultLabel = new Label();
	private Client client;
	private ProgressIndicator loader;



	@FXML
	public void initialize() {
		loader = new ProgressIndicator();
		loader.setPrefHeight(250);
		loader.setPrefWidth(250);
		loader.setLayoutX(100);
		loader.setLayoutY(140);
		resultLabel.setFont(Font.font("Helvetica Neue Bold", 14.0));
		resultLabel.setLayoutY(435);
		resultLabel.setLayoutX(50);
		resultLabel.setAlignment(Pos.CENTER);
		if(filesRoot!=null) {
			getFileList();
			progressbar.setVisible(false);
			//			controller = Main.getController();
			//			double procentage = Math.round(controller.getUsedMemoryPercentage()*10)/10.0;
			//			progressbar.setProgress(procentage);
			//			progressLabel.setText(Double.toString(procentage)+"/5.0 GB used");
		}
		else if(uploadRoot!=null) {
			uploadFail.setAlignment(Pos.CENTER);
		}
		else if(searchRoot!=null) {
			searchBar.setFocusTraversable(false);
		}
		else if(homeRoot!=null) {
			searchBar.setFocusTraversable(false);
		}
		else if(signInRoot!=null) {
			username.setFocusTraversable(false);
			password.setFocusTraversable(false);
		}
		else if(signUpRoot!=null) {
			usernameSignUp.setFocusTraversable(false);
			passwordSignUp.setFocusTraversable(false);
			emailSignUp.setFocusTraversable(false);
		}
	}

	@FXML
	public void signInButton() throws IOException {
		client = Main.getClient();
		client.setUserInterface(this);
		client.login(username.getText(), password.getText());
		signInRoot.getChildren().add(loader);
		signIn.setDisable(true);
		signUpNow.setDisable(true);
		username.setDisable(true);
		password.setDisable(true);
	}

	@FXML
	public void signOutButton() throws IOException {
		client = Main.getClient();
		client.setUserInterface(this);
		client.logout();
	}

	@FXML
	public void unregisterButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("unregisterUI.fxml"));
		homeRoot.getChildren().setAll(controllerPane);

	}

	public void verifyUnregisterButton() throws IOException{
		client = Main.getClient();
		client.setUserInterface(this);
		client.unregister();
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("signInUI.fxml"));
		unregisterRoot.getChildren().setAll(controllerPane);
	}

	@FXML
	public void signUpButton() throws IOException, NoSuchAlgorithmException {
		client = Main.getClient();
		client.setUserInterface(this);
		client.register(usernameSignUp.getText(), emailSignUp.getText(), passwordSignUp.getText());
		signUpRoot.getChildren().add(loader);
		usernameSignUp.setDisable(true);
		passwordSignUp.setDisable(true);
		emailSignUp.setDisable(true);
		signUp.setDisable(true);
	}

	@FXML
	public void chooseFileButton() throws IOException{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			fileToUpload.setText(file.getName());
			fileToUpload.setLayoutX(30.0);
			fileToUpload.setLayoutY(19.0);
			removeUploadFileButton.setVisible(true);
		}
	}

	@FXML
	public void uploadSuccesfulButton() throws IOException {
		client = Main.getClient();
		client.setUserInterface(this);
		if(publicDirectory)
			client.upload(file, getChosenDirectory(), "pub");
		else
			client.upload(file, getChosenDirectory(), "pvt");
		uploadButton.setDisable(false);
	}

	@FXML
	public void fileButton() throws IOException{
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("filesUI.fxml"));
		homeRoot.getChildren().setAll(controllerPane);
	}

	@FXML
	public void returnToHomePage() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("homePageUI.fxml"));
		if(filesRoot!=null)
			filesRoot.getChildren().setAll(controllerPane);
		else if(succesfulRoot!=null)
			succesfulRoot.getChildren().setAll(controllerPane);
		else if(searchRoot!=null)
			searchRoot.getChildren().setAll(controllerPane);
		else if(uploadRoot!=null)
			uploadRoot.getChildren().setAll(controllerPane);
	}

	@FXML
	public void signUpNowButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("signUpUI.fxml"));
		signInRoot.getChildren().setAll(controllerPane);
	}

	@FXML
	public void cancelSignUpButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("signInUI.fxml"));
		signUpRoot.getChildren().setAll(controllerPane);
	}

	@FXML
	public void uploadButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("upLoadUI.fxml"));
		homeRoot.getChildren().setAll(controllerPane);
	}
	
	public void searchButton() throws IOException {
		if(homeRoot!=null) {
			Parent controllerPane = FXMLLoader.load(Main.class.getResource("searchResultUI.fxml"));
			homeRoot.getChildren().setAll(controllerPane);
		}
		else if(searchRoot!=null) {
			client=Main.getClient();
			client.setUserInterface(this);
			client.search(searchBar.getText());
		}
	}

	

	@FXML
	public void removeUploadFile() {
		fileToUpload.setText("");
		removeUploadFileButton.setVisible(false);
		file=null;
	}

	public boolean isPublic() {
		return publicDirectory;
	}

	public boolean isPrivate() {
		return privateDirectory;
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
		client = Main.getClient();
		client.setUserInterface(this);
		String filename = listView.getSelectionModel().getSelectedItem();
		client.deleteFile(filename, getChosenDirectory());
	}
	@FXML
	public void downloadButton(){
		client = Main.getClient();
		client.setUserInterface(this);
		String filename = listView.getSelectionModel().getSelectedItem();
		client.download(filename, getChosenDirectory());
	}
	@FXML
	public void getFileList() {
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
		getFileList("public");
	}
	@FXML
	public void selectPrivateFilesButton() {
		privateDirectory=true;
		publicDirectory=false;
		privateFilesButton.setStyle("-fx-background-color: rgb(90, 51, 103);");
		privateFilesButton.setTextFill(Color.WHITE);
		publicFilesButton.setStyle("-fx-border-color: rgb(90, 51, 103); -fx-background-color: rgb(255,255,255); -fx-border-radius: 3;");
		publicFilesButton.setTextFill(Color.web("rgb(90, 51, 103)"));
		getFileList("private");
	}
	public void setStage(Stage stage) {
		this.stage=stage;
	}
	private String getChosenDirectory() {
		if(privateDirectory) return "private";
		return "public";
	}

	private void getFileList(String directory) {
		client = Main.getClient();
		client.setUserInterface(this);
		client.getFilelist(directory);
	}
	
	public void setSearchList(String searchItems) {
		ListView<Button> listView = new ListView<Button>();
		listView.setPrefHeight(300);
		listView.setPrefWidth(400);
		if(listView.getItems().size()>0)
			for(int i=listView.getItems().size();i>=0;i--) 
				listView.getItems().remove(i);
		if(!searchItems.isEmpty()) {
			for(String searchItem : searchItems.split("\n")) {
				Button button = new Button();
				button.setText(searchItem.split("\t\t")[0]);
				button.setStyle("-fx-border-color: rgb(90, 51, 103); -fx-background-color: rgb(255,255,255);-fx-padding: 5; -fx-border-style: none; -fx-border-width: 0; -fx-border-insets: 0; -fx-cursor: hand;");
				button.setOnAction(new EventHandler<ActionEvent>() {
		            public void handle(ActionEvent event) {
						try {
							Parent controllerPane = FXMLLoader.load(Main.class.getResource("signUpUI.fxml"));
							searchRoot.getChildren().setAll(controllerPane);
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
		        });
				
				listView.getItems().add(button);
			}
		}
		Platform.runLater(new Runnable() {
			public void run() {
				if(searchList.getChildren().size()>0)
					searchList.getChildren().remove(0);
				searchList.getChildren().add(listView);
			}
		});
	}
	public void updateFileList() {
		getFileList(getChosenDirectory());
	}

	public void setFileList(String files) {
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
				listView.getItems().add(filename);
			}
		}
		Platform.runLater(new Runnable() {
			public void run() {
				if(downloadFileList.getChildren().size()>0)
					downloadFileList.getChildren().remove(0);
				downloadFileList.getChildren().add(listView);
			}
		});
	}

	public void changeScene(int type) throws IOException {
		Platform.runLater(new Runnable() {
			Parent controllerPane = null;
			public void run() {
				try {
					System.out.println(type);
					switch(type) {
					case 1:
						controllerPane = FXMLLoader.load(Main.class.getResource("homePageUI.fxml"));
						signInRoot.getChildren().setAll(controllerPane);
						break;
					case 2:
						controllerPane = FXMLLoader.load(Main.class.getResource("signInUI.fxml"));
						if(homeRoot!=null)
							homeRoot.getChildren().setAll(controllerPane);
						else if(succesfulRoot!=null)
							succesfulRoot.getChildren().setAll(controllerPane);
						System.out.println("Logged out");
						break;
					case 3:
						controllerPane = FXMLLoader.load(Main.class.getResource("homePageUI.fxml"));
						signUpRoot.getChildren().setAll(controllerPane);
						break;
					case 4:
						controllerPane = FXMLLoader.load(Main.class.getResource("uploadSuccesfulUI.fxml"));
						uploadRoot.getChildren().setAll(controllerPane);
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void displayMessage(int type, String message) throws IOException {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				switch(type) {
				case 1:
					incorrectSignIn.setText(message);
					signInRoot.getChildren().remove(loader);
					signIn.setDisable(false);
					signUpNow.setDisable(false);
					username.setDisable(false);
					password.setDisable(false);
					break;
				case 2:
					String[] messages = message.split("\n");
					usernameError.setText(messages[0]);
					passwordError.setText(messages[2]);
					emailError.setText(messages[1]);
					signUpRoot.getChildren().remove(loader);
					usernameSignUp.setDisable(false);
					passwordSignUp.setDisable(false);
					emailSignUp.setDisable(false);
					signUp.setDisable(false);
					break;
				case 3:
					filesRoot.getChildren().remove(resultLabel);
					resultLabel.setText(message);
					filesRoot.getChildren().add(resultLabel);
					break;
				case 4:
					uploadRoot.getChildren().remove(resultLabel);
					resultLabel.setText(message);
					uploadRoot.getChildren().add(resultLabel);
					uploadRoot.getChildren().remove(loader);
					uploadButton.setDisable(true);
				case 5:
					uploadRoot.getChildren().remove(resultLabel);
					resultLabel.setText(message);
					break;
				}
			}
		});
	}
}
