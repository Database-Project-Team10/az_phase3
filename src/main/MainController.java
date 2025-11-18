package src.main;

import src.member.MemberController;
import src.project.ProjectController;

import java.util.Scanner;

public class MainController {

    private final MemberController memberController;
    private final ProjectController projectController;
    private final Scanner sc = new Scanner(System.in);

    // DI 방식: 외부에서 의존성을 주입받는다
    public MainController(MemberController memberController,
                          ProjectController projectController) {
        this.memberController = memberController;
        this.projectController = projectController;
    }

    /**
     * 메인 메뉴 로직 실행
     */
    public void run() {
        while (true) {
            System.out.println("\n---------- 메인 메뉴 ----------");
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
                    projectController.showProjectMenu();
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