package src.project;

import src.member.MemberService;
import src.post.PostController;
import src.link.LinkController;
import src.document.DocumentController;

import java.util.Scanner;

public class ProjectDetailController {

    private final PostController postController =  new PostController();
    private final LinkController linkController = new LinkController();
    private final DocumentController documentController = new DocumentController();
    private final MemberService memberService = new MemberService();
    private final Scanner scanner = new Scanner(System.in);

    public void showDetailMenu(Long projectId){
        while (true) {
            System.out.println("\n---------- 프로젝트 상세 ----------");
            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("현재 접속 중인 프로젝트: " + projectId);
                System.out.println("1. 게시물");
                System.out.println("2. 문서 아카이브");
                System.out.println("3. 링크 아카이브");
                System.out.println("4. 회의록 아카이브");
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
                    // 회의록 기능
                    break;
                case "b":
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }

        }
    }
}
