package Encryption;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Random;

/**
 * Created by g00275669 on 08/10/2015.
 */
public class RandomString {

    public static String generateString(Random rng, String characters, int length)
    {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    /** write details to text **/
    public static void saveDetails(String data){
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)))) {
            out.println(data);
            out.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** get current time **/
    public static String  GetCurrentTimeStamp(){
            java.util.Date date= new java.util.Date();
            String timeStamp = String.valueOf(new Timestamp(date.getTime()));
        return timeStamp;
    }
}
