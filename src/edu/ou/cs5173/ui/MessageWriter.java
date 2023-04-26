package edu.ou.cs5173.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JTextArea;

public class MessageWriter {
    private JTextArea chat;

    public MessageWriter(JTextArea chat) {
        this.chat = chat;
    }

    public static String currentTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return "[" + currentTime.format(fmt) + "] ";
    }

    public void writeMessage(String author, String content) {
        String message = currentTime() + author + " | " + content + "\n";
        this.chat.append(message);
    }

    public void writeSentCommunication(String communication) {
        String comm = currentTime() + "TX >>> " + communication + "\n";
        this.chat.append(comm);
    }

    public void writeReceivedCommunication(String communication) {
        String comm = currentTime() + "RX <<< " + communication + "\n";
        this.chat.append(comm); 
    }

    public void writeInfo(String x) {
        this.chat.append(currentTime() + "| INFO | " + x + "\n");
    }

}
