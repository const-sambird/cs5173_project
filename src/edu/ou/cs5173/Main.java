package edu.ou.cs5173;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.AbstractAction;
import javax.swing.Action;
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

import edu.ou.cs5173.io.SocketContainer;
import edu.ou.cs5173.io.Server;
import edu.ou.cs5173.ui.MessageWriter;

public class Main {
    public static void main(String[] args) {
        new Main();
    }

    // swing components
    private JFrame frame;
    private JTextArea chat;
    private JTextField chatBox;
    private JTextField hostToConnect;
    private JTextField portToConnect;
    private JTextField ourPort;
    private JTextField username;
    private JTextField password;
    private JTextField partner;
    private JButton doLogin;
    private JLabel loginStatus;
    private JLabel status;
    private Border border;
    private JScrollPane scroll;

    // app state
    private boolean isLoggedIn = false;
    private String name;
    private String pwd;
    private MessageWriter mw;

    // socket thread
    SocketContainer container;
    Thread server;

    public Main() {
        this.setupGui();
        this.mw = new MessageWriter(this.chat);
    }

    private void setupGui() {
        // i hate swing i hate swing i hate swing i hate swing i hate swing i ha
        JPanel gui = new JPanel(new BorderLayout(5, 5));
        this.chat = new JTextArea(20, 100);
        this.chat.setEditable(false);
        JPanel top = new JPanel(new BorderLayout(1, 1));
        JPanel hostinfo = new JPanel();
        hostinfo.setLayout(new FlowLayout(FlowLayout.LEADING));
        hostToConnect = new JTextField(10);
        portToConnect = new JTextField(5);
        ourPort = new JTextField(5);
        hostinfo.add(new JLabel("This port"));
        hostinfo.add(ourPort);
        hostinfo.add(new JLabel("Hostname"));
        hostinfo.add(hostToConnect);
        hostinfo.add(new JLabel("Port"));
        hostinfo.add(portToConnect);
        JPanel login = new JPanel();
        login.setLayout(new FlowLayout(FlowLayout.TRAILING));
        username = new JTextField(10);
        password = new JTextField(10);
        partner = new JTextField(10);
        loginStatus = new JLabel("Please log in.");
        doLogin = new JButton("Connect");
        doLogin.addActionListener(handleLogin());
        login.add(new JLabel("Username"));
        login.add(username);
        login.add(new JLabel("Password"));
        login.add(password);
        login.add(new JLabel("Partner"));
        login.add(partner);
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
        chatBox.addActionListener(handleMessageSend());
        this.frame = new JFrame("cs5173 chat");
        top.add(hostinfo, BorderLayout.WEST);
        top.add(login, BorderLayout.EAST);
        gui.add(top, BorderLayout.PAGE_START);
        gui.add(scroll);
        gui.add(bottom, BorderLayout.PAGE_END);
        gui.setBorder(new EmptyBorder(5,5,5,5));
        frame.setContentPane(gui);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Action handleMessageSend() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = chatBox.getText();
                if (isLoggedIn) {
                    chatBox.setText("");
                    mw.writeMessage(name, message);
                }
            }
        };
    }

    private Action handleLogin() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLoggedIn) return;
                String user = username.getText();
                String pass = password.getText();
                String thisPort = ourPort.getText();
                String host = hostToConnect.getText();
                String otherPort = portToConnect.getText();
                String other = partner.getText();

                if (thisPort == null || thisPort.strip().equals("")) {
                    status.setText("Bad port.");
                    return;
                }

                if (host == null || otherPort == null || host.strip().equals("") || otherPort.strip().equals("")) {
                    status.setText("Bad connection parameters.");
                    return;
                }

                if (user == null || user.strip().equals("")) {
                    status.setText("Bad username.");
                    return;
                }

                if (pass == null || pass.strip().equals("")) {
                    status.setText("Bad password.");
                    return;
                }

                if (other == null || other.strip().equals("")) {
                    status.setText("Bad partner username.");
                    return;
                }

                name = user;
                pwd = pass;
                isLoggedIn = true;
                loginStatus.setText("Logged in.");
                status.setText("Ready.");
                username.setEditable(false);
                password.setEditable(false);
                partner.setEditable(false);

                Thread serverThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            container.setServer();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            mw.writeInfo("An IOException occurred when spinning up the server");
                        }
                    }
                });

                Thread clientThread = new Thread(new Runnable(){
                    public void run() {
                        boolean success = container.setClient();
                        if (!success) {
                            container.resetClient();
                            mw.writeInfo("Couldn't connect to the remote server on " + host + ":" + otherPort + ", waiting for partner...");
                        }
                        serverThread.start();
                    }
                });

                container = new SocketContainer(thisPort, host, otherPort, user, pass, other, mw, clientThread);

                clientThread.start();
            }
        };
    }
}
