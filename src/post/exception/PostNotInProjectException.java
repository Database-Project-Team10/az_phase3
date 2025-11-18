package src.post.exception;

public class PostNotInProjectException extends PostException {
    public PostNotInProjectException() {
        super("해당 프로젝트에 존재하는 게시물이 아닙니다.");
    }
}
