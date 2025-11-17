package src.member.exception;

public class UnauthorizedException extends MemberException {
  public UnauthorizedException() {
    super("로그인이 필요합니다.");
  }
}