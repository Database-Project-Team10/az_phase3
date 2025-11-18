package src.project;

import src.member.MemberService;
import src.participant.ParticipantService;
import src.participant.exception.ParticipantException;
import src.post.PostController;
import src.project.dto.ProjectCreateRequestDto;
import src.project.exception.ProjectException;
import src.techspec.project.ProjectTechspecController;
import src.mbti.project.ProjectMbtiController;
import src.project.exception.ProjectNotFoundException;
import src.utils.InputUtil;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ProjectController {

    private final MemberService memberService;
    private final ProjectService projectService;
    private final ParticipantService participantService;
    private final ProjectDetailController projectDetailController;

    private final ProjectMbtiController projectMbtiController;
    private final ProjectTechspecController projectTechspecController;

    private final Scanner scanner = new Scanner(System.in);

    // ★ DI 적용: 필요한 모든 객체를 외부에서 주입받음
    public ProjectController(
        MemberService memberService,
        ProjectService projectService,
        ParticipantService participantService,
        ProjectDetailController projectDetailController,
        ProjectMbtiController projectMbtiController,
        ProjectTechspecController projectTechspecController) {

        this.memberService = memberService;
        this.projectService = projectService;
        this.participantService = participantService;
        this.projectDetailController = projectDetailController;
        this.projectMbtiController = projectMbtiController;
        this.projectTechspecController = projectTechspecController;
    }

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
        try {
            String input = InputUtil.getInput(scanner, "보고 싶은 프로젝트 개수를 입력하세요(최신순)");
            int cnt = Integer.parseInt(input);

            List<Project> list = projectService.getProjectList(cnt);
            showProjectList(list);

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 목록 조회가 취소되었습니다.");
        } catch (NumberFormatException e) {
            System.out.println("[오류] 숫자를 입력해주세요.");
        }

        pause();
    }

    private void handleCreateProject() {
        System.out.println("---------- 프로젝트 생성 ----------");

        try {
            String title = InputUtil.getInput(scanner, "프로젝트 제목");
            String description = InputUtil.getInput(scanner, "프로젝트 설명");

            Set<String> techSpecs = projectTechspecController.inputTechSpecs();
            Map<Long, String> mbtiMap = projectMbtiController.inputMbti();

            ProjectCreateRequestDto projectCreateRequestDto = new ProjectCreateRequestDto(
                    memberService.getCurrentUser().getId(),
                    title,
                    description,
                    techSpecs,
                    mbtiMap
            );

            Project newProject = projectService.createProject(projectCreateRequestDto);

            if (newProject != null) {
            } else {
                System.out.println("프로젝트 생성에 실패했습니다.");
            }

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 프로젝트 생성이 취소되었습니다.");
        } catch (ProjectException e) {
            System.out.println("[오류] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[오류] " + e.getMessage());
        }

        pause();
    }

    private void handleJoinProject() {
        try {
            Long projectId = InputUtil.getLong(scanner, "참여하고 싶은 프로젝트 ID");

            participantService.joinProject(projectId, memberService.getCurrentUser().getId());
            System.out.println("프로젝트 참여 성공!");

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 프로젝트 참여가 취소되었습니다.");
        } catch (ParticipantException | ProjectException e) {
            System.out.println("[오류]: " + e.getMessage());
        }

        pause();
    }

    private void handleDeleteProject() {
        showProjectList(projectService.getMyLeaderProjectList(memberService.getCurrentUser()));

        try {
            Long projectId = InputUtil.getLong(scanner, "삭제할 프로젝트의 ID");
            String confirm = InputUtil.getInput(scanner, "정말 삭제하시겠습니까? (Y/N)");

            if (!"Y".equalsIgnoreCase(confirm)) {
                System.out.println("삭제 취소");
                return;
            }

            projectService.deleteProject(projectId, memberService.getCurrentUser().getId());

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 프로젝트 삭제가 취소되었습니다.");
        } catch (ProjectException e) {
            System.out.println("[오류]: " + e.getMessage());
        }

        pause();
    }

    private void handleMyProjectDetail() {
        showProjectList(projectService.getMyProjectList(memberService.getCurrentUser()));

        try {
            Long projectId = InputUtil.getLong(scanner, "접속할 프로젝트 ID");

            Project myProject = projectService.getMyProjectById(memberService.getCurrentUser(), projectId);
            projectDetailController.showDetailMenu(myProject.getId());

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 접속이 취소되었습니다.");
        } catch (ProjectNotFoundException e) {
            System.out.println("[오류] 참여 중인 프로젝트에만 접속할 수 있습니다.");
        }

        pause();
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
    private void pause() {
        System.out.print("\n엔터키를 누르면 프로젝트 기능으로 돌아갑니다.");
        scanner.nextLine();
    }
}