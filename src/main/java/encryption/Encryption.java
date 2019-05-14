package main.java.encryption;

import java.nio.file.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import main.java.azure.*;

public class Encryption{
	static SecureRandom srandom = new SecureRandom();

	private static void processFile(Cipher ci,InputStream in,OutputStream out) throws IllegalBlockSizeException,BadPaddingException,IOException {
		byte[] ibuf = new byte[1024];
		int len;
		while ((len = in.read(ibuf)) != -1) {
			byte[] obuf = ci.update(ibuf, 0, len);
			if ( obuf != null ) out.write(obuf);
		}
		byte[] obuf = ci.doFinal();
		if ( obuf != null ) out.write(obuf);
	}

	private static void processFile(Cipher ci,String inFile,String outFile) throws IllegalBlockSizeException, BadPaddingException, IOException {
		try (FileInputStream in = new FileInputStream(inFile);
				FileOutputStream out = new FileOutputStream(outFile)) {
			processFile(ci, in, out);
		}
	}

	public static KeyPair doGenkey() throws NoSuchAlgorithmException,IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();
		try (FileOutputStream out = new FileOutputStream("temp/rsa.key")) {
			out.write(kp.getPrivate().getEncoded());
		}
		try (FileOutputStream out = new FileOutputStream("temp/rsa.pub")) {
			out.write(kp.getPublic().getEncoded());
		}
		return kp;
	}
	
	public static File encrypt(File inputFile, String key)throws Exception{
		System.out.println(key);
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
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pvt);
			byte[] b = cipher.doFinal(skey.getEncoded());
			out.write(b);
			System.err.println("AES Key Length: " + b.length);
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

	public static File decrypt(File inputFile, String key) throws Exception{
		File decrypted = null;
		String pubKeyFile = key;
		byte[] bytes = Files.readAllBytes(Paths.get(pubKeyFile));
		X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pub = kf.generatePublic(ks);

		try (FileInputStream in = new FileInputStream(inputFile)) {
			SecretKeySpec skey = null;
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, pub);
			byte[] b = new byte[256];
			in.read(b);
			byte[] keyb = cipher.doFinal(b);
			skey = new SecretKeySpec(keyb, "AES");
			byte[] iv = new byte[128/8];
			in.read(iv);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ci.init(Cipher.DECRYPT_MODE, skey, ivspec);
			decrypted = new File(inputFile.getPath().replace(".enc", ""));
			try (FileOutputStream out = new FileOutputStream(decrypted)){
				processFile(ci, in, out);
			}
		}
		return(decrypted);
	}
}
