package src.techspec.exception;

public abstract class TechspecException extends RuntimeException {
    public TechspecException(String message) {
        super(message);
    }
}
