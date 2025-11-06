package src.member;

import java.util.Scanner;

public class MemberController {
    private final MemberService memberService = new MemberService();
    private final Scanner scanner = new Scanner(System.in);

    public void showMemberMenu() {
        while (true) {
            System.out.println("\n---------- 회원 기능 ----------");

            // [수정] 로그인 상태에 따라 다른 메뉴 표시
            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("1. 로그아웃");
                System.out.println("2. 회원 정보 수정");
                System.out.println("b. 뒤로 가기");
            } else {
                System.out.println("1. 회원 가입");
                System.out.println("2. 로그인");
                System.out.println("b. 뒤로 가기");
            }

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            if (memberService.isLoggedIn()) {
                // 로그인 상태일 때
                switch (choice) {
                    case "1":
                        memberService.logout();
                        break;
                    case "2":
                        memberService.editMemberInfo();
                        break;
                    case "b":
                        return; // 메인 메뉴로
                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            } else {
                // 로그아웃 상태일 때
                switch (choice) {
                    case "1":
                        memberService.signUp();
                        break;
                    case "2":
                        memberService.login();
                        break;
                    case "b":
                        return; // 메인 메뉴로
                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            }
        }
    }
}
