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
                System.out.println("3. 내가 참여 중인 프로젝트 보기");
                System.out.println("4. 프로젝트 수정");
                System.out.println("5. 프로젝트 삭제");
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
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "2":
                        projectService.createProject(memberService.getCurrentUser());
                        break;
                    case "3":
                        projectService.showMyProjectList(memberService.getCurrentUser());
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
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
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
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
