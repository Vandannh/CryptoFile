package encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class Sample {
	private static PublicKey publicKey;
	private static PrivateKey privateKey;
    public static void main(String [] args) throws Exception {
        // generate public and private keys
        KeyPair keyPair = buildKeyPair();
       
        generateKeys(keyPair);
        
        File file = new File("files/test.png");
//        System.out.println(new String(readFileToByteArray(file)));
        System.out.println(readFileToByteArray(file).length);
        
        // sign the message
        byte [] signed = encrypt(privateKey, file);     
        System.out.println(new String(signed));  // <<signed message>>
        
        // verify the message
        byte[] verified = decrypt(publicKey, signed);                                 
        System.out.println(new String(verified));     // This is a secret message
    }

    public static void generateKeys(KeyPair keyPair) {
    	publicKey = keyPair.getPublic();
    	privateKey = keyPair.getPrivate();
    }
    public static PublicKey getPublicKey() {
    	return publicKey;
    }
    public static PrivateKey getPrivateKey() {
		return privateKey;
    }
    
    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);      
        return keyPairGenerator.genKeyPair();
    }

    public static byte[] encrypt(Key key, File file) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");  
        cipher.init(Cipher.ENCRYPT_MODE, key);  
    	byte[] bytes = new byte[1440948];
        try (FileOutputStream out = new FileOutputStream(new File("files/test.enc"));
        	FileInputStream in = new FileInputStream(file)) {
        	in.read(bytes);
        	System.out.println(bytes.length);
        	out.write(cipher.doFinal(bytes));
        }
        

        return cipher.doFinal(bytes);  
    }
    
    public static byte[] decrypt(Key key, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");  
        cipher.init(Cipher.DECRYPT_MODE, key);
        try (FileOutputStream out = new FileOutputStream(new File("files/test1.png"))) {
        	out.write(cipher.doFinal(encrypted));
        }
        return cipher.doFinal(encrypted);
    }
    
    private static byte[] readFileToByteArray(File file){
        FileInputStream fis = null;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try{
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();        
            
        }catch(IOException ioExp){
            ioExp.printStackTrace();
        }
        return bArray;
    }
}