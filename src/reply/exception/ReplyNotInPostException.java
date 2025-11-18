package src.reply.exception;

public class ReplyNotInPostException extends ReplyException {
    public ReplyNotInPostException() {
        super("해당 게시물에 존재하는 댓글이 아닙니다.");
    }
}
