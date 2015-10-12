package Encryption;

/**
 * Created by rahulthomas on 03/10/15.
 */
public class Checker {

    public static void main(String[] args) throws Exception {

        String password = "Rahul";
        String timeStamp = RandomString.GetCurrentTimeStamp();
        String savePass = timeStamp + " Mac: " + password;
        RandomString.saveDetails(savePass);
        String passwordEnc = AESencrp.encrypt(password);
        String passwordDec = AESencrp.decrypt(passwordEnc);

        System.out.println("Plain Text : " + password);
        System.out.println("Encrypted Text : " + passwordEnc);
        System.out.println("Decrypted Text : " + passwordDec);
    }
}
