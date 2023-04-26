package edu.ou.cs5173.protocol;

public class Message {
    public static final char SEPARATOR = '\u001f';

    public String sender;
    public String recipient;
    public MessageType messageType;
    public String payload;

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

        if (fields.length != 4) {
            // we've lost a field somewhere
            // or there's a separator character in the message for some reason
            // either way, it's malformed!
            this.messageType = MessageType.MALFORMED_MESSAGE;
            return;
        }

        this.messageType = this.stringToMessageType(fields[0]);
        this.sender = fields[1];
        this.recipient = fields[2];
        this.payload = fields[3];
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
        
        return type + SEPARATOR + sender + SEPARATOR + recipient + SEPARATOR + payload;
    }

    @Override
    public String toString() {
        return this.serialise();
    }
}
