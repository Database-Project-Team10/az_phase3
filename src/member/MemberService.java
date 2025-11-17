package src.member;

import src.member.exception.*;

public class MemberService {

    private final MemberRepository memberRepository = new MemberRepository();
    private static Member loggedInUser = null;

    /**
     * 회원가입
     */
    public void signUp(Member member, String confirmPassword) {

        if (isLoggedIn()) {
            throw new UnauthorizedException();
        }

        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new DuplicateEmailException();
        }

        if (!member.getPassword().equals(confirmPassword)) {
            throw new PasswordMismatchException();
        }

        memberRepository.save(member);
    }

    /**
     * 로그인
     */
    public void login(String email, String password) {

        if (isLoggedIn()) {
            throw new UnauthorizedException();
        }

        Member member = memberRepository.findByEmail(email);

        if (member == null || !member.getPassword().equals(password)) {
            throw new InvalidCredentialsException();
        }

        loggedInUser = member;
    }

    /**
     * 비밀번호 변경
     */
    public void editPassword(String newPassword, String confirmPassword) {

        if (!isLoggedIn()) {
            throw new UnauthorizedException();
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMismatchException();
        }

        memberRepository.updatePassword(loggedInUser.getEmail(), newPassword);
    }

    /**
     * 회원 탈퇴
     */
    public void deleteMember(String choice) {

        if (!isLoggedIn()) {
            throw new UnauthorizedException();
        }

        if (choice.equalsIgnoreCase("Y")) {
            Long deleteUserId = loggedInUser.getId();
            logout();
            memberRepository.delete(deleteUserId);
        }
    }

    /**
     * 로그아웃
     */
    public void logout() {
        if (!isLoggedIn()) {
            throw new UnauthorizedException();
        }
        loggedInUser = null;
    }

    /**
     * 로그인 여부
     */
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    /**
     * 현재 로그인된 사용자
     */
    public Member getCurrentUser() {
        return loggedInUser;
    }
}