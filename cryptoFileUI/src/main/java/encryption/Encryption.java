package main.java.encryption;

import java.nio.file.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Contains static methods for generating RSA keypairs, encrypting and decrypting files using AES/RSA encryption.
 * @version 1.0
 * @since 2019-05-15
 * @author Daniel Hägg
 *
 */
public class Encryption{
	static SecureRandom sRandom = new SecureRandom();

	/**
	 * Encrypts and decrypts a file
	 * @param cipher The cipher that is used to process the file
	 * @param inStream Stream to the file to be processed
	 * @param outStream Stream to the file to be processed
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 */
	private static void processFile(Cipher cipher,InputStream inStream,OutputStream outStream) throws IllegalBlockSizeException,BadPaddingException,IOException {
		byte[] inBuffer = new byte[1024];
		int length;
		while ((length = inStream.read(inBuffer)) != -1) {
			byte[] outBuffer = cipher.update(inBuffer, 0, length);
			if ( outBuffer != null )
				outStream.write(outBuffer);
		}
		byte[] outBuffer = cipher.doFinal();
		if ( outBuffer != null )
			outStream.write(outBuffer);
	}

	/**
	 * Generates a RSA keypair
	 * @return KeyPair The RSA keypair
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static KeyPair doGenkey() throws NoSuchAlgorithmException,IOException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(2048);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		try (FileOutputStream outStream = new FileOutputStream("temp/rsa.key")) {
			outStream.write(keyPair.getPrivate().getEncoded());
		}
		try (FileOutputStream outStream = new FileOutputStream("temp/rsa.pub")) {
			outStream.write(keyPair.getPublic().getEncoded());
		}
		return keyPair;
	}

	/**
	 * Encrypts a file with an RSA key
	 * @param inputFile The file to be encrypted
	 * @param key The RSA key used to encrypt the file
	 * @return File The encrypted file is returned
	 * @throws Exception
	 */
	public static File encrypt(File inputFile, String keyPath, String key)throws Exception{
		Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		if(key == "pvt") {
			byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey pvtKey = keyFactory.generatePrivate(keySpec);
			cipherRSA.init(Cipher.ENCRYPT_MODE, pvtKey);
		} else if(key == "pub") {
			byte[] bytes = Files.readAllBytes(Paths.get(keyPath));
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey pubKey = keyFactory.generatePublic(keySpec);
			cipherRSA.init(Cipher.ENCRYPT_MODE, pubKey);
		}
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		SecretKey secretKey = keyGen.generateKey();

		byte[] iv = new byte[128/8];
		sRandom.nextBytes(iv);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);

		try (FileOutputStream outStream = new FileOutputStream(inputFile + ".enc")) {
			byte[] fileBytes = cipherRSA.doFinal(secretKey.getEncoded());
			outStream.write(fileBytes);
			System.err.println("AES Key Length: " + fileBytes.length);
			outStream.write(iv);
			System.err.println("IV Length: " + iv.length);
			Cipher cipherAES = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipherAES.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
			try (FileInputStream inStream = new FileInputStream(inputFile)) {
				processFile(cipherAES, inStream, outStream);
			}
		}
		File encrypted = new File(inputFile+".enc");
		return(encrypted);
	}

	/**
	 * Decrypts a file with an RSA key
	 * @param inputFile The file to be decrypted
	 * @param key The RSA key used to decrypt the file
	 * @return File The decrypted file is returned
	 * @throws Exception
	 */
	public static File decrypt(File inputFile, String keyPath, String key) throws Exception{
		File decrypted = null;
		Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		if(key == "pub") {
			String pubKeyFile = keyPath;
			byte[] bytes = Files.readAllBytes(Paths.get(pubKeyFile));
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(keySpec);
			cipherRSA.init(Cipher.DECRYPT_MODE, publicKey);
		} else {
			byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
		}

		try (FileInputStream inStream = new FileInputStream(inputFile)) {
			SecretKeySpec secretKeySpec = null;
			byte[] fileBytes = new byte[256];
			inStream.read(fileBytes);
			byte[] keyBytes = cipherRSA.doFinal(fileBytes);
			secretKeySpec = new SecretKeySpec(keyBytes, "AES");
			byte[] iv = new byte[128/8];
			inStream.read(iv);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			Cipher cipherAES = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipherAES.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
			decrypted = new File(inputFile.getPath().replace(".enc", ""));
			decrypted = getFileName(decrypted);
			try (FileOutputStream outStream = new FileOutputStream(decrypted)){
				processFile(cipherAES, inStream, outStream);
			}
		}
		return(decrypted);
	}

	public static File getFileName(File file) {
        if (file.exists()){
            String newFileName = file.getName();
            String simpleName = file.getName().substring(0,newFileName.indexOf("."));
            String strDigit="";

            try {
                simpleName = (Integer.parseInt(simpleName)+1+"");
                File newFile = new File(file.getParent()+"/"+simpleName+getExtension(file.getName()));
                return getFileName(newFile);
            }catch (Exception e){}

            for (int i=simpleName.length()-1;i>=0;i--){
                if (!Character.isDigit(simpleName.charAt(i))){
                    strDigit = simpleName.substring(i+1);
                    simpleName = simpleName.substring(0,i+1);
                    break;
                }
            }

            if (strDigit.length()>0){
                simpleName = simpleName+(Integer.parseInt(strDigit)+1);
            }else {
                simpleName+="1";
            }

            File newFile = new File(file.getParent()+"/"+simpleName+getExtension(file.getName()));
            return getFileName(newFile);
        }
        return file;
    }

	public static String getExtension(String name) {
	        return name.substring(name.lastIndexOf("."));
	}
}
