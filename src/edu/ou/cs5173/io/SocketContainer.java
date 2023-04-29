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
    private volatile Thread clientThread;
    private volatile ContainerState containerState = ContainerState.CLIENT_FIRST;

    public SocketContainer(String thisPort, String host, String port, String user, String pass, String partner, MessageWriter mw, Thread clientThread) {
        this.thisPort = thisPort;
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.partner = partner;
        this.mw = mw;
        this.clientThread = clientThread;
    }

    public synchronized void setServer() throws IOException {
        this.server = new Server();
        if (this.containerState == ContainerState.CLIENT_FIRST) {
            this.server.start(Integer.parseInt(thisPort), user, pass, mw, this, this.getClient().getOut(), this.getClient().getIn());
        } else if (this.containerState == ContainerState.SERVER_FIRST) {
            this.server.start(Integer.parseInt(thisPort), user, pass, mw, this);
        }
    }

    public boolean setClient() {
        System.out.println("try-makeclient");
        this.client = new Client();
        System.out.println("try-start");
        return this.client.start(host, Integer.parseInt(port), user, pass, partner, mw);
    }

    public synchronized void resetClient() {
        this.client = null;
        this.containerState = ContainerState.SERVER_FIRST;
    }

    public synchronized void createClientThreadIfNotExists() {
        if (this.hasClient()) return;

        new Thread(new Runnable() {
            public void run() {
                boolean success = setClient();
                System.out.println("returned " + success);
            }
        }).start();
    }

    public synchronized Server getServer() {
        return this.server;
    }

    public Client getClient() {
        return this.client;
    }

    public synchronized boolean hasServer() {
        return this.server != null;
    }

    public synchronized boolean hasClient() {
        return this.client != null;
    }

    public ContainerState getContainerState() {
        return this.containerState;
    }

    /**
     * There's two possible states for the SocketContainer to be in.
     * 
     * This is largely academic, but it does have some implications for setting the
     * input/output streams correctly, as if we create the server first the iostreams
     * need to be passed to the client, and vice versa.
     */
    public enum ContainerState {
        SERVER_FIRST,
        CLIENT_FIRST
    }
}
