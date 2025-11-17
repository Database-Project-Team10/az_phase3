package src.reply.exception;

public class UnauthorizedReplyAccessException extends RuntimeException {
    public UnauthorizedReplyAccessException() {
        super("본인이 작성한 댓글만 수정/삭제할 수 있습니다.");
    }
}