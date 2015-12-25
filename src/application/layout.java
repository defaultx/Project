package application;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by rahul on 24/12/2015.
 */
public class layout {

    /**
     * Labels
     **/
    JLabel top_label1 = new JLabel("Number of Users:        ");
    JLabel top_label2 = new JLabel("Active Users:");
    JLabel top_label3 = new JLabel("Inactive Users:");
    JLabel name_label = new JLabel("Name");
    JLabel email_label = new JLabel("Email");
    JLabel pass_label = new JLabel("One Time Pass");
    JLabel status_label = new JLabel("Application Status: ");
    JLabel logo = new JLabel("logo goes here");

    JLabel top_labelA1 = new JLabel("null");
    JLabel top_labelA2 = new JLabel("null");
    JLabel top_labelA3 = new JLabel("null");
    JLabel status = new JLabel("null");

    /**
     * Text Field
     **/
    // JTextField top_labelA1 = new JTextField("null");
    // JTextField top_labelA2 = new JTextField("null");
    // JTextField top_labelA3 = new JTextField("null");


    /**
     * Text Area
     **/
    JTextArea names_txtArea = new JTextArea(20,20);
    JTextArea emails_txtArea = new JTextArea(20,20);
    JTextArea passes_txtArea = new JTextArea(20,20);

    /**
     * Buttons
     **/
    JButton refresh_btn = new JButton("Refresh Data");
    JButton connect_btn = new JButton("Connect to Database");
    //JButton refresh = new JButton("Refresh Data");

    public layout() {
        JFrame frame = new JFrame("Rahul");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocation(300, 400);
        frame.setLayout(new BorderLayout() );

        /*** Jpanels **/
        JPanel panel_root = new JPanel(new MigLayout(" "));
        JPanel panel_middle = new JPanel(new MigLayout(""));
        JPanel panel_topLeft = new JPanel(new MigLayout("filly"));
        JPanel panel_topCenter = new JPanel(new MigLayout("fill"));
        JPanel panel_topRight = new JPanel(new MigLayout("fill"));
        JPanel panel_bottom = new JPanel(new MigLayout("filly"));

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

        /*** Content attributes **/
        names_txtArea.setEditable(false);
        emails_txtArea.setEditable(false);
        passes_txtArea.setEditable(false);

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
        panel_topCenter.add(logo,"align center");
        panel_topLeft.add(top_labelA3, "pushx, growx, wrap, sg b");
        panel_topRight.add(refresh_btn, "align right, wrap, sg d");
        panel_topRight.add(connect_btn, "align right, wrap, sg d");

        panel_middle.add(name_label, "split 3,align left, pushx, growx, gapy 10, sg a");
        panel_middle.add(email_label, "align center,pushx, growx, sg a");
        panel_middle.add(pass_label, "align right,pushx, growx, wrap, sg a");

        panel_bottom.add(status_label,"split 2");
        panel_bottom.add(status, "align left");

        panel_middle.add(new JScrollPane(names_txtArea), "align left, split 3, push, grow");
        panel_middle.add(new JScrollPane(emails_txtArea), "align center,push, grow");
        panel_middle.add(new JScrollPane(passes_txtArea), "align right,push, grow, wrap");

        frame.setContentPane(panel_root);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new layout();
            }
        });
    }
}

