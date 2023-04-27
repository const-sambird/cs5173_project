package edu.ou.cs5173.io;

public class Container {
    private Server server;

    public synchronized void setServer(Server server) {
        this.server = server;
    }

    public synchronized Server getServer() {
        return this.server;
    }
}
