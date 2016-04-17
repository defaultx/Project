package Encryption;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import sun.plugin2.message.Message;
import sun.plugin2.message.transport.Transport;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import static javax.mail.Transport.send;

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
            System.out.println(in_msg.split(",")[1].trim());

            //}
            //if(in_msg != null) {
            //System.out.println(in_msg.split(",")[1]);
            if (in_msg.split(",")[1].trim().equalsIgnoreCase("mac")) {
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
            }else if (in_msg.split(",")[1].trim().equalsIgnoreCase("getPass")) {
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
            } else if (in_msg.split(",")[1].trim().equalsIgnoreCase("newPass")) {
                String email = in_msg.split(",")[0].toLowerCase();
                System.out.println("Email Recieved: " + email);
                connectToDatabase();
                String pass = getData(in_msg);
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
        int newPass;

        try {
            stmt = conn.createStatement();
            // We shall manage our transaction (because multiple SQL statements issued)
            conn.setAutoCommit(false);

            System.out.println("Type of request: " + data.split(",")[1]); //for debugging
            System.out.println(data.split(",")[1].equals("newPass"));

            if(data.split(",")[1].equalsIgnoreCase("getPass")) {
                email = data.split(",")[0];
                rset = stmt.executeQuery("SELECT pass FROM users WHERE email  = " + "'" + email + "'");
                String test = "SELECT pass FROM users WHERE email  = " + "'" + data + "'";
                System.out.println("SQL: " + test);
                if (rset.next())
                    pass = rset.getString("pass");
                result = pass;
            }
            else if(data.split(",")[1].equalsIgnoreCase("newPass")) {
                String userEmail = data.split(",")[0];
                //System.out.println("*****email******" + data.split(",")[0]); //for debugging
                //result = "newPass";
                newPass = RandomString.getRandomPass();
                System.out.println("Generated pass: " + newPass);
                PreparedStatement stmt1 = conn.prepareStatement("UPDATE users SET pass = ? WHERE email  = ?");
                stmt1.setInt(1, newPass);
                stmt1.setString(2, userEmail);
                stmt1.executeUpdate();
                stmt1.close();
                System.out.println("Password for: "+userEmail + " is changed to: " + newPass);
                result = String.valueOf(newPass);
                if(!isInternetReachable())
                    System.out.println("No internet connection to send Email!");
                else {
                    System.out.println("Internet connection is available!");
                    //sendEmail(userEmail);
                }


            }
            conn.commit();
            conn.close();

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return result;
    }

    /**
     * update data on the database
     * @param data
     * @param email
     */
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

    /**
     * checks for connection to the internet through dummy request
     * @return
     */
    public static boolean isInternetReachable()
    {
        try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * function to send html email to user
     * @param email
     */
    public static void sendEmail(String email) {
        // Recipient's email ID needs to be mentioned.
        String to = email;

        // Sender's email ID needs to be mentioned
        String from = "noreply@keylesskey.com";

        // Assuming you are sending email from localhost
        String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Send the actual HTML message, as big as you like
            message.setContent("<h1>This is actual message</h1>", "text/html");

            // Send message
            send(message);
            System.out.println("Sent message successfully....");
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (javax.mail.MessagingException e) {
            e.printStackTrace();
        }
    }

}
