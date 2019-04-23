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
	private CloudStorageAccount storageAccount;
	private CloudFileClient fileClient;
	private CloudFileShare share;
	
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
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			fileClient = storageAccount.createCloudFileClient();
			share = fileClient.getShareReference("test");
		} catch (InvalidKeyException | URISyntaxException | StorageException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Uploading an file to the share in Azure storage
	 * @param userDirectory 
	 * @param file 
	 */
	public void upload(String userDirectory, String directory, File file) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory userDir = rootDir.getDirectoryReference(userDirectory);
			CloudFileDirectory innerUserDir = userDir.getDirectoryReference(directory);
			CloudFile cloudFile = innerUserDir.getFileReference(file.getName());
			cloudFile.uploadFromFile(file.toString());
		} catch (StorageException | URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Downloading an file from the selected file share.
	 * 
	 * @param userDirectory the name of the directory the file is being downloaded from
	 * @param filename the name of the file to download
	 * @return if the download was successful
	 */
	public boolean download(String userDirectory, String directory, String filename) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory userDir = rootDir.getDirectoryReference(userDirectory);
			CloudFileDirectory userInnerDir = userDir.getDirectoryReference(directory);		
			CloudFile file = userInnerDir.getFileReference(filename);
			String resource = "downloads/";
			createDirectoryLocally(resource);
			file.download(new FileOutputStream(new File(resource + filename))); 
			return true;
		} catch (StorageException | URISyntaxException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deleting a file from the fileshare
	 * @param userDirectory the name of the directory the file is being downloaded from
	 * @param directory within the users directory
	 * @param name of file to be deleted
	 * @return if the download was successful
	 */
	public boolean deleteFile(String userDirectory, String directory, String filename) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory userDir = rootDir.getDirectoryReference(userDirectory);
			CloudFileDirectory userInnerDir = userDir.getDirectoryReference(directory);		
			CloudFile file = userInnerDir.getFileReference(filename);
			
			if(file.deleteIfExists()) {
				System.out.println("file: " + file.getName().toString() + " has been deleted!");
				return true;
			}else
				return false;
		} catch (StorageException | URISyntaxException e) {
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
	public void createDirectoryLocally(String directoryName) throws IOException {
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
	public void createDirectoryInAzure(String directoryName) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory dirToCreate = rootDir.getDirectoryReference(directoryName);
			dirToCreate.createIfNotExists();
		} catch (StorageException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a directory inside another directory
	 * 
	 * @param directoryName
	 */
	public void createDirectoryInsideDirectoryInAzure(String directoryName, String innerDirectoryName) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory userDir = rootDir.getDirectoryReference(directoryName);
			CloudFileDirectory userInnerDir = userDir.getDirectoryReference(innerDirectoryName);
			userInnerDir.createIfNotExists();
		} catch (StorageException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
