package main.java.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * A tester for the CryptoUtils class.
 * @author www.codejava.net
 *
 */
public class CryptoUtilsTest {
	private static PrivateKey privateKey;
	private static PublicKey publicKey;
	private static File pubKeyFile;
	private static File pvtKeyFile;
	
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String key = "Mary has one cat";
        genRSA();
        File inputFile = new File("files/test.png");
        File encryptedFile = new File("files/test_encrypt.png");
        File decryptedFile = new File("files/test_decrypt.png");
         
        try {
            CryptoUtils.encrypt(convertKeyToString(publicKey), inputFile, encryptedFile);
            CryptoUtils.decrypt(convertKeyToString(privateKey), encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    public static void genRSA() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();
		publicKey = kp.getPublic();
		privateKey = kp.getPrivate();
		pvtKeyFile = new File("files/private.key");
		pubKeyFile = new File("files/public.key");
		try (FileOutputStream out = new FileOutputStream(pvtKeyFile)) {
			out.write(convertKeyToString(privateKey).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (FileOutputStream out = new FileOutputStream(pubKeyFile)) {
			out.write(convertKeyToString(publicKey).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    private static String convertKeyToString(Key key) {
    	byte[] byte_pubkey = key.getEncoded();
		String str_key = Base64.getEncoder().encodeToString(byte_pubkey);
		return str_key;
    }
}