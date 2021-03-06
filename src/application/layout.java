package application;

import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * desktop application to view database data using miglayout.
 * uses jdbc driver to connect to server.
 * uses sql query to fetch data from the database.
 * uses java scheduler function to automatically refresh every set seconds.
 * Created by rahul on 24/12/2015.
 */
public class layout {


    static Connection conn = null;
    static Statement stmt = null;
    private static String databaseURL = "jdbc:mysql://localhost:3306/defaultx?autoReconnect=true&amp;useSSL=false";
    private static String username = "root";
    private static String password = "password";
    private static ResultSet rset = null;
    static JFrame frame;
    /**
     * Labels
     **/
    JLabel top_label1 = new JLabel("Number of Users:        ");
    JLabel top_label2 = new JLabel("Active Users:");
    JLabel top_label3 = new JLabel("Inactive Users:");
    JLabel name_label = new JLabel("Name");
    JLabel email_label = new JLabel("Email");
    JLabel pass_label = new JLabel("One Time Pass");
    JLabel room_label = new JLabel("Room");
    JLabel active_label = new JLabel("Active user?");
    JLabel status_label = new JLabel("Database Status: ");
    JLabel refresh_label = new JLabel("Last updated on ");
    JLabel logo;

    static JLabel top_labelA1 = new JLabel("null");
    static JLabel top_labelA2 = new JLabel("null");
    static JLabel top_labelA3 = new JLabel("null");
    static JLabel status = new JLabel("null");
    static JLabel refresh_status = new JLabel("null");

    /**
     * Text Field
     **/
    static JTextField search_txt = new JTextField(20);
    // JTextField top_labelA2 = new JTextField("null");
    // JTextField top_labelA3 = new JTextField("null");


    /**
     * Text Area
     **/
    static JTextArea names_txtArea = new JTextArea(20, 20);
    static JTextArea emails_txtArea = new JTextArea(20, 20);
    static JTextArea passes_txtArea = new JTextArea(10, 20);
    static JTextArea room_txtArea = new JTextArea(10, 20);
    static JTextArea active_txtArea = new JTextArea(20, 20);

    /**
     * Buttons
     **/
    static JButton refresh_btn = new JButton("Refresh Data");
    static JButton connect_btn = new JButton("Connect to Database");
    static JButton search_btn = new JButton("Search");
    //JButton refresh = new JButton("Refresh Data");

