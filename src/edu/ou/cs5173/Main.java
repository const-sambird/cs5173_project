package edu.ou.cs5173;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import edu.ou.cs5173.io.Server;
import edu.ou.cs5173.protocol.Message;
import edu.ou.cs5173.protocol.MessageHandler;
import edu.ou.cs5173.protocol.MessageType;
import edu.ou.cs5173.protocol.User;

public class Main {
    public static void main(String[] args) {
        PrintWriter pw = new PrintWriter(System.out, true, StandardCharsets.UTF_8);
        MessageHandler sam = new MessageHandler("sam", "pwd", pw);
        sam.handle(new Message("deepti", "sam", MessageType.INITIATE, "null").serialise());
    }
}
