package main.java.azure;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	 * Connecting to Azure storage account and gets an reference to a Azure file share container. If
	 * the share does not exists, a new one will be created used the parameter 
	 * Reference retrieved will be used to upload/download and remove files of the container in the file share.
	 * @param shareName -  name of the fileshare
	 */
	public void connect(String shareName)  {
		try {
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			fileClient = storageAccount.createCloudFileClient();

			share = fileClient.getShareReference(shareName);
			if(share.createIfNotExists()) {
				FileShareProperties properties = share.getProperties();
				properties.setShareQuota(5); //set the limit of storage to 5GB
				share.uploadProperties();
			}
			System.out.println(usedMemoryPercentage());
		} catch (InvalidKeyException | URISyntaxException | StorageException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Uploading an file to the share in Azure storage
	 * @param userDirectory 
	 * @param file 
	 */
	public void upload(String directory, File file) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory userDir = rootDir.getDirectoryReference(directory);
			CloudFile cloudFile = userDir.getFileReference(file.getName());
			cloudFile.uploadFromFile(file.toString());	
			System.out.println(directory);
			System.out.println(checkAvailableSpace()); //MARK: for test purpose
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
	public boolean download(String directory, String filename) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory userDir = rootDir.getDirectoryReference(directory);	
			CloudFile file = userDir.getFileReference(filename);
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
	 * Checks the status of a users share in terms of memory usage, displayed in megabytes
	 * @return used memory in megabytes
	 */
	public double checkAvailableSpace() {
		try {
			ShareStats stats = share.getStats();
			double memoryInBytes = stats.getUsageInBytes();
			double memoryInMegabytes = memoryInBytes/1000000;
			System.out.println(memoryInMegabytes); //MARK: for test only
			return memoryInMegabytes;

		} catch(StorageException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * Deletes a directory
	 * 
	 * @param directoryName the name of the directory being deleted
	 */
	public void deleteDirectory(String directoryName) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory containerDir = rootDir.getDirectoryReference(directoryName);
			containerDir.deleteIfExists();
		} catch (StorageException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays used memory in contrast to sharequota, ie how much space have been used in percentage 
	 * of a fileshare
	 * @return used memory in percentage
	 */
	public double usedMemoryPercentage() {
		FileShareProperties properties = share.getProperties();
		return checkAvailableSpace()/(properties.getShareQuota()*1000);
	}
}
