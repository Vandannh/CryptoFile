package encryption;

import java.io.File;

/**
 * A tester for the CryptoUtils class.
 * @author www.codejava.net
 *
 */
public class CryptoUtilsTest {
    public static void main(String[] args) {
        String key = "Mary has one cat";
        File inputFile = new File("files/document.txt");
        File encryptedFile = new File("files/document.encrypted");
        File decryptedFile = new File("files/document.decrypted");
         
        try {
            CryptoUtils.encrypt(key, inputFile, encryptedFile);
            CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}