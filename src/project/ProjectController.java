package src.project;

import src.member.MemberService;
import src.participant.ParticipantService;
import src.post.PostController;
import src.project.dto.ProjectCreateRequestDto;
import src.project.exception.ProjectException;
import src.techspec.project.ProjectTechspecController;
import src.mbti.project.ProjectMbtiController;
import src.project.exception.ProjectNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ProjectController {

    private final MemberService memberService = new MemberService();
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
            } else {
                System.out.println("1. 프로젝트 목록 보기");
                System.out.println("b. 뒤로 가기");
            }

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            boolean keepGoing;
            if (memberService.isLoggedIn()) {
                keepGoing = handleLoggedInMenu(choice);
            } else {
                keepGoing = handleGuestMenu(choice);
            }

            if (!keepGoing) {
                return;
            }
        }
    }

    private boolean handleLoggedInMenu(String choice) {
        switch (choice) {
            case "1":
                handleListProjects();
                break;

            case "2":
                handleCreateProject();
                break;

            case "3":
                handleJoinProject();
                break;

            case "4":
                handleDeleteProject();
                break;

            case "5":
                handleMyProjectDetail();
                break;

            case "b":
                return false;

            default:
                System.out.println("잘못된 입력입니다.");
        }
        return true;
    }

    private boolean handleGuestMenu(String choice) {
        switch (choice) {
            case "1":
                handleListProjects();
                break;

            case "b":
                return false;

            default:
                System.out.println("잘못된 입력입니다.");
        }
        return true;
    }

    private void handleListProjects() {
        System.out.print("보고 싶은 프로젝트 개수를 입력하세요.(최신순): ");
        int cnt = scanner.nextInt();
        scanner.nextLine();

        List<Project> list = projectService.getProjectList(cnt);
        showProjectList(list);

        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
        scanner.nextLine();
    }

    private void handleCreateProject() {
        System.out.println("---------- 프로젝트 생성 ----------");

        System.out.print("프로젝트 제목: ");
        String title = scanner.nextLine();

        System.out.print("프로젝트 설명: ");
        String description = scanner.nextLine();

        Set<String> techSpecs = projectTechspecController.inputTechSpecs();
        Map<Long, String> mbtiMap = projectMbtiController.inputMbti();

        ProjectCreateRequestDto projectCreateRequestDto = new ProjectCreateRequestDto(
                memberService.getCurrentUser().getId(),
                title,
                description,
                techSpecs,
                mbtiMap
        );

        try {
            Project newProject = projectService.createProject(projectCreateRequestDto);

            if (newProject != null) {
                // 성공 메시지는 Service 내부에서 출력
            } else {
                System.out.println("프로젝트 생성에 실패했습니다.");
            }

        } catch (ProjectException e) {
            System.out.println("[오류] " + e.getMessage());

        } catch (Exception e) {
            System.out.println("[오류] " + e.getMessage());
        }

        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
        scanner.nextLine();
    }

    private void handleJoinProject() {
        System.out.print("참여하고 싶은 프로젝트 ID를 입력하세요: ");
        Long projectId = scanner.nextLong();
        scanner.nextLine();

        boolean success = participantService.joinProject(projectId, memberService.getCurrentUser().getId());

        if (success) {
            System.out.println("프로젝트 참여 성공!");
        } else {
            System.out.println("프로젝트 참여 실패!");
        }

        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
        scanner.nextLine();
    }

    private void handleDeleteProject() {
        showProjectList(projectService.getMyProjectList(memberService.getCurrentUser()));

        System.out.print("삭제할 프로젝트의 ID를 입력해주세요: ");
        Long projectId = scanner.nextLong();
        scanner.nextLine();

        System.out.print("정말 삭제하시겠습니까? (Y/N): ");
        String confirm = scanner.nextLine();

        if (!"Y".equalsIgnoreCase(confirm)) {
            System.out.println("삭제 취소");
            return;
        }

        try {
            projectService.deleteProject(projectId, memberService.getCurrentUser().getId());
        } catch (ProjectException e) {
            System.out.println("[오류]: " + e.getMessage());
        }

        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
        scanner.nextLine();
    }

    private void handleMyProjectDetail() {
        showProjectList(projectService.getMyProjectList(memberService.getCurrentUser()));

        System.out.print("접속할 프로젝트 ID 입력: ");
        Long projectId = scanner.nextLong();
        scanner.nextLine();

        try {
            Project myProject = projectService.getMyProjectById(memberService.getCurrentUser(), projectId);
            projectDetailController.showDetailMenu(myProject.getId());

        } catch (ProjectNotFoundException e) {
            System.out.println("[오류] 참여 중인 프로젝트에만 접속할 수 있습니다.");
        }

        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
        scanner.nextLine();
    }

    private void showProjectList(List<Project> projectList) {
        System.out.println("---------- 프로젝트 목록 ----------");
        if (projectList.isEmpty()) {
            System.out.println("(프로젝트가 없습니다)");
            return;
        }
        for (Project p : projectList) {
            System.out.println(p.getId() + ". " + p.getTitle());
        }
    }
}