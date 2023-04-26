package edu.ou.cs5173.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.ou.cs5173.protocol.MessageHandler;

public class Server {
    private ServerSocket serverSocket;
    private boolean stopped = false;

    public void start(int port, String name, String password) throws IOException {
        this.serverSocket = new ServerSocket(port);
        while (!stopped) {
            new ClientHandler(this.serverSocket.accept(), name, password).start();
        }
    }

    public void stop() throws IOException {
        this.stopped = true;
        this.serverSocket.close();
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String name;
        private String password;

        public ClientHandler(Socket socket, String name, String password) {
            this.clientSocket = socket;
            this.name = name;
            this.password = password;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                MessageHandler handler = new MessageHandler(this.name, this.password, out);
                
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    boolean terminate = handler.handle(inputLine);
                    if (terminate) {
                        break;
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("An unrecoverable IO error occurred in the thread " + getName());
                e.printStackTrace();
            }
        }
    }
}
