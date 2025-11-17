package src.reply.exception;

public abstract class ReplyException extends RuntimeException {
    public ReplyException(String message) {
        super(message);
    }
}
