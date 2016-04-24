package Encryption;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Random;

/**
 * Created by g00275669 on 08/10/2015.
 * Random string class used to generate random strings, password, get current time and save details to text file
 */
public class RandomString {

    /**
     * function to generate a random string
     *
     * @param rng
     * @param characters
     * @param length
     * @return
     */
    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    /**
     * function to save the details to a text file
     *
     * @param data
     */
    public static void saveDetails(String data) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)))) {
            out.println(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * function to get current time
     *
     * @return
     */
    public static String GetCurrentTimeStamp() {
        java.util.Date date = new java.util.Date();
        String timeStamp = String.valueOf(new Timestamp(date.getTime()));
        return timeStamp;
    }

    /**
     * function to get a secure random  5 digits password
     *
     * @return
     */
    public static int getRandomPass() {
        Random rand = new SecureRandom();
        int min = 10000;
        int max = 99999;
        int pass = rand.nextInt(max - min);
        return pass;
    }
}
