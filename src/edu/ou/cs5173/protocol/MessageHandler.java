package edu.ou.cs5173.protocol;

public class MessageHandler {
    private User user;
    
    public MessageHandler(User user) {
        this.user = user;
    }

    /**
     * Handles a single message. Expects a message as it comes out of the socket;
     * ie, we will handle decryption here. Returns a boolean that determines whether
     * or not the server should terminate.
     *
     * @param message the message received
     * @return whether or not we should terminate
     */
    public boolean handle(String message) {
        String decrypted;

        try {
            decrypted = this.user.decrypt(message);
        } catch (Exception e) {
            System.err.println("Couldn't decrypt a received message, is the key bad?");
            System.err.println(message);
            e.printStackTrace();
            return true;
        }

        Message m = new Message(message);

        switch (m.getType()) {
            
        }

        return false;
    }
}
