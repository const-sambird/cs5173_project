package edu.ou.cs5173;

import edu.ou.cs5173.io.Server;
import edu.ou.cs5173.protocol.Message;
import edu.ou.cs5173.protocol.MessageType;
import edu.ou.cs5173.protocol.User;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start(12345);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
