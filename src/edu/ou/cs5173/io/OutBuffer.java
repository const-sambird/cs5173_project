package edu.ou.cs5173.io;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class OutBuffer {
    private Queue<String> messages;

    public OutBuffer() {
        this.messages = new LinkedList<String>();
    }

    public synchronized boolean has() {
        return !this.messages.isEmpty();
    }

    public synchronized String get() {
        return this.messages.remove();
    }

    public synchronized void add(String s) {
        this.messages.add(s);
    }
}
