package azureBridge;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.file.*;

/**
 * This class will function as a bridge between Azure storage and local files. 
 * By connecting to an Azure storage account and store/get files from the storage. 
 * This class mainly has 3 functions, with theese you can 1. connect, 2. upload, 3. download to/from Azure file share
 * @author Broceps
 */
public class AzureFileshareIO {
	CloudStorageAccount storageAccount;
	CloudFileClient fileClient;
	CloudFileShare share;
	File file;

	/*
	 * The Connection string used to connect to Azure storage account
	 * Change this string if you want to change which storage to work from
	 */
	public static final String storageConnectionString =
			"DefaultEndpointsProtocol=https;AccountName=cryptofile1;"
					+ "AccountKey=e93IzFOe+Z5okcXlk4WpAZ9ozaHPifAd8l8rLvGFlNORKtoBASySgx8clTwukGRqsL/1UyET7Y5HRO8KceTlqQ==;"
					+ "EndpointSuffix=core.windows.net";


	/**
	 * Connecting to Azure storage account and gets an reference to a Azure file share container.
	 * Reference retrieved will be used to upload/download files to the container in the file share.
	 */
	public void connect()  {
		try {
			//retrieving storage account
			storageAccount = CloudStorageAccount.parse(storageConnectionString);

			// Create the Azure Files client.
			fileClient = storageAccount.createCloudFileClient();

			// Get a reference to the file share
			share = fileClient.getShareReference("test"); //test = name of the share in cryptofile1

			System.out.println("Connected!");

		} catch (InvalidKeyException invalidKey) {
			invalidKey.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Uploading an file to the share in Azure storage
	 */
	public void upload() {
		//To choose a file:
		System.out.println("im about to open the JFileChooser");
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
		}


		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference(); //Get a reference to the root directory for the share.

			System.out.println(file.toString());
			final String filePath = file.toString(); // Define the path to a local file.

			//______The code for upploading:____________________________
			CloudFile cloudFile = rootDir.getFileReference(file.getName());
			cloudFile.uploadFromFile(filePath);
			//__________________________________________________________

		} catch (StorageException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	/**
	 * Downloading an file from the selected file share, by default we download from the root directory,
	 * if you wish to change from which directory to download, change the parameter of the "sampleDir" 
	 * ( currently using parameter "rootDir.getName() ).
	 * @param name of the file to download
	 */
	public void download(String filename) {

		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference(); //Get a reference to the root directory for the share.
			CloudFileDirectory sampleDir = rootDir.getDirectoryReference(rootDir.toString()); //Get a reference to the directory that contains the file
			CloudFile file = sampleDir.getFileReference(filename); //Get a reference to the file you want to download
			System.out.println(file.getName());

			//DO SOMETHING WITH THE FILE
			file.download(new FileOutputStream(new File("/../files/hej.txt"))); //MARK: Denna delen behövs lösas, får det ej att fungera med att
			//skriva ned filen lokalt, allt fungerar bra fram tills hit, 
			

			
			
		} catch (StorageException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
