package Encryption;
/**
 * Created by rahulthomas on 03/10/15.
 * <p>
 * AES (acronym of Advanced Encryption Standard) is a symmetric encryption algorithm.
 * The algorithm was developed by two Belgian cryptographer Joan Daemen and Vincent Rijmen.
 * AES was designed to be efficient in both hardware and software, and supports a block length of
 * 128 bits and key lengths of 128, 192, and 256 bits.
 * AES encryption is used by U.S. for securing sensitive but unclassified material, so we can say it is enough secure.
 **/

import java.nio.charset.Charset;
import java.security.*;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.*;

public class AESencrp {

    private static final String ALGO = "AES";
    private static byte[] keyValue;// = getKeyValueString();

    /** create a random string to use as keyword for encryption **/
    private static String getRandomString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrst";
        Random rng = new Random();
        String usedKey = RandomString.generateString(rng, chars, 15);
        return usedKey;
    }

    /**
     * function to use SHA-1 to generate a hash from your key and trim the result to 128 bit (16 bytes)
     * @return
     */
    public static byte[] getKeyValueString() {
        String keyValueString = getRandomString();
        System.out.println("Generated String: " + keyValueString);
        byte[] key = (keyValueString).getBytes(Charset.forName("UTF-8"));
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1"); // produces a 160-bit (20-byte) hash value
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] keyb = sha.digest(key);
        keyb = Arrays.copyOf(key, 16); // use only first 128 bit
        return keyb;
    }

    /**
     * function to encrypt the string with generated key
     * @param Data
     * @return
     * @throws Exception
     */
    public static String encrypt(String Data) throws Exception {
        System.out.println("Mac Address: " + Data);
        String timeStamp = RandomString.GetCurrentTimeStamp();
        //Checker.setMacAddress(Data);
        Key key = generateKey();
        String saveKey = timeStamp + " Key: " + keyValue;
        RandomString.saveDetails(saveKey);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    /**
     * function to decrypt encrypted string using AES algorithm
     * @param encryptedData
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    /**
     * function to generate a secret key for the encryption
     * @return
     * @throws Exception
     */
    private static Key generateKey() throws Exception {
        keyValue = getKeyValueString(); //see if this generates random key string every request
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }
}
