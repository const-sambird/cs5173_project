package edu.ou.cs5173.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private boolean stopped = false;

    public void start(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        while (!stopped) {
            new ClientHandler(this.serverSocket.accept()).start();
        }
    }

    public void stop() throws IOException {
        this.stopped = true;
        this.serverSocket.close();
    }

    private static class ClientHandler extends Thread {
        private final String DELIMITER = ""+'\u001e';
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (DELIMITER.equals(inputLine)) {
                        out.println("bye");
                        break;
                    }
                    out.println(inputLine);
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
