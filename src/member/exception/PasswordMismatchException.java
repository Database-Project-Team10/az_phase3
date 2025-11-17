package src.member.exception;

public class PasswordMismatchException extends MemberException {
    public PasswordMismatchException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}