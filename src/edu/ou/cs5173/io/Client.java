package edu.ou.cs5173.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ou.cs5173.protocol.Message;
import edu.ou.cs5173.protocol.MessageHandler;
import edu.ou.cs5173.protocol.MessageType;
import edu.ou.cs5173.ui.MessageWriter;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    private String password;
    private String partner;
    private MessageWriter mw;
    private MessageHandler handler;

    public void start(String ip, int port, String name, String password, String partner, MessageWriter mw) throws IOException {
        clientSocket = new Socket(ip, port);
        System.out.println("client");
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.name = name;
        this.password = password;
        this.partner = partner;
        this.mw = mw;
    }

    public void sendMessage(String msg) throws IOException {
        out.println(msg);
    }

    public void sendInitiate(String partner) {
        Message hello = new Message(name, partner, MessageType.INITIATE, "null");
        try {
            this.sendMessage(hello.serialise());
        } catch (IOException ex) {
            mw.writeInfo("There was an issue trying to initiate communication");
            ex.printStackTrace();
        }
    }

    public void sendChatMessage(String msg) {
        if (partner == null) {
            mw.writeInfo("Can't send a message yet, we haven't initiated a conversation");
            return;
        }

        Message message = new Message(name, partner, MessageType.MESSAGE, msg);
        handler.sendMessage(message);
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public BufferedReader getIn() {
        return this.in;
    }

    public PrintWriter getOut() {
        return this.out;
    }
}
