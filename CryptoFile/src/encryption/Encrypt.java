package encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
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
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {
	private File original;
	private File encrypted;
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
	private SecretKey skey;
	private IvParameterSpec ivspec;
	private byte[] iv;
	private SecureRandom srandom;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private File pubKeyFile;
	private File pvtKeyFile;

	public Encrypt(File inputFile) {
		original = inputFile;
		encrypted = new File(inputFile + ".enc");
		srandom = new SecureRandom();
		pvtKeyFile = new File("files/private.key");
		pubKeyFile = new File("files/public.key");
	}

	public void genRSA() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();

		publicKey = kp.getPublic();
		privateKey = kp.getPrivate();

		try (FileOutputStream out = new FileOutputStream(pvtKeyFile)) {
			out.write(kp.getPrivate().getEncoded());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (FileOutputStream out = new FileOutputStream(pubKeyFile)) {
			out.write(kp.getPublic().getEncoded());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadRSA() {
		byte[] bytesPub;
		try {
			bytesPub = Files.readAllBytes(Paths.get(pubKeyFile.toURI()));
			X509EncodedKeySpec ksPub = new X509EncodedKeySpec(bytesPub);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			publicKey = kf.generatePublic(ksPub);

			byte[] bytesPvt = Files.readAllBytes(Paths.get(pvtKeyFile.toURI()));
			PKCS8EncodedKeySpec ksPvt = new PKCS8EncodedKeySpec(bytesPvt);
			privateKey = kf.generatePrivate(ksPvt);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}


	public void genAES() throws NoSuchAlgorithmException {
		//Generates the AES key through the use of the KeyGenerator class
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(256);
		skey = kgen.generateKey();
		System.out.println(skey.getEncoded().length);
		//Generates the IV, same size as the AES key.
		iv = new byte[128/8];
		srandom.nextBytes(iv);
		//iv == an array of bytes, ivspec == the iv represented by a class, created from the iv
		ivspec = new IvParameterSpec(iv);
	}

	private void doEncryptRSAWithAES() {
		// Encrypts AES key and writes to the start of the encrypted file
		try (FileOutputStream out = new FileOutputStream(original + ".enc")) {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] b = cipher.doFinal(skey.getEncoded());
			out.write(b);
			out.flush();
			System.err.println("AES Key Length: " + b.length);
			out.write(iv);
			out.flush();
			System.err.println("IV Length: " + iv.length);

			Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
			processFile(ci);
			out.flush();
		} catch (InvalidKeyException | IOException | NoSuchAlgorithmException | 
				NoSuchPaddingException | IllegalBlockSizeException | 
				BadPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	private void doDecryptRSAWithAES() {
		try (FileInputStream in = new FileInputStream(original + ".enc")) {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] b = new byte[256];
			in.read(b);
			byte[] keyb = cipher.doFinal(b);
			
			SecretKeySpec secretkey = new SecretKeySpec(keyb, "AES");

			Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ci.init(Cipher.DECRYPT_MODE, secretkey, ivspec);

			processFile(ci);

		} catch (IOException | NoSuchAlgorithmException | 
				NoSuchPaddingException | InvalidKeyException | 
				IllegalBlockSizeException | BadPaddingException | 
				InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}


	private void processFile(Cipher ci) {
		try(FileInputStream in = new FileInputStream(original); 
				FileOutputStream out = new FileOutputStream(original + ".enc")) {
			byte[] ibuf = new byte[1024];
			int len;
			while ((len = in.read(ibuf)) != -1) {
				byte[] obuf = ci.update(ibuf, 0, len);
				if ( obuf != null ) out.write(obuf);
			}
			byte[] obuf = ci.doFinal();
			if ( obuf != null ) out.write(obuf);
			
		} catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws NoSuchAlgorithmException {
		Encrypt crypt = new Encrypt(new File("files/document.txt"));
		crypt.genAES();
		crypt.genRSA();
//		crypt.loadRSA();
		crypt.doEncryptRSAWithAES();
		crypt.doDecryptRSAWithAES();
	}
}
