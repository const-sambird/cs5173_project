package edu.ou.cs5173.protocol;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import edu.ou.cs5173.ui.MessageWriter;

public class MessageHandler {
    private String name;
    private String password;
    private User user;
    private PrintWriter out;
    private MessageWriter mw;
    
    public boolean hasUser() {
        return user != null;
    }

    public MessageHandler(String name, String password, PrintWriter out, MessageWriter mw) {
        this.name = name;
        this.password = password;
        this.out = out;
        this.mw = mw;
    }

    public void setPartner(String partner) {
        this.user = new User(this.name, partner, this.password);
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
        boolean isDecrypted = message.contains(Message.USER_AGENT);
        Message m;

        if (isDecrypted) {
            mw.writeReceivedCommunication("[UNCRYPT] " + message);
            m = new Message(message);
        } else {
            if (!this.hasUser()) return false;
            String decrypted;
            mw.writeReceivedCommunication("[ENCRYPT] " + message);
            try {
                decrypted = this.user.decrypt(message);
                mw.writeReceivedCommunication("[DECRYPT] " + decrypted);
            } catch (Exception e) {
                System.err.println("Couldn't decrypt a received message, is the key bad?");
                System.err.println(message);
                e.printStackTrace();
                return true;
            }

            m = new Message(decrypted);
        }

        if (!this.name.equals(m.getRecipient())) {
            // don't you hate it when you get someone else's mail?
            return false;
        }

        if (this.hasUser() && !this.user.getPartner().equals(m.getSender())) {
            // this isn't meant for this MessageHandler!
            return false;
        }

        switch (m.getType()) {
            case MALFORMED_MESSAGE:
                // malformed. nothing to be done
                break;
            case INITIATE:
                if (m.getRecipient().equals(this.name) && !this.hasUser()) {
                    this.setPartner(m.getSender());
                    String challenge = Integer.toString(this.user.setChallenge());
                    Message response = new Message(this.user.getName(), this.user.getPartner(), MessageType.RESPONDENT_CHALLENGE, challenge);
                    this.sendMessage(response);
                }
                break;
            case RESPONDENT_CHALLENGE:
                if (m.getRecipient().equals(this.name) && !this.hasUser()) {
                    this.setPartner(m.getSender());
                    int challenge = Integer.parseInt(m.getPayload());
                    String reply = new String(Base64.getEncoder().encode(this.user.solveChallenge(challenge).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
                    String ourChallenge = Integer.toString(this.user.setChallenge());
                    Message response = new Message(this.user.getName(), this.user.getPartner(), MessageType.INITIATOR_CHALLENGE, reply + "---" + ourChallenge);
                    this.sendMessage(response);
                }
                break;
            case INITIATOR_CHALLENGE:
                if (this.hasUser()) {
                    String[] p = m.getPayload().split("---");
                    if (p.length != 2) {
                        this.sendMessage(new Message(this.user.getName(), this.user.getPartner(), MessageType.CHALLENGE_FAILED, "null"));
                        break;
                    }
                    String challengeResponse = new String(Base64.getDecoder().decode(p[0]), StandardCharsets.UTF_8);
                    int ourChallenge = Integer.parseInt(p[1]);
                    boolean challengeSuccess = this.user.validateChallenge(challengeResponse);
                    if (!challengeSuccess) {
                        this.sendMessage(new Message(this.user.getName(), this.user.getPartner(), MessageType.CHALLENGE_FAILED, "null"));
                        break;
                    }

                    String reply = new String(Base64.getEncoder().encode(this.user.solveChallenge(ourChallenge).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
                    Message response = new Message(this.user.getName(), this.user.getPartner(), MessageType.CHALLENGE_RESPONSE, reply);
                    this.sendMessage(response);
                }
                break;
            case CHALLENGE_RESPONSE:
                if (this.hasUser()) {
                    boolean challengeSuccess = this.user.validateChallenge(new String(Base64.getDecoder().decode(m.getPayload()), StandardCharsets.UTF_8));
                    if (challengeSuccess) {
                        Message response = new Message(this.user.getName(), this.user.getPartner(), MessageType.CHALLENGE_SUCCESS, "null");
                        this.sendMessage(response);
                    } else {
                        Message response = new Message(this.user.getName(), this.user.getPartner(), MessageType.CHALLENGE_FAILED, "null");
                        this.sendMessage(response);
                    }
                }
                break;
            case CHALLENGE_SUCCESS:
                // do we actually need this one?
                break;
            case CHALLENGE_FAILED:
                // reset state
                this.user = null;
                break;
            case UPDATE_KEY:
                if (this.user.doesTrustOther()) {
                    this.user.updateState();
                }
                break;
            case MESSAGE:
                if (this.user.doesTrustOther()) {
                    String text = m.getPayload();
                    mw.writeMessage(m.getSender(), text);
                }
                break;
            case ABORT:
                // aye aye, cap'n
                if (this.user.doesTrustOther()) {
                    this.user = null;
                    return true;
                }
        }

        return false;
    }

    public static boolean shouldEncrypt(MessageType mt) {
        return mt == MessageType.CHALLENGE_SUCCESS || mt == MessageType.UPDATE_KEY || mt == MessageType.MESSAGE || mt == MessageType.ABORT;
    }

    public void sendMessage(Message m) {
        String toSend;

        if (shouldEncrypt(m.getType())) {
            String s = m.serialise();
            mw.writeSentCommunication("[DECRYPT] " + s);
            toSend = this.user.encrypt(s);
            mw.writeSentCommunication("[ENCRYPT] " + toSend);
        } else {
            toSend = m.serialise();
            mw.writeSentCommunication("[UNCRYPT] " + toSend);
        }
        out.println(toSend);
    }
}
