package Encryption;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.*;

import static javax.mail.Transport.send;

/**
 * Created by rahul on 10/11/2015.
 * worker thread that processes the request so server can focus on accepting connections
 */
public class WorkerRunnable implements Runnable {
    protected Socket clientSocket = null;
    protected String serverText = null;
    static Connection conn = null;
    static Statement stmt = null;
    private static String databaseURL = "jdbc:mysql://localhost:3306/defaultx?autoReconnect=true&amp;useSSL=false";
    private static String username = "root";
    private static String password = "password";
    private static ResultSet rset = null;
    private String passwordEnc = null;
    private String passwordDec = null;

    private BufferedWriter outputD;
    private BufferedReader inputD;
    private Socket connection;
    private String message = "";

    //room door ip addresses
    private String room101 = "192.168.1.100";

    //port for all doors
    private static int doorPort = 8080;


    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
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
            System.out.println("Recieved message: " + in_msg);
            System.out.println(in_msg.split(",")[1].trim());


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
                    //out.writeUTF(passwordEnc); //send encrypted password to phone
                    connectToDatabase();
                    String room = getData(in_msg);
                    out.writeUTF(room);
                    out.flush();
                    connectToDoor(room101);
                    sendMessageToDoor("keycard id");
                    closeDoorStreams();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (in_msg.split(",")[1].trim().equalsIgnoreCase("getPass")) {
                String email = in_msg.split(",")[0].toLowerCase();
                System.out.println("Email Recieved: " + email);
                connectToDatabase();
                String pass = getData(in_msg);
                System.out.println("Pass Code: " + pass);
                if (pass != null) {
                    out.writeUTF(pass);
                    out.flush();
                } else {
                    out.close();
                    input.close();
                }
            } else if (in_msg.split(",")[1].trim().equalsIgnoreCase("newPass")) {
                String email = in_msg.split(",")[0].toLowerCase();
                System.out.println("Email Recieved: " + email);
                connectToDatabase();
                String pass = getData(in_msg);
                if (pass != null) {
                    out.writeUTF(pass);
                    out.flush();
                } else {
                    out.close();
                    input.close();
                }
            }

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

    /**
     * function to get connection to the sql database using jdbc driver
     */
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

    /**
     * function to fetch data from the sql database using sql query
     *
     * @param data
     * @return
     */
    private static String getData(String data) {
        String pass = null;
        String result = null;
        //String email = null;
        int newPass;

        try {
            stmt = conn.createStatement();
            // We shall manage our transaction (because multiple SQL statements issued)
            conn.setAutoCommit(false);

            System.out.println("Type of request: " + data.split(",")[1]); //for debugging

            if (data.split(",")[1].equalsIgnoreCase("getPass")) {
                String email = data.split(",")[0];
                rset = stmt.executeQuery("SELECT pass FROM users WHERE email  = " + "'" + email + "'");
                String test = "SELECT pass FROM users WHERE email  = " + "'" + data + "'";
                System.out.println("SQL: " + test);
                if (rset.next())
                    pass = rset.getString("pass");
                result = pass;
            } else if (data.split(",")[1].equalsIgnoreCase("newPass")) {
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
                System.out.println("Password for: " + userEmail + " is changed to: " + newPass);
                result = String.valueOf(newPass);
                if (!isInternetReachable())
                    System.out.println("No internet connection to send Email!");
                else {
                    System.out.println("Internet connection is available!");
                    sendEmail(userEmail);
                }
            } else if (data.split(",")[1].equalsIgnoreCase("mac")) {
                String email = data.split(",")[2];
                String room = null;
                rset = stmt.executeQuery("SELECT room FROM users WHERE email  = " + "'" + email + "'");
                if (rset.next())
                    room = rset.getString("room");
                result = room;
                System.out.println("Room number of " + email + " is: " + room);
            }
            conn.commit();
            conn.close();

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return result;
    }

    /**
     * update data on the database using sql query
     *
     * @param data
     * @param email
     */
    private static void setData(String data, String email) {
        connectToDatabase();
        System.out.println("Set data to : " + data);
        System.out.println("Set data to Email : " + email);
        try {
            stmt = conn.createStatement();
            // We shall manage our transaction (because multiple SQL statements issued)
            conn.setAutoCommit(false);
            stmt.executeUpdate("UPDATE users SET active = " + "'" + data + "'" + "WHERE email =" + "'" + email + "'");

            conn.commit();
            conn.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * checks for connection to the internet through dummy request
     *
     * @return
     */
    public static boolean isInternetReachable() {
        try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * function to send html email to user
     *
     * @param email
     */
    public static void sendEmail(String email) {
        // Recipient's email ID needs to be mentioned.
        String to = email;

        // Sender's email ID needs to be mentioned
        String from = "noreply@keylesskey.com";

        // Assuming you are sending email from localhost
        String host = "192.169.1.15";//"localhost";

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
        } catch (javax.mail.MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to get connected to the requested door for data transfer
     * @param ipAddress
     */
    public void connectToDoor(String ipAddress) {
        while (true) {
            try {
                tryToConnect(ipAddress, doorPort);
                setupStreams();
                //commWithDoor();
            } catch (EOFException eofException) {
                showMessage("Client terminated connection");
            } catch (IOException ioException) {
                showMessage("Could not connect...");
            } finally {
                //closeDoorStreams();
            }
        }
    }

    /**
     * Function to get a socket connection to requested door using ip address and port number
     * @param ip
     * @param port
     * @throws IOException
     */
    private void tryToConnect(String ip, int port) throws IOException {
        System.out.println("Attempting to connect to address " + ip);
        connection = new Socket(ip, port); //once someone asks to connect, it accepts the connection to the socket this gets repeated fast
        System.out.println("Now connected to " + connection.getInetAddress().getHostName()); //shows IP adress of client
    }

    /**
     * function to setup and start input and output streams for communication
     * @throws IOException
     */
    private void setupStreams() throws IOException {
        System.out.println("creating streams...");
        outputD = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        outputD.flush();
        inputD = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        System.out.println("Streams are setup!");
    }

    //for debug
    private void commWithDoor() throws IOException {
        sendMessageToDoor("hello server \n");
        // ableToType(true); //makes the user able to type
        do {
            char x = (char) inputD.read();
            while (x != '\n') {
                message += x;
                x = (char) inputD.read();
            }
            if (!message.isEmpty() && message.length() > 2) {
                try {
                    Thread.sleep(1000);     //1000 milliseconds is one second.
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Recieved: " + message);
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                System.out.println("Enter message for Galileo: ");
                String msg = reader.nextLine(); // Scans the next token of the input as an int.
                sendMessageToDoor(msg);
                message = "";
            }

        } while (!message.equals("END")); //if the user has not disconnected, by sending "END"

    }

    /**
     * function to print message on screen
     * @param message
     */
    private void showMessage(final String message) {
        System.out.println(message);
    }

    /**
     * function to send a message(String) to the door
     * @param message
     */
    private void sendMessageToDoor(String message) {
        try {
            outputD.write(message + '\n');
            outputD.flush();
            showMessage("Sent: " + message);
        } catch (IOException ex) {
            showMessage("\nSomething messed up whilst sending messages...");
        }
    }

    /**
     * function to close all the in/out streams with the door
     */
    private void closeDoorStreams() {
        showMessage("Closing streams...");
        try {
            outputD.close();
            inputD.close();
            connection.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
