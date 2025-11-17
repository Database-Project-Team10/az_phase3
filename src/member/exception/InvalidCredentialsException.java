package src.member.exception;

public class InvalidCredentialsException extends MemberException {
  public InvalidCredentialsException() {
    super("이메일 또는 비밀번호가 올바르지 않습니다.");
  }
}