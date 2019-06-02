package application;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
/**
 * Class that contains the methods for the UI
 *
 * @author Ramy Behnam, Mattias Jönsson
 * @version 2.0
 * @since 2019-05-10
 *
 */
public class UserInterfaceController{
	@FXML private AnchorPane signInRoot, signUpRoot, homeRoot, uploadRoot, filesRoot, succesfulRoot, searchRoot, unregisterRoot, userPageRoot;
	@FXML private TextField username, password, usernameSignUp, passwordSignUp, emailSignUp, searchBar;
	@FXML private Button signIn, signUpNow, signUp, cancelSignUp, uploadButton, removeUploadFileButton, publicDirectoryUploadButton, privateDirectoryUploadButton, privateFilesButton, publicFilesButton;
	@FXML private Pane uploadList, downloadFileList, searchList;
	@FXML private Label userLabel, incorrectSignIn, usernameError, passwordError, emailError, repasswordError, uploadFail, fileToUpload, progressLabel;
	@FXML private ScrollPane scrollUploadList, scrollFileList;
	@FXML private ToggleGroup toggleGroupUpload;
	@FXML private ProgressBar progressbar;
	private Stage stage;
	private File file;
	private boolean publicDirectory, privateDirectory, selected=false, otherUser;
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
		else if(signInRoot!=null) {
			username.setFocusTraversable(false);
			password.setFocusTraversable(false);
		}
		else if(signUpRoot!=null) {
			usernameSignUp.setFocusTraversable(false);
			passwordSignUp.setFocusTraversable(false);
			emailSignUp.setFocusTraversable(false);
		}
		else if(userPageRoot!=null) {
			otherUser=true;
			privateDirectory=false;
			publicDirectory=false;
			client=Main.getClient();
			client.setUserInterface(this);
			client.getUserFiles(client.getSearchedUser());
			userLabel.setText(client.getSearchedUser());
		}
	}
	
	/**
	 * Method for sign in
	 * @throws IOException
	 */
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
	
	/**
	 * Method for sign out
	 * @throws IOException
	 */
	@FXML
	public void signOutButton() throws IOException {
		client = Main.getClient();
		client.setUserInterface(this);
		client.logout();
	}
	
	/**
	 * Method for unregistering
	 * @throws IOException
	 */
	@FXML
	public void unregisterButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("unregisterUI.fxml"));
		homeRoot.getChildren().setAll(controllerPane);

	}
	
	/**
	 * Method for verifying that use wants to unregister acount
	 * @throws IOException
	 */
	public void verifyUnregisterButton() throws IOException{
		client = Main.getClient();
		client.setUserInterface(this);
		client.unregister();
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("signInUI.fxml"));
		unregisterRoot.getChildren().setAll(controllerPane);
	}
	
	/**
	 * Method for sign up
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
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
	
	/**
	 * Method for choosing file that user wishes to upload 
	 * @throws IOException
	 */
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
	
	/**
	 * Method for uploading the file
	 * @throws IOException
	 */
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

	/**
	 * Method that changes scene from homePageUI to filesUI
	 * @throws IOException
	 */
	@FXML
	public void fileButton() throws IOException{
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("filesUI.fxml"));
		homeRoot.getChildren().setAll(controllerPane);
	}

	/**
	 * Method that returns to homePageUI from different scenes
	 * @throws IOException
	 */
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
		else if(userPageRoot!=null)
			userPageRoot.getChildren().setAll(controllerPane);	
	}
	
	/**
	 * Method that changes scene from signInUI to signUpUI
	 * @throws IOException
	 */
	@FXML
	public void signUpNowButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("signUpUI.fxml"));
		signInRoot.getChildren().setAll(controllerPane);
	}

	/**
	 * Method that changes scene from signUpUI to signInUI
	 * @throws IOException
	 */
	@FXML
	public void cancelSignUpButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("signInUI.fxml"));
		signUpRoot.getChildren().setAll(controllerPane);
	}

	/**
	 * Method that changes scene from homePageUI touploadUI
	 * @throws IOException
	 */
	@FXML
	public void uploadButton() throws IOException {
		Parent controllerPane = FXMLLoader.load(Main.class.getResource("upLoadUI.fxml"));
		homeRoot.getChildren().setAll(controllerPane);
	}
	
	/**
	 * Method for search function
	 * @throws IOException
	 */
	@FXML
	public void searchButton() throws IOException {
		if(homeRoot!=null) {
			Parent controllerPane = FXMLLoader.load(Main.class.getResource("searchResultUI.fxml"));
			homeRoot.getChildren().setAll(controllerPane);
		}
		else if(searchRoot!=null) {
			if(!searchBar.getText().isEmpty()) {
				client=Main.getClient();
				client.setUserInterface(this);
				client.search(searchBar.getText());
			}
		}
	}
	
	/**
	 * Method for removing file that the user no longer want to upload
	 */
	@FXML
	public void removeUploadFile() {
		fileToUpload.setText("");
		removeUploadFileButton.setVisible(false);
		file=null;
	}

	/**
	 * Method that returns publicDirectory
	 * @return
	 */
	public boolean isPublic() {
		return publicDirectory;
	}

	/**
	 * Method that returns privateDirectory
	 * @return
	 */
	public boolean isPrivate() {
		return privateDirectory;
	}
	
	public boolean isOtherUser() {
		return otherUser;
	}
	
	/**
	 * Method for choosing public directory when trying to upload file
	 */
	@FXML
	public void choosePublicDirectory() {
		selected=true;
		publicDirectory=true;
		privateDirectory=false;
		otherUser=false;
		publicDirectoryUploadButton.setStyle("-fx-background-color: rgb(26,116,168);");
		publicDirectoryUploadButton.setTextFill(Color.WHITE);
		privateDirectoryUploadButton.setStyle("-fx-border-color: rgb(26,116,168); -fx-background-color: rgb(255,255,255); -fx-border-radius: 3;");
		privateDirectoryUploadButton.setTextFill(Color.web("rgb(26,116,168)"));
	}
	
	/**
	 * Method for choosing private directory when trying to upload file
	 */
	@FXML
	public void choosePrivateDirectory() {
		selected=true;
		privateDirectory=true;
		publicDirectory=false;
		otherUser=false;
		privateDirectoryUploadButton.setStyle("-fx-background-color: rgb(26,116,168);");
		privateDirectoryUploadButton.setTextFill(Color.WHITE);
		publicDirectoryUploadButton.setStyle("-fx-border-color: rgb(26,116,168); -fx-background-color: rgb(255,255,255); -fx-border-radius: 3;");
		publicDirectoryUploadButton.setTextFill(Color.web("rgb(26,116,168)"));
	}
	
	/**
	 * Method for deleting chosen file from users directory
	 */
	@FXML
	public void deleteFileButton(){
		client = Main.getClient();
		client.setUserInterface(this);
		String filename = listView.getSelectionModel().getSelectedItem();
		if(filename!=null)
			client.deleteFile(filename, getChosenDirectory());
		else {
			filesRoot.getChildren().remove(resultLabel);
			resultLabel.setText("Please choose a file to delete");
			filesRoot.getChildren().add(resultLabel);
		}
	}
	
	/**
	 * Method for downloading chosen file from users directory
	 */
	@FXML
	public void downloadButton(){
		client = Main.getClient();
		client.setUserInterface(this);
		String filename = listView.getSelectionModel().getSelectedItem();
			if(userPageRoot!=null) 
				if(filename!=null)
					client.downloadUserFile(filename, client.getSearchedUser());
				else {
					filesRoot.getChildren().remove(resultLabel);
					resultLabel.setText("Please choose a file to download");
					filesRoot.getChildren().add(resultLabel);
				}
			else
				if(filename!=null)
					client.download(filename, getChosenDirectory());
				else {
					filesRoot.getChildren().remove(resultLabel);
					resultLabel.setText("Please choose a file to download");
					filesRoot.getChildren().add(resultLabel);
				}
	}
	
	@FXML
	public void getFileList() {
		selectPrivateFilesButton();
	}
	
	/**
	 * Method for choosing public directory when trying to download or delete files from users files
	 */
	@FXML
	public void selectPublicFilesButton() {
		publicDirectory=true;
		privateDirectory=false;
		publicFilesButton.setStyle("-fx-background-color: rgb(26,116,168);");
		publicFilesButton.setTextFill(Color.WHITE);
		privateFilesButton.setStyle("-fx-border-color: rgb(26,116,168); -fx-background-color: rgb(255,255,255); -fx-border-radius: 3;");
		privateFilesButton.setTextFill(Color.web("rgb(26,116,168)"));
		getFileList("public");
	}
	
	/**
	 * Method for choosing public directory when trying to download or delete files from users files
	 */
	@FXML
	public void selectPrivateFilesButton() {
		privateDirectory=true;
		publicDirectory=false;
		privateFilesButton.setStyle("-fx-background-color: rgb(26,116,168);");
		privateFilesButton.setTextFill(Color.WHITE);
		publicFilesButton.setStyle("-fx-border-color: rgb(26,116,168); -fx-background-color: rgb(255,255,255); -fx-border-radius: 3;");
		publicFilesButton.setTextFill(Color.web("rgb(26,116,168)"));
		getFileList("private");
	}
	
	public void setStage(Stage stage) {
		this.stage=stage;
	}
	
	
	private String getChosenDirectory() {
		if(privateDirectory) return "private";
		return "public";
	}
	
	/**
	 * Method for fetching files from the chosen directory
	 * @param directory
	 */
	private void getFileList(String directory) {
		client = Main.getClient();
		client.setUserInterface(this);
		client.getFilelist(directory);
	}
	
	/**
	 * Method for showing list for users
	 * @param searchItems
	 */
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
							client=Main.getClient();
							client.setSearchedUser(searchItem.split("\t\t")[0]);
							Parent controllerPane = FXMLLoader.load(Main.class.getResource("userPageUI.fxml"));
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
	
	/**
	 * Updates the filelist
	 */
	public void updateFileList() {
		getFileList(getChosenDirectory());
	}
	
	/**
	 * Sets the filelist and shows it
	 * 
	 * @param files the list of the files
	 */
	public void setFileList(String files) {
		listView = new ListView<String>();
		listView.setPrefHeight(200);
		listView.setPrefWidth(400);
		if(listView.getItems().size()>0)
			for(int i=listView.getItems().size();i>=0;i--) {
				listView.getItems().remove(i);
			}
		if(!files.isEmpty())
			for(String file : files.split("\n")) 
				listView.getItems().add(file.split("\t\t")[0]);
		Platform.runLater(new Runnable() {
			public void run() {
				if(downloadFileList.getChildren().size()>0)
					downloadFileList.getChildren().remove(0);
				downloadFileList.getChildren().add(listView);
			}
		});
	}

	/**
	 * Changing different scenes
	 * @param type the type of change
	 * @throws IOException
	 */
	public void changeScene(int type) throws IOException {
		Platform.runLater(new Runnable() {
			Parent controllerPane = null;
			public void run() {
				try {
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

	/**
	 * Displays different messages
	 * @param type
	 * @param message
	 * @throws IOException
	 */
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
					usernameError.setText(messages[1]);
					passwordError.setText("Password must contain a minimum of:\n1 number, 1 letter and 8 characters");
					emailError.setText(messages[2]);
					signUpRoot.getChildren().remove(loader);
					usernameSignUp.setDisable(false);
					passwordSignUp.setDisable(false);
					emailSignUp.setDisable(false);
					signUp.setDisable(false);
					break;
				case 3:
					if(filesRoot!=null) {
						filesRoot.getChildren().remove(resultLabel);
						resultLabel.setText(message);
						filesRoot.getChildren().add(resultLabel);
					}
					else if(userPageRoot!=null) {
						userPageRoot.getChildren().remove(resultLabel);
						resultLabel.setText(message);
						userPageRoot.getChildren().add(resultLabel);
					}
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
