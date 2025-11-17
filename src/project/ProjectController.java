package src.project;

import src.member.MemberService;
import src.participant.ParticipantService;
import src.post.PostController;
import src.techspec.project.ProjectTechspecController;
import src.mbti.project.ProjectMbtiController;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ProjectController {

    private final MemberService memberService =  new MemberService();
    private final ProjectService projectService = new ProjectService();
    private final ParticipantService participantService = new ParticipantService();
    private final ProjectDetailController projectDetailController = new ProjectDetailController();

    private final ProjectMbtiController projectMbtiController = new ProjectMbtiController();
    private final ProjectTechspecController projectTechspecController = new ProjectTechspecController();

    private final Scanner scanner = new Scanner(System.in);

    public void showProjectMenu() {
        while (true) {
            System.out.println("\n---------- 프로젝트 기능 ----------");

            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("1. 프로젝트 목록 보기");
                System.out.println("2. 프로젝트 생성");
                System.out.println("3. 프로젝트 참여");
                System.out.println("4. 프로젝트 삭제");
                System.out.println("5. 내가 참여 중인 프로젝트 보기 및 활동하기");
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
                    case "1":
                        System.out.print("보고 싶은 프로젝트 개수를 입력하세요.(최신순): ");
                        int cnt = scanner.nextInt();
                        scanner.nextLine();
                        showProjectList(projectService.getProjectList(cnt));
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;

                    case "2":
                        System.out.println("---------- 프로젝트 생성 ----------");
                        System.out.print("프로젝트 제목: ");
                        String title = scanner.nextLine();
                        System.out.print("프로젝트 설명: ");
                        String description = scanner.nextLine();

                        // 1. 스택 입력
                        Set<String> techSpecs = projectTechspecController.inputTechSpecs();

                        // 2. MBTI 입력
                        Map<Long, String> mbtiMap = projectMbtiController.inputMbti();

                        // 3. Service 호출
                        Project newProject = projectService.createProject(
                                memberService.getCurrentUser(),
                                title,
                                description,
                                techSpecs,
                                mbtiMap
                        );

                        if (newProject != null){
                        } else {
                            System.out.println("프로젝트 생성에 실패했습니다.");
                        }
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;

                    case "3":
                        System.out.print("참여하고 싶은 프로젝트 번호를 입력해주세요: ");
                        projectId = scanner.nextLong();
                        scanner.nextLine();
                        if (participantService.joinProject(projectId, memberService.getCurrentUser().getId())){
                            System.out.println("프로젝트 참여 성공!");
                        } else {
                            System.out.println("프로젝트 참여 실패!");
                        }
                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;

                    case "4":
                        showProjectList(projectService.getMyProjectList(memberService.getCurrentUser()));
                        System.out.print("삭제할 프로젝트의 번호를 입력해주세요: ");
                        projectId = scanner.nextLong();
                        scanner.nextLine();

                        System.out.print("정말로 삭제하시겠습니까? (Y/N) ");
                        String confirm = scanner.nextLine();

                        if ("Y".equalsIgnoreCase(confirm)) {
                            // Service는 ID만 받음
                            if (projectService.deleteProject(projectId)){
                                System.out.println("삭제 완료!");
                            } else {
                                System.out.println("삭제에 실패했습니다.");
                            }
                        } else {
                            System.out.println("삭제 취소");
                        }

                        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;

                    case "5":
                        showProjectList(projectService.getMyProjectList(memberService.getCurrentUser()));
                        System.out.print("접속할 프로젝트의 번호 입력: ");

                        projectId = scanner.nextLong();
                        scanner.nextLine();

                        Project projectToView = projectService.getMyProjectById(memberService.getCurrentUser(), projectId);
                        if (projectToView != null) {
                            projectDetailController.showDetailMenu(projectId);
                        } else {
                            System.out.println("잘못된 프로젝트 ID이거나 권한이 없습니다.");
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
}