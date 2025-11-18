package src.project;

import src.document.DocumentController;
import src.link.LinkController;
import src.mbti.project.ProjectMbtiController;
import src.meeting.MeetingController;
import src.member.MemberService;
import src.post.PostController;
import src.project.exception.ProjectException;
import src.techspec.project.ProjectTechspecController;

import java.util.Scanner;

public class ProjectDetailController {

    private final PostController postController =  new PostController();
    private final LinkController linkController = new LinkController();
    private final DocumentController documentController = new DocumentController();
    private final MeetingController meetingController = new MeetingController();
    private final ProjectTechspecController projectTechspecController = new ProjectTechspecController();
    private final ProjectMbtiController projectMbtiController = new ProjectMbtiController();

    private final ProjectService projectService = new ProjectService();
    private final MemberService memberService = new MemberService();

    private final Scanner scanner = new Scanner(System.in);

    public void showDetailMenu(Long projectId){
        Project currentProject = projectService.getProjectDetail(projectId);

        while (true) {
            System.out.println("\n---------- 프로젝트 상세 ----------");
            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("현재 접속 중인 프로젝트: " + projectId);
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
                    updateProjectInfoUI(currentProject);
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

    private void updateProjectInfoUI(Project project) {
        String newTitle = project.getTitle();
        String newDesc = project.getDescription();

        System.out.print("새 제목 (엔터 시 유지): ");
        String t = scanner.nextLine();
        if(!t.isEmpty()) newTitle = t;

        System.out.print("새 설명 (엔터 시 유지): ");
        String d = scanner.nextLine();
        if(!d.isEmpty()) newDesc = d;


        try{
            projectService.updateProjectInfo(project.getId(), newTitle, newDesc, memberService.getCurrentUser().getId());
            project.setTitle(newTitle);
            project.setDescription(newDesc);
            System.out.println("수정 완료");
        } catch (ProjectException e) {
            System.out.println("[오류]: " + e.getMessage());
        }
    }
}
