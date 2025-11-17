package src.link.exception;

public class InvalidLinkInputException extends LinkException {
	private static final long serialVersionUID = 1L;
	public InvalidLinkInputException(String message) {
        super(message);
    }
}