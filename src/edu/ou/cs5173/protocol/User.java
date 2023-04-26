package edu.ou.cs5173.protocol;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class User {
    private String password;
    private int challenge;
    private String name;
    private String partner;
    private String key;
    private int state;

    public User(String name, String partner, String password) {
        this.name = name;
        this.partner = partner;
        this.password = password;
        this.state = 0;
    }

    private void updateKey() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("sha256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("??? the jvm didn't recognise 'sha256' as a valid algorithm for MessageDigest#getInstance ???");
            e.printStackTrace();
            return;
        }

        String stringToHash = this.name + this.partner + this.password + this.state;
        byte[] bytesToHash = stringToHash.getBytes(StandardCharsets.UTF_8);
        byte[] res = md.digest(bytesToHash);
        this.key = new String(res, StandardCharsets.UTF_8);
    }

    /**
     * Periodically changes the key every once in a while
     * Done at the request of either party
     */
    public void updateState() {
        this.state += 1;
        this.updateKey();
    }

    public int setChallenge() {
        SecureRandom sr = new SecureRandom();
        this.challenge = sr.nextInt();
        return this.challenge;
    }

    public boolean validateChallenge(String answer) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("sha256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("??? the jvm didn't recognise 'sha256' as a valid algorithm for MessageDigest#getInstance ???");
            e.printStackTrace();
            return false;
        }

        String correctInput = Integer.toString(this.challenge) + this.key;
        String correctAnswer = new String(md.digest(correctInput.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        return correctAnswer.equals(answer);
    }

    public String solveChallenge(int challenge) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("sha256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("??? the jvm didn't recognise 'sha256' as a valid algorithm for MessageDigest#getInstance ???");
            e.printStackTrace();
            return "";
        }

        String challengeString = Integer.toString(challenge) + this.key;
        
        return new String(md.digest(challengeString.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    public String encrypt(String message) {
        // TODO: use the key to encrypt this message
        return message;
    }

    public String decrypt(String ciphertext) {
        // TODO: use the key to decrypt the ciphertext into plaintext
        return ciphertext;
    }
}
