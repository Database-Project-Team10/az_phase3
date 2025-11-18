package src.link.exception;

public class LinkNotFoundException extends LinkException {
    public LinkNotFoundException() {
        super("해당 링크를 찾을 수 없습니다.");
    }
}