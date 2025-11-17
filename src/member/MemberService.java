package src.member;

import java.util.Scanner;

public class MemberService {

    private final MemberRepository memberRepository = new MemberRepository();
    private final Scanner sc = new Scanner(System.in);

    private static Member loggedInUser = null;

    public boolean signUp(Member member, String confirmPassword) {

        if (isLoggedIn()) {
            System.out.println("이미 로그인되어 있습니다. 먼저 로그아웃해주세요.");
            return false;
        }

        // [비즈니스 로직 1] 아이디 중복 검사
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            System.out.println("이미 존재하는 아이디입니다. 다른 아이디를 입력해주세요.");
            return false;
        }

        // [비즈니스 로직 2] 비밀번호 확인
        if (!member.getPassword().equals(confirmPassword)) {
            System.out.println("비밀번호가 일치하지 않습니다. 다시 시도해주세요.");
            return false;
        }

        return memberRepository.save(member);

    }

    public void login() {
        if (isLoggedIn()) {
            System.out.println("이미 로그인되어 있습니다.");
            return;
        }

        System.out.println("---------- 로그인 ----------");
        System.out.print("이메일: ");
        String email = sc.nextLine();
        System.out.print("비밀번호: ");
        String password = sc.nextLine();

        // [비즈니스 로직 3] 사용자 조회
        Member member = memberRepository.findByEmail(email);

        // [비즈니스 로직 4] 비밀번호 검증
        if (member != null && member.getPassword().equals(password)) {
            // 로그인 성공
            loggedInUser = member; // static 변수에 현재 로그인한 사용자 정보 저장
            System.out.println("로그인 성공! '" + loggedInUser.getEmail() + "'님, 환영합니다.");
        } else {
            // 로그인 실패
            System.out.println("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    public void editMemberInfo() {
        if (!isLoggedIn()) {
            System.out.println("로그인이 필요합니다.");
            return;
        }

        System.out.println("----- 회원 정보 수정 -----");
        System.out.println("현재 이메일: " + loggedInUser.getEmail());

        System.out.print("새 비밀번호: ");
        String newPassword = sc.nextLine();
        System.out.print("새 비밀번호 확인: ");
        String confirmPassword = sc.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return;
        }

        boolean isSuccess = memberRepository.updatePassword(loggedInUser.getEmail(), newPassword);

        if (isSuccess) {
            // 현재 메모리에 들고 있는 loggedInUser 객체의 정보도 동기화
            loggedInUser.setPassword(newPassword);

            System.out.println("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            System.out.println("오류가 발생하여 비밀번호 변경에 실패했습니다.");
        }
    }

    public Member getMemberInfo(Long id) {
        return memberRepository.findById(id);
    }

    public boolean deleteMember() {
        if (!isLoggedIn()) {
            System.out.println("로그인이 필요합니다.");
            return false;
        }
        System.out.print("정말로 탈퇴하시겠습니까? (Y/N) ");
        String choice = sc.nextLine();
        if (choice.equalsIgnoreCase("Y")) {
            Long deleteUserId = loggedInUser.getId();
            logout();
            return memberRepository.delete(deleteUserId);
        }
        else if (choice.equalsIgnoreCase("N")) {
            return false;
        }
        else {
            System.out.println("잘못된 입력입니다.");
        }
        return false;
    }

    /**
     * 로그아웃 로직
     */
    public void logout() {
        if (!isLoggedIn()) {
            System.out.println("로그인 상태가 아닙니다.");
            return;
        }
        System.out.println("'" + loggedInUser.getEmail() + "'님이 로그아웃하셨습니다.");
        loggedInUser = null; // 로그인 정보 제거
    }

    // 로그인 상태 확인
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    //현재 로그인된 사용자 반환
    public Member getCurrentUser() {
        return loggedInUser;
    }
}
