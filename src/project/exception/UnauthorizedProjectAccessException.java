package src.project.exception;

public class UnauthorizedProjectAccessException extends ProjectException {
    public UnauthorizedProjectAccessException(String message) {
        super(message);
    }
}
