package azure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.ArrayList;


import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.file.*;

import test2.ConnectionStrings;

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
	public static final String storageConnectionString = ConnectionStrings.storageConnectionString;

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
		} catch (InvalidKeyException | URISyntaxException | StorageException e) {
			e.printStackTrace();
		}
	}


	public void upload(String directory, File file) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory userDir = rootDir.getDirectoryReference(directory);
			CloudFile cloudFile = userDir.getFileReference(file.getName());
			cloudFile.uploadFromFile(file.toString());	
		} catch (StorageException | URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

	public boolean download(String directory, String filename, String resource) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory userDir = rootDir.getDirectoryReference(directory);	
			CloudFile file = userDir.getFileReference(filename);
			//createDirectoryLocally(resource);
			try(FileOutputStream fos = new  FileOutputStream(new File(resource + filename))){
				file.download(fos);
			}
			return true;
		} catch (StorageException | URISyntaxException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteFile(String directory, String filename) {
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory userDir = rootDir.getDirectoryReference(directory);	
			CloudFile file = userDir.getFileReference(filename);
			if(file.deleteIfExists()) {
				return true;
			}else
				return false;
		} catch (StorageException | URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}


	//public void createDirectoryLocally(String directoryName) throws IOException {
	//	Path path = Paths.get(directoryName);
	//	if (!Files.exists(path)) {
	//		Files.createDirectory(path);
	//	}
	//}

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
	
	public void deleteShare(String shareName) {
		try {
			share.deleteIfExists();
		} catch (StorageException e) {
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

	/**
	 * Retrieve the name of all files in a specified directory in a users fileshare
	 * @param directoryName - name of the directory to retrieve filenames from
	 * @return ArrayList with the filenames
	 */
	public ArrayList<String> getFileNamesInDirectory(String directoryName) {
		ArrayList<String> filenames = new ArrayList<String>();
		try {
			CloudFileDirectory rootDir = share.getRootDirectoryReference();
			CloudFileDirectory directory = rootDir.getDirectoryReference(directoryName);
			Iterable<ListFileItem> items = directory.listFilesAndDirectories();
			for(ListFileItem file: items) {
				String filename = file.getUri().toString().substring(file.getUri().toString().lastIndexOf('/')+1).replaceAll("%20", " "); //retrieves the filename in a clean format
				filenames.add(filename);
			}
		}catch(StorageException | URISyntaxException e) {
			e.printStackTrace();
		}
		return filenames;
	}

	

}
