package edu.ou.cs5173;

import edu.ou.cs5173.protocol.Message;
import edu.ou.cs5173.protocol.MessageType;

public class Main {
    public static void main(String[] args) {
        // create a test message
        Message message = new Message("sam", "deepti", MessageType.INITIATE, "hello!");
        // have a look at its properties
        System.out.printf("message type       | %s\n", message.messageType);
        System.out.printf("message sender     | %s\n", message.sender);
        System.out.printf("message recipient  | %s\n", message.recipient);
        System.out.printf("message payload    | %s\n", message.payload);
        // serialise it
        String serialisedMessage = message.serialise();
        System.out.printf("serialised message | %s\n", serialisedMessage);
        // deserialise it
        Message recMessage = new Message(serialisedMessage);
        // have a look at its properties
        System.out.printf("message type       | %s\n", recMessage.messageType);
        System.out.printf("message sender     | %s\n", recMessage.sender);
        System.out.printf("message recipient  | %s\n", recMessage.recipient);
        System.out.printf("message payload    | %s\n", recMessage.payload);
    }
}
