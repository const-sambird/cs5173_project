package edu.ou.cs5173;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class Main {
    public static void main(String[] args) {
        new Main();
    }

    private JFrame frame;
    private JTextArea chat;
    private JTextField chatBox;
    private JTextField username;
    private JTextField password;
    private JButton doLogin;
    private boolean isLoggedIn = false;
    private JLabel loginStatus;
    private JLabel status;
    private Border border;
    private JScrollPane scroll;

    public Main() {
        this.setupGui();        
    }

    private void setupGui() {
        // i hate swing i hate swing i hate swing i hate swing i hate swing i ha
        JPanel gui = new JPanel(new BorderLayout(5, 5));
        this.chat = new JTextArea(10, 50);
        JPanel login = new JPanel();
        login.setLayout(new FlowLayout(FlowLayout.TRAILING));
        username = new JTextField("Username");
        password = new JTextField("Password");
        loginStatus = new JLabel("Please log in.");
        doLogin = new JButton("Login");
        username.addFocusListener(clearDefault(username));
        password.addFocusListener(clearDefault(password));
        login.add(username);
        login.add(password);
        login.add(doLogin);
        login.add(loginStatus);
        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout(1, 1));
        chatBox = new JTextField();
        status = new JLabel("Ready.");
        bottom.add(chatBox, BorderLayout.NORTH);
        bottom.add(status, BorderLayout.SOUTH);
        DefaultCaret caret = (DefaultCaret) chat.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scroll = new JScrollPane(chat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        border = BorderFactory.createLineBorder(Color.BLUE, 1);
        chatBox.setBorder(border);
        this.frame = new JFrame("cs5173 chat");
        gui.add(login, BorderLayout.PAGE_START);
        gui.add(scroll);
        gui.add(bottom, BorderLayout.PAGE_END);
        gui.setBorder(new EmptyBorder(5,5,5,5));
        frame.setContentPane(gui);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private FocusListener clearDefault(JTextField field) {
        return new FocusListener() {
            public void focusGained(FocusEvent e) {
                field.setText("");
            }
        
            public void focusLost(FocusEvent e) {}
        };
    }
}
