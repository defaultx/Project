package Encryption;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by rahul on 10/11/2015.
 *  worker thread that processes the request so server can focus on accepting connections
 */
public class WorkerRunnable implements Runnable{
    protected Socket clientSocket = null;
    protected String serverText   = null;
    String macAddress = null;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            OutputStream output = clientSocket.getOutputStream();
            macAddress = input.readUTF();
            System.out.println("Received from client: " + macAddress);
            long time = System.currentTimeMillis();
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
                    this.serverText + " - " +
                    time +
                    "").getBytes());
            output.close();
            input.close();
            System.out.println("Request processed: " + time);
            String passwordEnc = null;
            String passwordDec = null;
            String timeStamp = RandomString.GetCurrentTimeStamp();
            String saveMac = timeStamp + " Mac: " + macAddress;
            try {
                RandomString.saveDetails(saveMac);
                passwordEnc = AESencrp.encrypt(macAddress);
                passwordDec = AESencrp.decrypt(passwordEnc);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Encrypted Text : " + passwordEnc);
            System.out.println("Decrypted Text : " + passwordDec);

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

}
