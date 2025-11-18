package src.member;

import src.member.dto.CreateMemberRequestDto;
import src.member.dto.MemberInfoResponseDto;
import src.member.dto.PasswordUpdateRequestDto;
import src.member.exception.*;

public class MemberService {

    private final MemberRepository memberRepository;
    private Member loggedInUser;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원가입
     */
    public Member signUp(CreateMemberRequestDto createMemberRequestDto) {

        if (isLoggedIn()) {
            throw new UnauthorizedException();
        }

        if (memberRepository.findByEmail(createMemberRequestDto.getEmail()) != null) {
            throw new DuplicateEmailException();
        }

        if (!createMemberRequestDto.getPassword().equals(createMemberRequestDto.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        return memberRepository.save(createMemberRequestDto.toEntity());
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
    public void editPassword(PasswordUpdateRequestDto passwordUpdateRequestDto) {

        if (!isLoggedIn()) {
            throw new UnauthorizedException();
        }

        Long currentMemberId = loggedInUser.getId();
        if (memberRepository.findById(currentMemberId) == null){
            throw new MemberNotFoundException();
        }

        if (!passwordUpdateRequestDto.getNewPassword().equals(passwordUpdateRequestDto.getConfirmNewPassword())) {
            throw new PasswordMismatchException();
        }

        memberRepository.updatePassword(currentMemberId, passwordUpdateRequestDto.getNewPassword());
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

    public MemberInfoResponseDto getAllInfo() {
        if (!isLoggedIn()) {
            throw new UnauthorizedException();
        }
        return memberRepository.getAllInfoById(loggedInUser.getId());
    }
}