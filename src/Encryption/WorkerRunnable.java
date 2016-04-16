package Encryption;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import sun.plugin2.message.Message;
import sun.plugin2.message.transport.Transport;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * Created by rahul on 10/11/2015.
 *  worker thread that processes the request so server can focus on accepting connections
 */
public class WorkerRunnable implements Runnable{
    protected Socket clientSocket = null;
    protected String serverText   = null;
    static Connection conn = null;
    static Statement stmt = null;
    private static String databaseURL = "jdbc:mysql://localhost:3306/defaultx?autoReconnect=true&amp;useSSL=false";
    private static String username = "root";
    private static String password = "password";
    private static ResultSet rset = null;
    private String passwordEnc = null;
    private String passwordDec = null;


    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        String macAddress = null, in_msg = null;

        try {
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            OutputStream output = clientSocket.getOutputStream();
            long time = System.currentTimeMillis();
            //just Time
            DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
            //System.out.println(df2.format(time));
            //while (input.available()> 0){
            in_msg = input.readUTF();
            System.out.println("Recieved message: "+in_msg);
            //}
            //if(in_msg != null) {
            //System.out.println(in_msg.split(",")[1]);
            if (in_msg.split(",")[1].compareTo("getPass") > 0) {
                String email = in_msg.split(",")[0].toLowerCase();
                System.out.println("Email Recieved: " + email);
                connectToDatabase();
                String pass = getData(in_msg);
                System.out.println("Pass Code: " + pass);
                if(pass !=null) {
                    out.writeUTF(pass);
                    out.flush();
                }else {
                    out.close();
                    input.close();
                }
                //out.close();
            } else if (in_msg.contains("mac")) {
                macAddress = in_msg.split(",")[0];
                String email = in_msg.split(",")[2].toString();
                try {
                    passwordEnc = AESencrp.encrypt(macAddress);
                    System.out.println("Received from client: " + macAddress);
                    System.out.println("Request processed: " + df2.format(time));
                    String timeStamp = RandomString.GetCurrentTimeStamp();
                    String saveMac = timeStamp + " Mac: " + macAddress;
                    RandomString.saveDetails(saveMac);
                    System.out.println("Encrypted Text : " + passwordEnc);
                    setData("yes", email);
                    out.writeUTF(passwordEnc);
                    out.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (in_msg.split(",")[1] == "mac") {

            }
            // }

            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
                    this.serverText + " - " +
                    time +
                    "").getBytes());
            output.close();
            input.close();

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

    private static void connectToDatabase() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(databaseURL, username, password);

        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private static String getData(String data) {
        String pass = null;
        String result = null;
        String email = null;
        try {
            stmt = conn.createStatement();
            // We shall manage our transaction (because multiple SQL statements issued)
            conn.setAutoCommit(false);

            System.out.println("Type of request: " + data.split(",")[1]); //for debugging

            if(data.split(",")[1].compareTo("getPass") > 0) {
                email = data.split(",")[0];
                rset = stmt.executeQuery("SELECT pass FROM users WHERE email  = " + "'" + email + "'");
                String test = "SELECT pass FROM users WHERE email  = " + "'" + data + "'";
                System.out.println("SQL: " + test);
                if (rset.next())
                    pass = rset.getString("pass");
                result = pass;
            }
            else if(data.split(",")[1].compareTo("newPass") > 0) {
                String userEmail = data.split(",")[0];
                System.out.println("*****email******" + data.split(",")[0] + "************"); //for debugging
                result = "newPass";

            }
            conn.commit();
            conn.close();

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        System.out.println("*********"+result+"***********");
        return result;
    }

    private static void setData(String data, String email) {
        connectToDatabase();
        System.out.println("Seta data to : "+ data);
        System.out.println("Seta data to Email : "+ email);
        try {
            stmt = conn.createStatement();
            // We shall manage our transaction (because multiple SQL statements issued)
            conn.setAutoCommit(false);
            stmt.executeUpdate("UPDATE users SET active = " + "'" + data + "'" + "WHERE email =" + "'" + email + "'" );

            conn.commit();
            conn.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

}
