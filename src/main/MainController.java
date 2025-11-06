package src.main;

import src.member.MemberController;

import java.util.Scanner;

public class MainController {
    private final MemberController memberController;
    private final Scanner sc;

    // 2. 생성자에서 필요한 객체들을 초기화합니다.
    public MainController() {
        this.memberController = new MemberController();
        this.sc = new Scanner(System.in);
    }

    /**
     * 메인 메뉴 로직을 실행하는 메서드
     */
    public void run() {
        while (true) {
            System.out.println("\n---------- Main ----------");
            System.out.println("1. 회원 기능");
            System.out.println("2. 프로젝트 기능");
            System.out.println("q. 나가기");
            System.out.print("메뉴를 선택하세요: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    memberController.showMemberMenu();
                    break;
                case "2":
                    break;
                case "q":
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 1, 2, q 중에서 선택해주세요.");
                    break;
            }
        }
    }
}
