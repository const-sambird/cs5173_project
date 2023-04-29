package edu.ou.cs5173.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.ou.cs5173.protocol.Message;
import edu.ou.cs5173.protocol.MessageHandler;
import edu.ou.cs5173.ui.MessageWriter;

public class Server implements MessageSender {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    private String password;
    private MessageHandler handler;

    public void start(int port, String name, String password, MessageWriter mw, OutBuffer o) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.clientSocket = serverSocket.accept();
        this.name = name;
        this.password = password;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.handler = new MessageHandler(this.name, this.password, out, mw);
        String inputLine;
        boolean done = false;

        do {
            if (o.has()) {
                this.sendMessage(o.get());
            }
            if (clientSocket.getInputStream().available() > 5) {
                inputLine = in.readLine();
                done = handler.handle(inputLine);
            }
        } while (!done);

        mw.writeInfo("Session ended.");
        in.close();
        out.close();
        clientSocket.close();
        this.stop();
    }

    public void stop() throws IOException {
        this.serverSocket.close();
    }

    public void sendMessage(String msg) throws IOException {
        out.println(msg);
    }

    public MessageHandler getHandler() {
        return this.handler;
    }

    public void sendChatMessage(Message m) {
        if (handler == null) return;

        handler.sendMessage(m);
    }
}
