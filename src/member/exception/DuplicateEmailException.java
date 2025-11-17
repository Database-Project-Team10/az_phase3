package src.member.exception;

public class DuplicateEmailException extends MemberException {
    public DuplicateEmailException() {
        super("이미 존재하는 이메일입니다.");
    }
}