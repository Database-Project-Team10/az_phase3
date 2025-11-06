package src.project;

import src.member.MemberService;

import java.util.Scanner;

public class ProjectController {

    private final MemberService memberService =  new MemberService();
    private final Scanner scanner = new Scanner(System.in);
    private final ProjectService projectService = new ProjectService();

    public void showProjectMenu() {
        while (true) {
            System.out.println("\n---------- 프로젝트 기능 ----------");

            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("1. 프로젝트 목록 보기");
                System.out.println("2. 프로젝트 생성");
                System.out.println("b. 뒤로 가기");
            }
            else {
                System.out.println("1. 프로젝트 목록 보기");
                System.out.println("b. 뒤로 가기");
            }

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            if (memberService.isLoggedIn()) {
                switch (choice) {
                    case "1":
                        System.out.print("보고 싶은 프로젝트 개수를 입력하세요.(최신순): ");
                        int cnt = scanner.nextInt();
                        scanner.nextLine();
                        projectService.showProjectList(cnt);
                        break;
                    case "2":
                        projectService.createProject();
                        break;
                    case "b":
                        return;
                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            }
            else {
                switch (choice) {
                    case "1":
                        System.out.print("보고 싶은 프로젝트 개수를 입력하세요.(최신순): ");
                        int cnt = scanner.nextInt();
                        scanner.nextLine();
                        projectService.showProjectList(cnt);
                        break;
                    case "b":
                        return;
                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            }
        }
    }
}
