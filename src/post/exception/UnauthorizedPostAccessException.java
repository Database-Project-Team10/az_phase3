package src.post.exception;

public class UnauthorizedPostAccessException extends PostException {
  public UnauthorizedPostAccessException() {
    super("해당 게시글에 대한 수정 또는 삭제 권한이 없습니다.");
  }
}