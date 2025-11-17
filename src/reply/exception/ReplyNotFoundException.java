package src.reply.exception;

public class ReplyNotFoundException extends ReplyException {
    public ReplyNotFoundException() {
        super("존재하지 않는 댓글입니다.");
    }
}