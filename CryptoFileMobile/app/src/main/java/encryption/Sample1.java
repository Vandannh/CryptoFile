package encryption;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Sample1 {
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(128); // The AES key size in number of bits
		SecretKey secKey = generator.generateKey();

		String plainText = "Please encrypt me urgently...";
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
		byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair keyPair = kpg.generateKeyPair();

		PublicKey puKey = keyPair.getPublic();
		PrivateKey prKey = keyPair.getPrivate();

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.PUBLIC_KEY, puKey);
		byte[] encryptedKey = cipher.doFinal(secKey.getEncoded()/*Seceret Key From Step 1*/);
		
		cipher.init(Cipher.PRIVATE_KEY, prKey);
		byte[] decryptedKey = cipher.doFinal(encryptedKey);


		//Convert bytes to AES SecertKey
		SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length, "AES");
		Cipher aesCipher1 = Cipher.getInstance("AES");
		aesCipher1.init(Cipher.DECRYPT_MODE, originalKey);
		byte[] bytePlainText = aesCipher1.doFinal(byteCipherText);
		String plainText1 = new String(bytePlainText);
	}
}
