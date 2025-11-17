package src.project;

import src.member.MemberService;
import src.post.PostController;
import src.mbti.ProjectMbtiController;
import src.link.LinkController;

import src.mbti.ProjectMbtiRepository;
import src.techspec.ProjectTechspecRepository;
import src.techspec.Techspec;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProjectDetailController {

    private final PostController postController =  new PostController();
    private final LinkController linkController = new LinkController();
    private final MemberService memberService = new MemberService();
    private final Scanner scanner = new Scanner(System.in);

    private final ProjectMbtiController projectMbtiController = new ProjectMbtiController();
    private final ProjectRepository projectRepository = new ProjectRepository();

    private final ProjectMbtiRepository projectMbtiRepository = new ProjectMbtiRepository();
    private final ProjectTechspecRepository projectTechspecRepository = new ProjectTechspecRepository();

    public void showDetailMenu(Long projectId){
        Project currentProject = projectRepository.findById(projectId);

        while (true) {
            System.out.println("\n---------- 프로젝트 상세 ----------");
            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("현재 접속 중인 프로젝트: " + projectId);
                System.out.println("1. 게시물");
                System.out.println("2. 문서 아카이브");
                System.out.println("3. 링크 아카이브");
                System.out.println("4. 회의록 아카이브");
                System.out.println("5. 설정 정보 조회 (MBTI/스택)");
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
                    // 문서 기능
                    break;
                case "3":
                    linkController.showLinkMenu(projectId);
                    break;
                case "4":
                    // 회의록 기능
                    break;
                case "5":
                    this.showProjectInfo(projectId);
                    break;
                case "b":
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }

        }
    }

    private void showProjectInfo(Long projectId) {
        System.out.println("\n---------- 프로젝트 설정 정보 ----------");

        // (1) 선호 MBTI 조회
        Map<Long, String> mbtiMap = projectMbtiRepository.findMbtiMapByProjectId(projectId);
        System.out.print("[선호 MBTI] ");
        if (mbtiMap.isEmpty()) {
            System.out.print("(설정되지 않음)");
        } else {
            // ID 1~4번 순서대로 출력 (E/I, S/N, T/F, J/P)
            for (long i = 1; i <= 4; i++) {
                System.out.print(mbtiMap.getOrDefault(i, "?"));
            }
        }
        System.out.println();

        // (2) 요구 기술 스택 조회
        List<Techspec> techList = projectTechspecRepository.findTechspecsByProjectId(projectId);
        System.out.println("[요구 기술 스택]");
        if (techList.isEmpty()) {
            System.out.println("- (설정되지 않음)");
        } else {
            for (Techspec tech : techList) {
                System.out.println("- " + tech.getName());
            }
        }

        System.out.println("----------------------------------------");
        System.out.println("(엔터키를 누르면 상세 메뉴로 돌아갑니다)");
        scanner.nextLine();
    }
}
