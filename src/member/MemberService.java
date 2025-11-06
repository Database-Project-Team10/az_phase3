package src.member;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MemberService {

    private final MemberRepository memberRepository = new MemberRepository();
    private final Scanner sc = new Scanner(System.in);

    private static Member loggedInUser = null;

    public void signUp() {

        if (isLoggedIn()) {
            System.out.println("이미 로그인되어 있습니다. 먼저 로그아웃해주세요.");
            return;
        }

        System.out.println("----- 회원 가입 -----");
        System.out.print("사용할 이메일: ");
        String email = sc.nextLine();

        // [비즈니스 로직 1] 아이디 중복 검사
        if (memberRepository.findByEmail(email) != null) {
            System.out.println("이미 존재하는 아이디입니다. 다른 아이디를 입력해주세요.");
            return;
        }

        System.out.print("사용할 비밀번호: ");
        String password = sc.nextLine();
        System.out.print("비밀번호 확인: ");
        String confirmPassword = sc.nextLine();

        // [비즈니스 로직 2] 비밀번호 확인
        if (!password.equals(confirmPassword)) {
            System.out.println("비밀번호가 일치하지 않습니다. 다시 시도해주세요.");
            return;
        }

        System.out.print("이름: ");
        String name = sc.nextLine();
        System.out.print("생년월일(YYYY-MM-DD): ");
        String birthDate = sc.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localBirthDate = LocalDate.parse(birthDate, formatter);

        Member newMember = new Member(email, password, name, localBirthDate);
        // 2. Repository를 통해 저장
        memberRepository.save(newMember);

        System.out.println("'" + email + "'님, 회원가입이 완료되었습니다!");
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
