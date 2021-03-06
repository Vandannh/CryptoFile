package encryption;

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
 * @author Daniel H�gg
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
	public static File encrypt(File inputFile, String key)throws Exception{
		byte[] keyBytes = Files.readAllBytes(Paths.get(key));
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		SecretKey secretKey = keyGen.generateKey();

		byte[] iv = new byte[128/8];
		sRandom.nextBytes(iv);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);

		try (FileOutputStream outStream = new FileOutputStream(inputFile + ".enc")) {
			Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipherRSA.init(Cipher.ENCRYPT_MODE, privateKey);
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
	public static File decrypt(File inputFile, String key) throws Exception{
		File decrypted = null;
		String pubKeyFile = key;
		byte[] bytes = Files.readAllBytes(Paths.get(pubKeyFile));
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);

		try (FileInputStream inStream = new FileInputStream(inputFile)) {
			SecretKeySpec secretKeySpec = null;
			Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipherRSA.init(Cipher.DECRYPT_MODE, publicKey);
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
			try (FileOutputStream outStream = new FileOutputStream(decrypted)){
				processFile(cipherAES, inStream, outStream);
			}
		}
		return(decrypted);
	}
}
