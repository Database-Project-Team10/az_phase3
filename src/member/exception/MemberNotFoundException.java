package src.member.exception;

public class MemberNotFoundException extends MemberException {
    public MemberNotFoundException() {
        super("해당 회원을 찾을 수 없습니다.");
    }
}