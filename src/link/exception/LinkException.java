package src.link.exception;

public abstract class LinkException extends RuntimeException {
	public LinkException(String message) {
        super(message);
    }
}