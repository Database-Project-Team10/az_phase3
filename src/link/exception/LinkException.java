package src.link.exception;

public abstract class LinkException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public LinkException(String message) {
        super(message);
    }
}