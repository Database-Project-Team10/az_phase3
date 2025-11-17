package src.post;

import src.member.MemberService;
import src.reply.ReplyController;
import src.post.exception.InvalidPostException;
import src.post.exception.PostNotFoundException;
import src.post.exception.UnauthorizedPostAccessException;

import java.util.List;
import java.util.Scanner;

public class PostController {

    private final ReplyController replyController = new ReplyController();
    private final MemberService memberService =  new MemberService();
    private final Scanner scanner = new Scanner(System.in);
    private final PostService postService = new PostService();

    public void showPostMenu(Long projectId) {
        while (true) {
            System.out.println("\n---------- 게시물 기능 ----------");

            if (!memberService.isLoggedIn()) {
                System.out.println("로그인해야합니다.");
                return;
            }

            System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
            System.out.println("현재 접속 중인 프로젝트: " + projectId);
            System.out.println("1. 전체 게시물 보기");
            System.out.println("2. 내가 작성한 게시물 보기");
            System.out.println("3. 게시물 작성");
            System.out.println("4. 게시물 수정");
            System.out.println("5. 게시물 삭제");
            System.out.println("6. 게시물 접속");
            System.out.println("b. 뒤로 가기");

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();
            Long choicePostId = null;
            String title = null;
            String content = null;

            switch (choice) {

                case "1": // 게시물 목록
                    try {
                        showPostList(postService.getPostList(projectId));
                    } catch (Exception e) {
                        printError(e);
                    }
                    pause();
                    break;

                case "2":  // 내가 작성한 게시물 목록
                    try {
                        showPostList(postService.getMyPostList(projectId, memberService.getCurrentUser().getId()));
                    } catch (Exception e) {
                        printError(e);
                    }
                    pause();
                    break;

                case "3": // 게시물 생성
                    try {
                        System.out.println("---------- 게시물 작성 ----------");
                        System.out.print("게시물 제목: ");
                        title = scanner.nextLine();
                        System.out.print("게시물 내용: ");
                        content = scanner.nextLine();

                        Post post = new Post(projectId, memberService.getCurrentUser().getId(), title, content);

                        postService.createPost(post);
                        System.out.println("게시물 생성 성공!");

                    } catch (Exception e) {
                        printError(e);
                    }
                    pause();
                    break;

                case "4": // 게시물 수정
                    try {
                        System.out.println("---------- 게시물 수정 ----------");
                        showPostList(postService.getMyPostList(projectId, memberService.getCurrentUser().getId()));

                        System.out.print("수정하고 싶은 게시물 번호를 입력하세요: ");
                        choicePostId = Long.valueOf(scanner.nextLine());

                        Post myPost = postService.getMyPost(choicePostId, memberService.getCurrentUser().getId());
                        showPostDetail(myPost);

                        // 제목 입력 여부
                        while (true) {
                            System.out.print("제목을 수정하시겠습니까? (Y/N): ");
                            String c = scanner.nextLine();
                            if (c.equals("Y")) {
                                System.out.print("수정할 제목 입력: ");
                                title = scanner.nextLine();
                                break;
                            } else if (c.equals("N")) {
                                title = myPost.getTitle();
                                break;
                            } else {
                                System.out.println("잘못된 입력입니다.");
                            }
                        }

                        // 내용 입력 여부
                        while (true) {
                            System.out.print("내용을 수정하시겠습니까? (Y/N): ");
                            String c = scanner.nextLine();
                            if (c.equals("Y")) {
                                System.out.print("수정할 내용 입력: ");
                                content = scanner.nextLine();
                                break;
                            } else if (c.equals("N")) {
                                content = myPost.getContent();
                                break;
                            } else {
                                System.out.println("잘못된 입력입니다.");
                            }
                        }

                        Post updated = new Post(
                                myPost.getId(),
                                memberService.getCurrentUser().getId(),  // 수정: 작성자 ID 포함
                                title,
                                content,
                                myPost.getCreatedAt()
                        );

                        postService.updatePost(updated);
                        System.out.println("게시물 수정 성공!");

                    } catch (Exception e) {
                        printError(e);
                    }
                    pause();
                    break;

                case "5": // 게시물 삭제
                    try {
                        System.out.println("---------- 게시물 삭제 ----------");
                        showPostList(postService.getMyPostList(projectId, memberService.getCurrentUser().getId()));

                        System.out.print("삭제할 게시물 번호를 입력하세요: ");
                        choicePostId = Long.valueOf(scanner.nextLine());

                        postService.deletePost(choicePostId, memberService.getCurrentUser().getId());
                        System.out.println("게시물 삭제 성공!");

                    } catch (Exception e) {
                        printError(e);
                    }
                    pause();
                    break;

                case "6": // 게시물 접속
                    try {
                        showPostList(postService.getPostList(projectId));
                        System.out.print("접속할 게시물의 번호를 입력해주세요: ");
                        choicePostId = Long.valueOf(scanner.nextLine());
                        showPostDetail(postService.getPost(choicePostId));
                        replyController.showReplyMenu(choicePostId);

                    } catch (Exception e) {
                        printError(e);
                    }
                    break;

                case "b":
                    return;

                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void printError(Exception e) {
        if (e instanceof PostNotFoundException) {
            System.out.println("[오류] 게시물을 찾을 수 없습니다.");
        } else if (e instanceof UnauthorizedPostAccessException) {
            System.out.println("[오류] 작성자 본인만 수정/삭제할 수 있습니다.");
        } else if (e instanceof InvalidPostException) {
            System.out.println("[오류] " + e.getMessage());
        } else {
            System.out.println("[시스템 오류] " + e.getMessage());
        }
    }

    private void pause() {
        System.out.print("\n엔터키를 누르면 게시물 기능으로 돌아갑니다.");
        scanner.nextLine();
    }

    public void showPostDetail(Post post){
        System.out.println("---------- 게시물 번호 " + post.getId() + " ----------");
        System.out.println("게시물 제목: " + post.getTitle());
        System.out.println("게시물 내용");
        System.out.println(post.getContent());
        System.out.println();
    }

    public void showPostList(List<Post> postList){
        System.out.println("---------- 게시물 목록 ----------");
        for (Post post : postList){
            System.out.println(post.getId() + ". " + post.getTitle());
            System.out.println("게시물 내용: " + post.getContent());
            System.out.println();
        }
    }
}