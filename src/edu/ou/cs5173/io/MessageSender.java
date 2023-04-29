package edu.ou.cs5173.io;

import java.io.IOException;

import edu.ou.cs5173.protocol.Message;

public interface MessageSender {
    public void sendMessage(String message) throws IOException;

    public void sendChatMessage(Message message);
}
