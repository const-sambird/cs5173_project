package edu.ou.cs5173.io;

import java.io.IOException;

public interface MessageSender {
    public void sendMessage(String message) throws IOException;
}
