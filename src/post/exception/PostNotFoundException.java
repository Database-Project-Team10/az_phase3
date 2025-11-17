package src.post.exception;

public class PostNotFoundException extends PostException {
  public PostNotFoundException() {
    super("해당 게시글을 찾을 수 없습니다.");
  }
}