package edu.ou.cs5173.io;

import java.io.IOException;

import edu.ou.cs5173.protocol.MessageHandler;
import edu.ou.cs5173.ui.MessageWriter;

public class SocketContainer {
    private Server server;
    private Client client;
    private String host;
    private String port;
    private String user;
    private String pass;
    private String partner;
    private MessageWriter mw;

    public SocketContainer(String host, String port, String user, String pass, String partner, MessageWriter mw) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.partner = partner;
        this.mw = mw;
    }

    public synchronized void setServer(Server server) {
        this.server = server;
    }

    public synchronized void setClient() throws IOException {
        this.client = new Client();
        this.client.start(host, Integer.parseInt(port), user, pass, partner, mw);
    }

    public synchronized void resetClient() {
        this.client = null;
    }

    public synchronized Server getServer() {
        return this.server;
    }

    public synchronized Client getClient() {
        return this.client;
    }

    public synchronized boolean hasServer() {
        return this.server != null;
    }

    public synchronized boolean hasClient() {
        return this.client != null;
    }
}
