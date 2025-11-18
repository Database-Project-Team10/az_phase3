package src.document.exception;

public class DocumentNotFoundException extends DocumentException {
    public DocumentNotFoundException() {
        super("해당 문서를 찾을 수 없습니다.");
    }
}