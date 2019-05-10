package main.java.encryption;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Arrays;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class TestEncrypt
{
	static SecureRandom srandom = new SecureRandom();

	static private void processFile(Cipher ci,InputStream in,OutputStream out)
			throws IllegalBlockSizeException,
			BadPaddingException,
			IOException {
		byte[] ibuf = new byte[1024];
		int len;
		while ((len = in.read(ibuf)) != -1) {
			byte[] obuf = ci.update(ibuf, 0, len);
			if ( obuf != null ) out.write(obuf);
		}
		byte[] obuf = ci.doFinal();
		if ( obuf != null ) out.write(obuf);
	}

	static private void processFile(Cipher ci,String inFile,String outFile)
			throws IllegalBlockSizeException,
			BadPaddingException,
			IOException {
		try (FileInputStream in = new FileInputStream(inFile);
				FileOutputStream out = new FileOutputStream(outFile)) {
			processFile(ci, in, out);
		}
	}

	static private void doGenkey()
			throws NoSuchAlgorithmException,
			IOException {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();
		try (FileOutputStream out = new FileOutputStream("files/rsa.key")) {
			out.write(kp.getPrivate().getEncoded());
		}

		try (FileOutputStream out = new FileOutputStream("files/rsa.pub")) {
			out.write(kp.getPublic().getEncoded());
		}
	}


	static private File doEncryptRSAWithAES(File inputFile, String key)
			throws NoSuchAlgorithmException,
			InvalidAlgorithmParameterException,
			InvalidKeyException,
			InvalidKeySpecException,
			NoSuchPaddingException,
			BadPaddingException,
			IllegalBlockSizeException,
			IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(key));
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey pvt = kf.generatePrivate(ks);

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		SecretKey skey = kgen.generateKey();

		byte[] iv = new byte[128/8];
		srandom.nextBytes(iv);
		IvParameterSpec ivspec = new IvParameterSpec(iv);

		try (FileOutputStream out = new FileOutputStream(inputFile + ".enc")) {
			{
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(Cipher.ENCRYPT_MODE, pvt);
				byte[] b = cipher.doFinal(skey.getEncoded());
				out.write(b);
				System.err.println("AES Key Length: " + b.length);
			}

			out.write(iv);
			System.err.println("IV Length: " + iv.length);

			Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
			try (FileInputStream in = new FileInputStream(inputFile)) {
				processFile(ci, in, out);
			}
		}
		File encrypted = new File(inputFile+".enc");
		return(encrypted);
	}

	static private File doDecryptRSAWithAES(File inputFile, String key)
			throws NoSuchAlgorithmException,
			InvalidAlgorithmParameterException,
			InvalidKeyException,
			InvalidKeySpecException,
			NoSuchPaddingException,
			BadPaddingException,
			IllegalBlockSizeException,
			IOException
	{
		String pubKeyFile = key;
		byte[] bytes = Files.readAllBytes(Paths.get(pubKeyFile));
		X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pub = kf.generatePublic(ks);
		
		String path = inputFile.getPath();
		path.replaceFirst(".enc", "");

		try (FileInputStream in = new FileInputStream(inputFile)) {
			SecretKeySpec skey = null;
			{
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(Cipher.DECRYPT_MODE, pub);
				byte[] b = new byte[256];
				in.read(b);
				byte[] keyb = cipher.doFinal(b);
				skey = new SecretKeySpec(keyb, "AES");
			}

			byte[] iv = new byte[128/8];
			in.read(iv);
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ci.init(Cipher.DECRYPT_MODE, skey, ivspec);

			try (FileOutputStream out = new FileOutputStream(path)){
				processFile(ci, in, out);
			}
		}
		inputFile.delete(); 
		File decrypted = new File(path);
		return(decrypted);
	}
	
	public static void main(String[] args) {
		try {
		doGenkey();
		File encrypted = doEncryptRSAWithAES(new File("files/document.txt"), "files/rsa.key");
//		doDecryptRSAWithAES(encrypted, "files/rsa.pub");
		} catch(Exception e) {
			System.out.println("error");
		}
	}
}
