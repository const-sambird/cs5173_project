# The Very Good CS5173 Messaging Protocol (VGCMP)

**Note: I made some modifications after typing up this document (which is mostly for my own reference, not intended to be marked). The final protocol definition is included in [the project report](../../../../../cs5173_report.pdf) - that should be considered canonical, not this document!**

## Communications

Each VGCMP communication is either *encrypted* or *unencrypted*. Encrypted communications are sent in three parts, each divided by a `SEPARATOR` defined in [Message.java](./Message.java).

Part | Name | Description
-----|------|------------
0    | salt | The salt used to generate the key that encrypted this communication. This salt should be used to generate the decryption key.
1    | iv   | The initialisation vector for the CBC scheme. This should be the IV used when decrypting.
2    | msg  | The ciphertext of the message sent (see below).

## Messages

Once the message ciphertext has been decrypted, or if the message was not encrypted at all, it is similarly subdivided into four parts, broken up by the same `SEPARATOR`.

Part | Name | Description
-----|------|------------
0    | type | The type of message. Defaults to `MALFORMED_MESSAGE` when this value cannot be read.
1    | send | Who sent this message.
2    | recv | Who should receive this message.
3    | payl | The message payload.

### Types of message

There are several types of message. The type of message determines what the payload should be.

Type | Encrypted? | Expected payload | Expected behaviour
-----|------------|------------------|-------------------
`INITIATE` | no | `null` | The sender would like to initiate communication with the recipient. The recipient should reply with `RESPONDENT_CHALLENGE`.
`RESPONDENT_CHALLENGE` | no | an integer | A proof-of-identity challenge from the respondent to the initiator. The initiator should reply with `INITIATOR_CHALLENGE`.
`INITIATOR_CHALLENGE` | no | challenge response + `---` + an integer | The payload is a response to the respondent's challenge, plus a challenge set by the initiator. The respondent should reply with `CHALLENGE_RESPONSE`.
`CHALLENGE_RESPONSE` | no | challenge response | The respondent's reply to the initiator's challenge. If the initiator receives this message, it may assume it has passed the challenge set by the respondent. The initiator should reply with `CHALLENGE_SUCCESS`.
`CHALLENGE_SUCCESS` | yes | `null` | The respondent has passed the initiator's challenge. All challenges are now complete and communication can now begin.
`CHALLENGE_FAILED` | no | `null` | A challenge has been failed. The clients should terminate this session and return to `INITIATE`.
`UPDATE_KEY` | yes | `null` | Update the session key. Done by incrementing the 'state' variable.
`MESSAGE` | yes | a message | A message from one party to the other.
`ABORT` | yes | `null` | Terminate this session. Further communication will be ignored, apart from `INITIATE`.
`MALFORMED_MESSAGE` | either | any | This message couldn't be parsed. Should not be sent intentionally.

### Challenges

An example challenge scheme between Alice (initiator) and Bob (respondent):

1. Alice: `INITIATE`
2. Bob : `RESPONDENT_CHALLENGE`, 42
3. Alice: `INITIATOR_CHALLENGE`, `SHA_256(42 + K_{Alice-Bob}) + "---" + 18`
4. Bob: `CHALLENGE_RESPONSE`, `SHA_256(18 + K_{Bob-Alice})`
5. Alice: `CHALLENGE_SUCCESS`