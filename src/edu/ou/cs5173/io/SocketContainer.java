package edu.ou.cs5173.io;

import java.io.IOException;

import edu.ou.cs5173.ui.MessageWriter;

public class SocketContainer {
    private Server server;
    private Client client;
    private String thisPort;
    private String host;
    private String port;
    private String user;
    private String pass;
    private String partner;
    private MessageWriter mw;
    private ContainerState containerState = ContainerState.CLIENT_FIRST;

    public SocketContainer(String thisPort, String host, String port, String user, String pass, String partner, MessageWriter mw) {
        this.thisPort = thisPort;
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.partner = partner;
        this.mw = mw;
    }

    public synchronized void setServer() throws IOException {
        this.server = new Server();
        if (this.containerState == ContainerState.CLIENT_FIRST) {
            this.server.start(Integer.parseInt(thisPort), user, pass, mw, this, this.getClient().getOut(), this.getClient().getIn());
        } else if (this.containerState == ContainerState.SERVER_FIRST) {
            this.server.start(Integer.parseInt(thisPort), user, pass, mw, this);
        }
    }

    public synchronized void setClient() throws IOException {
        this.client = new Client();
        this.client.start(host, Integer.parseInt(port), user, pass, partner, mw);
    }

    public synchronized void resetClient() {
        this.client = null;
        this.containerState = ContainerState.SERVER_FIRST;
    }

    public synchronized void createClientThreadIfNotExists() {
        if (this.hasClient()) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setClient();
                } catch (IOException ex) {
                    mw.writeInfo("FATAL: We should've been able to connect by now, but connection failed. Close the app and try again.");
                }
            }
        }).start();
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

    /**
     * There's two possible states for the SocketContainer to be in.
     * 
     * This is largely academic, but it does have some implications for setting the
     * input/output streams correctly, as if we create the server first the iostreams
     * need to be passed to the client, and vice versa.
     */
    private enum ContainerState {
        SERVER_FIRST,
        CLIENT_FIRST
    }
}
