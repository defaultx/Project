package Encryption;

/**
 * Created by rahulthomas on 03/10/15.
 */
public class Checker {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);
        new Thread(server).start();

        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //String password = WorkerRunnable.getMacAddress();
        //System.out.println("Plain Text : " + password);
        // String timeStamp = RandomString.GetCurrentTimeStamp();
        // String savePass = timeStamp + " Mac: " + password;
        // RandomString.saveDetails(savePass);
        // String passwordEnc = AESencrp.encrypt(password);
        // String passwordDec = AESencrp.decrypt(passwordEnc);

//        System.out.println("Plain Text : " + password);
//        System.out.println("Encrypted Text : " + passwordEnc);
//        System.out.println("Decrypted Text : " + passwordDec);
    }

    /*public static void setMacAddress(String mac){
        System.out.println("Plain Text : " + mac);
    }*/
}
