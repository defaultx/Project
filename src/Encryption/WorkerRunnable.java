package Encryption;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.sql.*;

/**
 * Created by rahul on 10/11/2015.
 *  worker thread that processes the request so server can focus on accepting connections
 */
public class WorkerRunnable implements Runnable{
    protected Socket clientSocket = null;
    protected String serverText   = null;
    String macAddress = null, email = null, in_msg = null;
    static Connection conn = null;
    static Statement stmt = null;
    private static String databaseURL = "jdbc:mysql://localhost:3306/defaultx?autoReconnect=true&amp;useSSL=false";
    private static String username = "root";
    private static String password = "password";
    private static ResultSet rset = null;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            OutputStream output = clientSocket.getOutputStream();
            in_msg = input.readUTF();
            if(in_msg.indexOf('@') >= 0) {
                email = in_msg;
                System.out.println("Email Recieved: " + email);
                connectToDatabse();
                String pass = getData(email);
                System.out.println("Pass Code: " +pass);
                out.writeUTF(pass);
                out.flush();
                //out.close();
            }
            else
            macAddress = in_msg;

            //macAddress = input.readUTF();
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

    private static void connectToDatabse() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(databaseURL, username, password);

        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private static String getData(String email) {
        String pass = null;
        try {
            stmt = conn.createStatement();
            // We shall manage our transaction (because multiple SQL statements issued)
            conn.setAutoCommit(false);
            rset = stmt.executeQuery("SELECT pass FROM users WHERE email  = " + "'" + email + "'");
            String test = "SELECT pass FROM users WHERE email  = " + "'" + email + "'";
            System.out.println("SQL: "+test);
            if (rset.next())
            pass = rset.getString("pass");
            conn.commit();

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return pass;
    }

}
