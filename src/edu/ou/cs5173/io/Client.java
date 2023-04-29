package edu.ou.cs5173.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ou.cs5173.protocol.Message;
import edu.ou.cs5173.protocol.MessageHandler;
import edu.ou.cs5173.protocol.MessageType;
import edu.ou.cs5173.ui.MessageWriter;

public class Client implements MessageSender {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    private String password;
    private String partner;
    private MessageWriter mw;
    private MessageHandler handler;
    private OutBuffer o;

    public boolean start(String ip, int port, String name, String password, String partner, MessageWriter mw, OutBuffer o) {
        try {
            clientSocket = new Socket(ip, port);
        } catch (IOException ex) {
            return false;
        }
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.name = name;
        this.password = password;
        this.partner = partner;
        this.mw = mw;
        this.o = o;
        this.handler = new MessageHandler(this.name, this.password, out, mw);

        this.sendInitiate();

        new Thread(new Runnable() {
            public void run() {
                String inputLine;
                boolean done = false;
                try {
                    do {
                        if (o.has()) {
                            sendMessage(o.get());
                        }
                        if (clientSocket.getInputStream().available() > 0) {
                            inputLine = in.readLine();
                            done = handler.handle(inputLine);
                        }
                    } while (!done);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        return true;
    }

    public void sendMessage(String msg) throws IOException {
        out.println(msg);
    }

    public void sendInitiate() {
        Message hello = new Message(name, partner, MessageType.INITIATE, "null");
        o.add(hello.serialise());
    }

    public void sendChatMessage(String msg) {
        if (partner == null) {
            mw.writeInfo("Can't send a message yet, we haven't initiated a conversation");
            return;
        }

        Message message = new Message(name, partner, MessageType.MESSAGE, msg);
        handler.sendMessage(message);
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public BufferedReader getIn() {
        return this.in;
    }

    public PrintWriter getOut() {
        return this.out;
    }

    public void sendChatMessage(Message m) {
        if (handler == null) return;

        handler.sendMessage(m);
    }
}
