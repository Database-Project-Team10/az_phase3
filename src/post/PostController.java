package src.post;

import src.member.MemberService;

import java.util.List;
import java.util.Scanner;

public class PostController {

    private final MemberService memberService =  new MemberService();
    private final Scanner scanner = new Scanner(System.in);
    private final PostService postService = new PostService();

    public void showPostMenu(Long projectId) {
        while (true) {
            System.out.println("\n---------- 게시물 기능 ----------");

            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("현재 접속 중인 프로젝트: " + projectId);
                System.out.println("1. 전체 게시물 보기");
                System.out.println("2. 내가 작성한 게시물 보기");
                System.out.println("3. 게시물 작성");
                System.out.println("4. 게시물 수정");
                System.out.println("5. 게시물 삭제");
                System.out.println("b. 뒤로 가기");
            } else {
                System.out.println("로그인해야합니다.");
                return;
            }

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            if (memberService.isLoggedIn()) {
                switch (choice) {
                    case "1": // 게시물 목록
                        List<Post> postList = postService.getPostList(projectId);
                        System.out.println("---------- 전체 게시물 목록 ----------");
                        for (Post post : postList){
                            System.out.println(post.getId() + ". " + post.getTitle());
                            System.out.println("게시물 내용: " + post.getContent());
                        }
                        System.out.print("\n엔터키를 누르면 게시물 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "2":  // 내가 작성한 게시물 목록
                        System.out.println("---------- 내가 작성한 게시물 목록 ----------");
                        List<Post> myPostList = postService.getMyPostList(projectId, memberService.getCurrentUser().getId());
                        for (Post post : myPostList){
                            System.out.println(post.getId() + ". " + post.getTitle());
                        }
                        System.out.print("\n엔터키를 누르면 게시물 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "3": // 게시물 생성
                        System.out.print("\n엔터키를 누르면 게시물 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "4": // 게시물 수정
                        System.out.print("\n엔터키를 누르면 게시물 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "5": // 게시물 삭제
                        System.out.print("\n엔터키를 누르면 게시물 기능으로 돌아갑니다.");
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
