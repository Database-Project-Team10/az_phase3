package src.project.exception;

public abstract class ProjectException extends RuntimeException {
    public ProjectException(String message) {
        super(message);
    }
}
