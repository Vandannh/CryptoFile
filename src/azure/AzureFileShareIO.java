package azure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.file.*;

/**
 * This class will function as a bridge between Azure storage and local files. 
 * By connecting to an Azure storage account and store/get files from the storage. 
 * This class mainly has 3 functions, with theese you can 1. connect, 2. upload, 3. download to/from Azure file share
 * 
 * @version 1.0
 * @since 2019-04-15
 * @author Robin Andersson
 */
public class AzureFileShareIO {
	CloudStorageAccount storageAccount;
	CloudFileClient fileClient;
	CloudFileShare share;
	File file;

	/*
	 * The Connection string used to connect to Azure storage account
	 * Change this string if you want to change which storage to work from
	 */
	public static final String storageConnectionString = "YOUR_CONNECTION_STRING"; // Edit this


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
	 * @param userDirectory 
	 * @param file 
	 */
	public void upload(String userDirectory, File file) {
		System.out.println("im about to open the JFileChooser");
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference(); //Get a reference to the root directory for the share.
			CloudFileDirectory sampleDir = rootDir.getDirectoryReference(userDirectory);
			System.out.println(file.toString());
			System.out.println(userDirectory);
			final String filePath = file.toString(); // Define the path to a local file.
			CloudFile cloudFile = sampleDir.getFileReference(file.getName());
			cloudFile.uploadFromFile(filePath);
		} catch (StorageException | URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the file extension
	 * @param file
	 * @return extension as a String
	 */
	public String getExtension(CloudFile file) {
		return(file.getName().substring(file.getName().indexOf('.')+1));
	}

	/**
	 * Downloading an file from the selected file share.
	 * 
	 * @param userDirectory the name of the directory the file is being downloaded from
	 * @param filename the name of the file to download
	 * @return if the download was successful
	 */
	public boolean download(String userDirectory, String filename) {
		System.out.println(userDirectory);
		System.out.println(filename);
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference(); //Get a reference to the root directory for the share.
			CloudFileDirectory sampleDir = rootDir.getDirectoryReference(userDirectory); //Get a reference to the directory that contains the file
			CloudFile file = sampleDir.getFileReference(filename); //Get a reference to the file you want to download
			System.out.println(file.getName());
			System.out.println(rootDir.toString());
			String resource = "downloads/";
			mkdir(resource);
			System.out.println(resource);
			file.download(new FileOutputStream(new File(resource + filename))); 
			return true;

		} catch (StorageException | URISyntaxException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Creates a directory on local computer if it doesn't exists
	 * 
	 * @param directoryName the name of the directory being created
	 * @throws IOException 
	 */
	public void mkdir(String directoryName) throws IOException {
		Path path = Paths.get(directoryName);
		if (!Files.exists(path)) {
			Files.createDirectory(path);
		}
	}

	/**
	 * Creates a directory in Azure File Share if it doesn't exists
	 * 
	 * @param directoryName the name of the directory being created
	 */
	public void createDirectory(String directoryName) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory sampleDir = rootDir.getDirectoryReference(directoryName);
			sampleDir.createIfNotExists();
		} catch (StorageException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
