package edu.ou.cs5173.io;

import java.io.IOException;

import edu.ou.cs5173.ui.MessageWriter;

public class SocketContainer {
    private volatile Server server;
    private volatile Client client;
    private volatile String thisPort;
    private volatile String host;
    private volatile String port;
    private volatile String user;
    private volatile String pass;
    private volatile String partner;
    private volatile MessageWriter mw;
    private volatile OutBuffer o;

    public SocketContainer(String thisPort, String host, String port, String user, String pass, String partner, MessageWriter mw, OutBuffer o) {
        this.thisPort = thisPort;
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.partner = partner;
        this.mw = mw;
        this.o = o;
    }

    public synchronized void setServer() throws IOException {
        this.server = new Server();
        this.server.start(Integer.parseInt(thisPort), user, pass, mw, o);
    }

    public synchronized Server getServer() {
        return this.server;
    }

    public synchronized boolean hasServer() {
        return this.server != null;
    }

    public synchronized boolean setClient() {
        this.client = new Client();
        return this.client.start(host, Integer.parseInt(port), user, pass, partner, mw, o);
    }

    public synchronized Client getClient() {
        return this.client;
    }

    public synchronized void resetClient() {
        this.client = null;
    }

    public synchronized boolean hasClient() {
        return this.client != null;
    }

    public synchronized MessageSender getSender() {
        if (this.hasClient()) {
            return this.client;
        }

        return this.server;
    }
}
