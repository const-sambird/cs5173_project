package edu.ou.cs5173.protocol;

public class Message {
    public static final char SEPARATOR = '\u001f';

    /**
     * A protocol definition string. Its main use is assisting
     * clients in determining whether or not a message is encrypted.
     * If this string is present in the serialised message, it's almost
     * certainly not encrypted.
     */
    public static final String USER_AGENT = "VGCMP/1.0";

    private String sender;
    private String recipient;
    private MessageType messageType;
    private String payload;

    /**
     * Creates a message object, given parameters.
     * This creates an object representation of a message we are **sending**.
     *
     * @param sender the username of the sender
     * @param recipient the username of the recipient
     * @param messageType the type of message
     * @param payload anything else that should be sent with the message
     */
    public Message(String sender, String recipient, MessageType messageType, String payload) {
        this.sender = sender;
        this.recipient = recipient;
        this.messageType = messageType;
        this.payload = payload;
    }

    /**
     * Creates a message object, given a serialised message string.
     * This creates an object representation of a message we are **receiving**.
     *
     * @param serialisedMessage
     */
    public Message(String serialisedMessage) {
        String[] fields = serialisedMessage.split(""+SEPARATOR);

        if (fields.length != 5) {
            // we've lost a field somewhere
            // or there's a separator character in the message for some reason
            // either way, it's malformed!
            this.messageType = MessageType.MALFORMED_MESSAGE;
            return;
        }

        if (!fields[0].equals(USER_AGENT)) {
            // if there's no user-agent, something's gone wrong
            this.messageType = MessageType.MALFORMED_MESSAGE;
            return;
        }

        this.messageType = this.stringToMessageType(fields[1]);
        this.sender = fields[2];
        this.recipient = fields[3];
        this.payload = fields[4];
    }

    /**
     * This method converts the enum MessageType into a serialisable string.
     * This should be the canonical source of truth for the enum's values.
     * Nowhere else should be converting MessageTypes to strings.
     *
     * @param type the MessageType to convert
     * @return the message type as a string
     */
    private String messageTypeToString(MessageType type) {
        String result;

        switch (type) {
            case INITIATE:
                result = "init";
                break;
            case RESPONDENT_CHALLENGE:
                result = "resp_chal";
                break;
            case INITIATOR_CHALLENGE:
                result = "init_chal";
                break;
            case CHALLENGE_RESPONSE:
                result = "chal_resp";
                break;
            case CHALLENGE_SUCCESS:
                result = "chal_succ";
                break;
            case CHALLENGE_FAILED:
                result = "chal_fail";
                break;
            case UPDATE_KEY:
                result = "key_update";
                break;
            case MESSAGE:
                result = "msg";
                break;
            case ABORT:
                result = "abort";
                break;
            default:
                result = "malformed";
        }

        return result;
    }

    /**
     * Like {@code Message#messageTypeToString(MessageType)}, this method converts between
     * String (when we have a serialised message) and the MessageType enum. The canonical
     * source of truth for String-to-MessageType.
     *
     * @param type the serialised type of the message
     * @return the message type as a MessageType
     */
    private MessageType stringToMessageType(String type) {
        MessageType result;

        switch (type) {
            case "init":
                result = MessageType.INITIATE;
                break;
            case "resp_chal":
                result = MessageType.RESPONDENT_CHALLENGE;
                break;
            case "init_chal":
                result = MessageType.INITIATOR_CHALLENGE;
                break;
            case "chal_resp":
                result = MessageType.CHALLENGE_RESPONSE;
                break;
            case "chal_succ":
                result = MessageType.CHALLENGE_SUCCESS;
                break;
            case "chal_fail":
                result = MessageType.CHALLENGE_FAILED;
                break;
            case "key_update":
                result = MessageType.UPDATE_KEY;
                break;
            case "msg":
                result = MessageType.MESSAGE;
                break;
            case "abort":
                result = MessageType.ABORT;
                break;
            default:
                result = MessageType.MALFORMED_MESSAGE;
        }

        return result;
    }

    /**
     * Returns a String representation of this Message that may be sent through a Socket
     *
     * @return
     */
    public String serialise() {
        String type = this.messageTypeToString(this.messageType);
        
        return USER_AGENT + SEPARATOR + type + SEPARATOR + sender + SEPARATOR + recipient + SEPARATOR + payload;
    }

    @Override
    public String toString() {
        return this.serialise();
    }

    // getters down here
    public String getSender() {
        return this.sender;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public MessageType getType() {
        return this.messageType;
    }

    public String getPayload() {
        return this.payload;
    }
}