    public layout() {
        frame = new JFrame("Rahul");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocation(100, 100);
        frame.setLayout(new BorderLayout());

        /*** Jpanels **/
        JPanel panel_root = new JPanel(new MigLayout(" "));
        JPanel panel_middle = new JPanel(new MigLayout(""));
        JPanel panel_topLeft = new JPanel(new MigLayout("filly"));
        JPanel panel_topCenter = new JPanel(new MigLayout("fill"));
        JPanel panel_topRight = new JPanel(new MigLayout("fill"));
        JPanel panel_bottom = new JPanel(new MigLayout("fill"));

        frame.add(panel_root);

        /*** Content colours **/
        panel_topLeft.setBackground(Color.darkGray);
        //panel_topCenter.setBackground(Color.WHITE);
        panel_topRight.setBackground(Color.darkGray);
        panel_bottom.setBackground(Color.darkGray);
        top_label1.setForeground(Color.WHITE);
        top_label2.setForeground(Color.WHITE);
        top_label3.setForeground(Color.WHITE);
        top_labelA1.setForeground(Color.WHITE);
        top_labelA2.setForeground(Color.WHITE);
        top_labelA3.setForeground(Color.WHITE);
        status.setForeground(Color.WHITE);
        status_label.setForeground(Color.WHITE);
        refresh_label.setForeground(Color.WHITE);
        refresh_status.setForeground(Color.LIGHT_GRAY);

        /*** Content attributes **/
        names_txtArea.setEditable(false);
        emails_txtArea.setEditable(false);
        passes_txtArea.setEditable(false);
        refresh_btn.setEnabled(false);
        search_btn.setEnabled(true);

        String path = "logo-02.png";
        BufferedImage logo1 = null;
        try {
            logo1 = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        logo = new JLabel(new ImageIcon(logo1));

        panel_root.add(panel_topLeft, "split 3, pushx, growx, sg 1");
        panel_root.add(panel_topCenter, "pushx, growx, sg 1");
        panel_root.add(panel_topRight, "pushx, growx, wrap, sg 1");
        panel_root.add(panel_middle, "push, grow, wrap");
        panel_root.add(panel_bottom, "pushx, growx");


        panel_topLeft.add(top_label1, "split 2, gapy 5, sg c");
        panel_topLeft.add(top_labelA1, "pushx, growx, right, wrap, sg b");
        panel_topLeft.add(top_label2, "split 2, sg c ");
        panel_topLeft.add(top_labelA2, "pushx, growx, wrap, sg b");
        panel_topLeft.add(top_label3, "split 2, sg c");
        panel_topCenter.add(logo, "align center");
        panel_topLeft.add(top_labelA3, "pushx, growx, wrap, sg b");
        panel_topRight.add(refresh_btn, "pushx, growx, align right, wrap, sg d");
        panel_topRight.add(connect_btn, "pushx, growx, align right, wrap, sg d");

        panel_middle.add(name_label, "split 3,align left, pushx, growx, gapy 10, sg a");
        panel_middle.add(email_label, "align center,pushx, growx, sg a");
        panel_middle.add(pass_label, "align right,pushx, growx, sg a");
        panel_middle.add(room_label, "align right,pushx, growx, sg a");
        panel_middle.add(active_label, "align right,pushx, growx, wrap, sg a");

        panel_bottom.add(status_label, "split 2, align left");
        panel_bottom.add(status, "align left");
        panel_bottom.add(search_txt, "split 2, align right, pushx");
        panel_bottom.add(search_btn, "align center, pushx");
        panel_bottom.add(refresh_label, "split 2,align right, pushx");
        panel_bottom.add(refresh_status, "pushx, wrap");

        panel_middle.add(new JScrollPane(names_txtArea), "align left, split 3, push, grow");
        panel_middle.add(new JScrollPane(emails_txtArea), "align center,push, grow");
        panel_middle.add(new JScrollPane(passes_txtArea), "align right,push, grow");
        panel_middle.add(new JScrollPane(room_txtArea), "align right,push, grow");
        panel_middle.add(new JScrollPane(active_txtArea), "align right,push, grow, wrap");

        status.setText("Disconnected");
        status.setForeground(Color.red);

        frame.setContentPane(panel_root);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * function to fetch data from the sql database using sql query and update the relevant
     * text area with the data
     */
    private static void getData() {
        try {
            stmt = conn.createStatement();
            // We shall manage our transaction (because multiple SQL statements issued)
            conn.setAutoCommit(false);
            rset = stmt.executeQuery("SELECT fname, email, pass, room, active FROM users");
            int totalCount = 0, activeCount = 0, inActiveCount = 0;
            emails_txtArea.setText(null);
            names_txtArea.setText(null);
            passes_txtArea.setText(null);
            active_txtArea.setText(null);
            room_txtArea.setText(null);
            // displaying records
            while (rset.next()) {
                names_txtArea.append(rset.getString("fname") + "\n-------------------------------------\n");
                emails_txtArea.append(rset.getString("email") + "\n-------------------------------------\n");
                active_txtArea.append(rset.getString("active") + "\n-------------------------------------\n");
                passes_txtArea.append(rset.getString("pass") + "\n-------------------------------------\n");
                room_txtArea.append(rset.getString("room") + "\n-------------------------------------\n");
                if (rset.getString("active").equals("yes"))
                    activeCount++;
                else
                    inActiveCount++;
                totalCount++;
            }
            top_labelA1.setText(String.valueOf(totalCount));
            top_labelA2.setText(String.valueOf(activeCount));
            top_labelA3.setText(String.valueOf(inActiveCount));
            conn.commit();

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * function to get connection to the sql database using jdbc driver
     */
    private static void connectToDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(databaseURL, username, password);
            status.setText("Connected");
            status.setForeground(Color.green);
            refresh_btn.setEnabled(true);
            connect_btn.setEnabled(false);

        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
            connect_btn.setEnabled(true);
        } catch (SQLException e1) {
            e1.printStackTrace();
            status.setText("Disconnected");
            status.setForeground(Color.red);
            connect_btn.setEnabled(true);
        }
    }

    /**
     * function to search for requested data in the database and to show the result
     * @param detail
     * @throws SQLException
     */
    public static void search(String detail) throws SQLException {
        stmt = conn.createStatement();
        PreparedStatement pstmt = null;
        // We shall manage our transaction (because multiple SQL statements issued)
        conn.setAutoCommit(false);
        int totalCount = 0, activeCount = 0, inActiveCount = 0;
        emails_txtArea.setText(null);
        names_txtArea.setText(null);
        passes_txtArea.setText(null);
        active_txtArea.setText(null);
        room_txtArea.setText(null);

        if (detail.contains("@")) { //check to see if it's an email
            pstmt = conn.prepareStatement("SELECT fname, email, pass, active, room FROM users WHERE email = ?");
            pstmt.setString(1, detail);
        } else if (detail.matches("^[0-9]{3}$")) { //check to see if its a room number which has 4 numbers between 0 and 9
            pstmt = conn.prepareStatement("SELECT fname, email, pass, active, room FROM users WHERE room = ?");
            pstmt.setString(1, detail);
        } else { //last search option is name
            pstmt = conn.prepareStatement("SELECT fname, email, pass, active, room FROM users WHERE fname = ?");
            pstmt.setString(1, detail);
        }

        rset = pstmt.executeQuery();

        // displaying records
        while (rset.next()) {
            names_txtArea.append(rset.getString("fname") + "\n-------------------------------------\n");
            emails_txtArea.append(rset.getString("email") + "\n-------------------------------------\n");
            active_txtArea.append(rset.getString("active") + "\n-------------------------------------\n");
            passes_txtArea.append(rset.getString("pass") + "\n-------------------------------------\n");
            room_txtArea.append(rset.getString("room") + "\n-------------------------------------\n");
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            ScheduledFuture<?> handle;
            boolean finishedSearch = false;

            @Override
            public void run() {
                new layout();

                /** Schedule Executor service to refresh data ever set seconds **/
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                Runnable toRun = new Runnable() {
                    public void run() {
                        Date date = new Date(System.currentTimeMillis());

                        /*//just Time
                        DateFormat df2 = new SimpleDateFormat("HHmmss");
                        System.out.println(df2.format(date));*/

                        //both Date and Time
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy @ HH:mm:ss");
                        System.out.println("Updated at: " + df.format(date));

                        refresh_status.setText(String.valueOf(df.format(date)));
                        getData();
                    }
                };

                refresh_btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        finishedSearch = true;
                        toRun.run();
                        if (finishedSearch = true) {
                            handle = scheduler.scheduleAtFixedRate(toRun, 1, 25, TimeUnit.SECONDS); //wait 1 sec after starting and then every 25 sec
                            finishedSearch = false;
                        }
                    }
                });

                connect_btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        connectToDatabase();
                        handle = scheduler.scheduleAtFixedRate(toRun, 1, 25, TimeUnit.SECONDS); //wait 1 sec after starting and then every 25 sec
                    }
                });

                search_btn.addActionListener(new ActionListener() {
                                                 @Override
                                                 public void actionPerformed(ActionEvent e) {
                                                     if (search_txt.getText() != null) {
                                                         handle.cancel(true);
                                                         connectToDatabase();
                                                         try {
                                                             search(search_txt.getText());
                                                         } catch (SQLException e1) {
                                                             e1.printStackTrace();
                                                         }
                                                     }
                                                 }
                                             }

                );

                /** Safely close all connections and stop all tasks before exiting **/
                frame.addWindowListener(new

                                                WindowAdapter() {
                                                    @Override
                                                    public void windowClosing(WindowEvent e) {
                                                        if (JOptionPane.showConfirmDialog(frame,
                                                                "Are you sure to exit the app?", "Quit",
                                                                JOptionPane.YES_NO_OPTION,
                                                                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                                                            if (!scheduler.isShutdown())
                                                                scheduler.shutdown();

                                                            if (rset != null)
                                                                try {
                                                                    rset.close();
                                                                } catch (SQLException e1) {
                                                                }
                                                            if (stmt != null)
                                                                try {
                                                                    stmt.close();
                                                                } catch (SQLException e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                            if (conn != null)
                                                                try {
                                                                    conn.close();
                                                                } catch (SQLException e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                            System.exit(0);
                                                        } else {

                                                        }
                                                        // super.windowClosing(e);
                                                    }
                                                }

                );
            }
        });
    }
}

