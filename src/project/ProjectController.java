package src.project;

import src.mbti.project.ProjectMbtiController;
import src.member.MemberService;
import src.participant.ParticipantService;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ProjectController {

    private final MemberService memberService =  new MemberService();
    private final Scanner scanner = new Scanner(System.in);
    private final ProjectService projectService = new ProjectService();
    private final ParticipantService participantService = new ParticipantService();
    private final ProjectDetailController projectDetailController = new ProjectDetailController();
    private final ProjectMbtiController projectMbtiController = new ProjectMbtiController();

    public void showProjectMenu() {
        while (true) {
            System.out.println("\n---------- 프로젝트 기능 ----------");

            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("1. 프로젝트 목록 보기");
                System.out.println("2. 프로젝트 생성");
                System.out.println("3. 프로젝트 참여");
                System.out.println("4. 내가 참여 중인 프로젝트 보기 및 활동하기");
                System.out.println("5. 프로젝트 수정");
                System.out.println("6. 프로젝트 삭제");
                System.out.println("b. 뒤로 가기");
            }
            else {
                System.out.println("1. 프로젝트 목록 보기");
                System.out.println("b. 뒤로 가기");
            }

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();
            Long projectId = null;

            if (memberService.isLoggedIn()) {
                switch (choice) {
                    case "1": // 프로젝트 목록 보기
                        System.out.print("보고 싶은 프로젝트 개수를 입력하세요.(최신순): ");
                        int cnt = scanner.nextInt();
                        scanner.nextLine();
                        showProjectList(projectService.getProjectList(cnt));
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "2": // 프로젝트 생성
                        System.out.println("---------- 프로젝트 생성 ----------");
                        System.out.print("프로젝트 제목: ");
                        String title = scanner.nextLine();
                        System.out.print("프로젝트 설명: ");
                        String description = scanner.nextLine();

                        Set<String> uniqueTechNames = new HashSet<>();
                        System.out.println("\n---------- 요구 스택 추가 ----------");
                        while (true) {
                            System.out.print("추가할 기술 스택 이름 (완료: q): ");
                            String techName = scanner.nextLine();

                            if ("q".equalsIgnoreCase(techName)) {
                                break; // q 입력 시 루프 종료
                            }
                            // [!] DB에 저장하지 않고, "대문자"로 변환하여 Set에 추가
                            uniqueTechNames.add(techName.toUpperCase());
                        }

                        if (projectService.createProject(title, description, uniqueTechNames, memberService.getCurrentUser())){
                            System.out.println("프로젝트가 생성되었습니다.");
                        }
                        else {
                            System.out.println("프로젝트 생성에 실패했습니다.");
                        }
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "3": // 프로젝트 참여
                        System.out.print("참여하고 싶은 프로젝트 번호를 입력해주세요: ");
                        projectId = scanner.nextLong();
                        scanner.nextLine();
                        if (participantService.joinProject(projectId, memberService.getCurrentUser().getId())){
                            System.out.println("프로젝트 참여 성공!");
                        }
                        else {
                            System.out.println("프로젝트 참여 실패!");
                        }
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "4": // 참여 중인 프로젝트 조회
                        showProjectList(projectService.getMyProjectList(memberService.getCurrentUser()));
                        System.out.println("접속할 프로젝트의 번호를 입력해주세요.");

                        projectId = scanner.nextLong();
                        scanner.nextLine();
                        projectDetailController.showDetailMenu(projectId);
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "5": // 프로젝트 수정
                        showProjectList(projectService.getMyProjectList(memberService.getCurrentUser()));
                        System.out.print("수정할 프로젝트의 번호를 입력해주세요: ");
                        projectId = scanner.nextLong();
                        scanner.nextLine();

                        if (projectService.updateProject(projectId)){
                            System.out.println("수정 완료!");
                        }
                        else {
                            System.out.println("수정에 실패했습니다.");
                        }
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "6": // 프로젝트 삭제
                        showProjectList(projectService.getMyProjectList(memberService.getCurrentUser()));
                        System.out.print("삭제할 프로젝트의 번호를 입력해주세요: ");
                        projectId = scanner.nextLong();
                        scanner.nextLine();
                        System.out.print("정말로 삭제하시겠습니까? (Y/N) ");
                        if (projectService.deleteProject(projectId, scanner.nextLine())){
                            System.out.println("삭제 완료!");
                        }
                        else {
                            System.out.println("삭제에 실패했습니다.");
                        }
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
                        showProjectList(projectService.getProjectList(cnt));
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

    private void showProjectList(List<Project> projectList){
        System.out.println("---------- 프로젝트 목록 ----------");
        for (Project project : projectList) {
            System.out.println(project.getId() + ". " + project.getTitle());
        }
    }

    private void showProjectDetail(Project project){
        System.out.println("\n---------- 프로젝트 상세 정보 ----------");
        System.out.println("프로젝트명: " +  project.getTitle());
        System.out.println("\n프로젝트 설명");
        System.out.println(project.getDescription());
    }
}
