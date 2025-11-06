package src.member;

import java.util.Scanner;

public class MemberController {
    private final MemberService memberService = new MemberService();
    private final Scanner scanner = new Scanner(System.in);

    public void showMemberMenu() {
        while (true) {
            System.out.println("----- Member -----");
            System.out.println("1. 회원 가입");
            System.out.println("2. 로그인");
            System.out.println("b. 뒤로 가기");
            System.out.print("입력: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    memberService.signUp();
                    break;
                case "2":
                    // sad
                    break;
                case "b":
                    // 뒤로 가기
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 1, 2, b 중에서 선택해주세요.");
                    break;
            }
        }
    }
}
