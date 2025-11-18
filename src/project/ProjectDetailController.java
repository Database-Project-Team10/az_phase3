package src.project;

import src.document.DocumentController;
import src.link.LinkController;
import src.mbti.project.ProjectMbtiController;
import src.meeting.MeetingController;
import src.member.MemberService;
import src.post.PostController;
import src.project.dto.ProjectUpdateRequestDto;
import src.project.exception.ProjectException;
import src.techspec.project.ProjectTechspecController;

import java.util.Scanner;

public class ProjectDetailController {

    private final PostController postController;
    private final LinkController linkController;
    private final DocumentController documentController;
    private final MeetingController meetingController;
    private final ProjectTechspecController projectTechspecController;
    private final ProjectMbtiController projectMbtiController;

    private final ProjectService projectService;
    private final MemberService memberService;

    private final Scanner scanner = new Scanner(System.in);

    // ★ 모든 의존성을 외부에서 주입받도록 변경 (DI 적용)
    public ProjectDetailController(
            PostController postController,
            LinkController linkController,
            DocumentController documentController,
            MeetingController meetingController,
            ProjectTechspecController projectTechspecController,
            ProjectMbtiController projectMbtiController,
            ProjectService projectService,
            MemberService memberService
    ) {
        this.postController = postController;
        this.linkController = linkController;
        this.documentController = documentController;
        this.meetingController = meetingController;
        this.projectTechspecController = projectTechspecController;
        this.projectMbtiController = projectMbtiController;
        this.projectService = projectService;
        this.memberService = memberService;
    }

    public void showDetailMenu(Long projectId) {
        Project currentProject = projectService.getProject(projectId);

        while (true) {
            System.out.println("\n---------- 프로젝트 상세 ----------");
            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("현재 접속 중인 프로젝트: " + projectId);
                System.out.println("제목: " + currentProject.getTitle());
                System.out.println("설명");
                System.out.println(currentProject.getDescription());
                System.out.println("-------------------------------");

                System.out.println("1. 게시물");
                System.out.println("2. 문서 아카이브");
                System.out.println("3. 링크 아카이브");
                System.out.println("4. 회의록 아카이브");
                System.out.println("5. 제목/설명 수정");
                System.out.println("6. 요구 테크스펙");
                System.out.println("7. 희망 MBTI");
                System.out.println("b. 뒤로 가기");
            } else {
                System.out.println("로그인해야합니다.");
                return;
            }

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    postController.showPostMenu(projectId);
                    break;
                case "2":
                    documentController.showDocumentMenu(projectId);
                    break;
                case "3":
                    linkController.showLinkMenu(projectId);
                    break;
                case "4":
                    meetingController.showMeetingMenu(projectId);
                    break;
                case "5":
                    currentProject = updateProjectInfoUI(currentProject);
                    break;
                case "6":
                    projectTechspecController.showProjectTechspecMenu(currentProject);
                    return;
                case "7":
                    projectMbtiController.showProjectMbtiMenu(currentProject);
                    break;
                case "b":
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private Project updateProjectInfoUI(Project project) {
        String newTitle = project.getTitle();
        String newDesc = project.getDescription();

        System.out.print("새 제목 (엔터 시 유지): ");
        String t = scanner.nextLine();
        if (!t.isEmpty()) newTitle = t;

        System.out.print("새 설명 (엔터 시 유지): ");
        String d = scanner.nextLine();
        if (!d.isEmpty()) newDesc = d;

        try {
            ProjectUpdateRequestDto projectUpdateRequestDto = new ProjectUpdateRequestDto(
                    newTitle,
                    newDesc
            );
            projectService.updateProjectInfo(project.getId(),
                    memberService.getCurrentUser().getId(),
                    projectUpdateRequestDto);

            System.out.println("수정 완료!");

            return new Project(project.getId(), newTitle, newDesc,
                    project.getCreatedAt(), project.getModifiedAt());
        } catch (ProjectException e) {
            System.out.println("[오류]: " + e.getMessage());
        }
        return project;
    }
}