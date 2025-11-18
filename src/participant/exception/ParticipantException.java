package src.participant.exception;

public abstract class ParticipantException extends RuntimeException {
    public ParticipantException(String message) {
        super(message);
    }
}
