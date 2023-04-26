package edu.ou.cs5173;

import edu.ou.cs5173.protocol.Message;
import edu.ou.cs5173.protocol.MessageType;
import edu.ou.cs5173.protocol.User;

public class Main {
    public static void main(String[] args) {
        // create users
        User user1 = new User("sam", "deepti", "pwd");
        User user2 = new User("deepti", "sam", "pwd");
        // encrypt a message
        String message = "hello!";
        String ciphertext = user1.encrypt(message);
        System.out.printf("ciphertext | %s\n", ciphertext);
        // decrypt a message
        String plaintext = user2.decrypt(ciphertext);
        System.out.printf("plaintext  | %s\n", plaintext);
    }
}
