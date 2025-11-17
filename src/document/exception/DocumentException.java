package src.document.exception;

public abstract class DocumentException extends RuntimeException {
    
    public DocumentException(String message) {
        super(message);
    }
}