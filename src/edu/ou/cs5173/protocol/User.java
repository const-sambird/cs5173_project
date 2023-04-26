package edu.ou.cs5173.protocol;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKey;

import edu.ou.cs5173.util.CryptoUtilities;

public class User {
    private String password;
    private int challenge;
    private String name;
    private String partner;
    private String encrypt_key;
    private String decrypt_key;
    private int state;

    public User(String name, String partner, String password) {
        this.name = name;
        this.partner = partner;
        this.password = password;
        this.state = 0;

        this.updateKey();
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

        String encToHash = this.name + this.partner + this.password + this.state;
        byte[] encBytes = encToHash.getBytes(StandardCharsets.UTF_8);
        byte[] encRes = md.digest(encBytes);
        this.encrypt_key = new String(encRes, StandardCharsets.UTF_8);

        String decToHash = this.partner + this.name + this.password + this.state;
        byte[] decBytes = decToHash.getBytes(StandardCharsets.UTF_8);
        byte[] decRes = md.digest(decBytes);
        this.decrypt_key = new String(decRes, StandardCharsets.UTF_8);
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

        String correctInput = Integer.toString(this.challenge) + this.decrypt_key;
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

        String challengeString = Integer.toString(challenge) + this.encrypt_key;
        
        return new String(md.digest(challengeString.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    private SecretKey getEncryptionKey(String salt) {
        try {
            return CryptoUtilities.getKeyFromPassword(this.encrypt_key, salt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private SecretKey getDecryptionKey(String salt) {
        try {
            return CryptoUtilities.getKeyFromPassword(this.decrypt_key, salt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encrypt(String message) {
        SecureRandom sr = new SecureRandom();
        byte[] saltBytes = new byte[32];
        sr.nextBytes(saltBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes);
        SecretKey encKey = this.getEncryptionKey(salt);
        String iv = Base64.getEncoder().encodeToString(CryptoUtilities.generateIv());

        try {
            return salt + Message.SEPARATOR + iv + Message.SEPARATOR + CryptoUtilities.encrypt(message, encKey, iv);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String decrypt(String message) throws Exception {
        String[] fields = message.split("" + Message.SEPARATOR);
        
        if (fields.length != 3) {
            System.err.println("Couldn't split the received message into the form <salt, iv, ciphertext>!");
            System.err.println("message: " + message);
            return "";
        }

        String salt = fields[0];
        String iv = fields[1];
        String ciphertext = fields[2];
        SecretKey decKey = this.getDecryptionKey(salt);

        return CryptoUtilities.decrypt(ciphertext, decKey, iv);
    }
}
