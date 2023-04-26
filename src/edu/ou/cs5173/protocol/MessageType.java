package edu.ou.cs5173.protocol;

public enum MessageType {
    INITIATE,
    RESPONDENT_CHALLENGE,
    INITIATOR_CHALLENGE,
    CHALLENGE_RESPONSE,
    CHALLENGE_FAILED,
    UPDATE_KEY,
    MESSAGE,
    ABORT,
    MALFORMED_MESSAGE
}
