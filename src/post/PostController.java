package src.post;

import src.member.MemberService;
import src.post.dto.PostRequestDto;
import src.post.exception.PostException;
import src.reply.ReplyController;
import src.utils.InputUtil;

import java.util.List;
import java.util.Scanner;

public class PostController {

    private final ReplyController replyController;
    private final MemberService memberService;
    private final Scanner scanner;
    private final PostService postService;

    public PostController(
            ReplyController replyController,
            MemberService memberService,
            Scanner scanner,
            PostService postService
    ) {
        this.replyController = replyController;
        this.memberService = memberService;
        this.scanner = scanner;
        this.postService = postService;
    }

    public void showPostMenu(Long projectId) {
        while (true) {
            printMenu(projectId);

            if (!memberService.isLoggedIn()) {
                System.out.println("로그인해야합니다.");
                return;
            }

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        handleViewAllPosts(projectId);
                        break;

                    case "2":
                        handleViewMyPosts(projectId);
                        break;

                    case "3":
                        handleCreatePost(projectId);
                        break;

                    case "4":
                        handleUpdatePost(projectId);
                        break;

                    case "5":
                        handleDeletePost(projectId);
                        break;

                    case "6":
                        handleEnterPost(projectId);
                        break;

                    case "b":
                        return;

                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            } catch (PostException e) {
                printError(e);
            }
        }
    }

    private void printMenu(Long projectId) {
        System.out.println("\n---------- 게시물 기능 ----------");

        System.out.println("현재 로그인: " + (memberService.isLoggedIn() ? memberService.getCurrentUser().getEmail() : "로그인 필요"));
        System.out.println("현재 접속 중인 프로젝트: " + projectId);

        System.out.println("1. 전체 게시물 보기");
        System.out.println("2. 내가 작성한 게시물 보기");
        System.out.println("3. 게시물 작성");
        System.out.println("4. 게시물 수정");
        System.out.println("5. 게시물 삭제");
        System.out.println("6. 게시물 접속");
        System.out.println("b. 뒤로 가기");
        System.out.print("메뉴를 선택하세요: ");
    }

    private void handleViewAllPosts(Long projectId) {
        List<Post> list = postService.getPostList(projectId);
        printPostList(list);
        pause();
    }

    private void handleViewMyPosts(Long projectId) {
        Long memberId = memberService.getCurrentUser().getId();
        List<Post> list = postService.getMyPostList(projectId, memberId);
        printPostList(list);
        pause();
    }

    private void handleCreatePost(Long projectId) {
        System.out.println("---------- 게시물 작성 ----------");
        try {
            String title = InputUtil.getInput(scanner, "게시물 제목");
            String content = InputUtil.getInput(scanner, "게시물 내용");

            PostRequestDto postRequestDto = new PostRequestDto(title, content);

            postService.createPost(projectId, memberService.getCurrentUser().getId(), postRequestDto);
            System.out.println("게시물 생성 성공!");

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 게시물 작성이 취소되었습니다.");
        }

        pause();
    }

    private void handleUpdatePost(Long projectId) {
        Long memberId = memberService.getCurrentUser().getId();

        System.out.println("---------- 게시물 수정 ----------");

        try {
            List<Post> myPosts = postService.getMyPostList(projectId, memberId);
            printPostList(myPosts);

            Long postId = InputUtil.getLong(scanner, "수정하고 싶은 게시물 번호");

            Post myPost = postService.getPost(postId);
            printPostDetail(myPost);

            String newTitle = askEditField("제목", myPost.getTitle());
            String newContent = askEditField("내용", myPost.getContent());

            PostRequestDto postRequestDto = new PostRequestDto(newTitle, newContent);

            postService.updatePost(projectId, myPost.getId(), memberId, postRequestDto);
            System.out.println("게시물 수정 성공!");

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 게시물 수정이 취소되었습니다.");
        }
        pause();
    }

    private void handleDeletePost(Long projectId) {
        Long memberId = memberService.getCurrentUser().getId();

        System.out.println("---------- 게시물 삭제 ----------");
        try {
            List<Post> myPosts = postService.getMyPostList(projectId, memberId);
            printPostList(myPosts);

            Long postId = InputUtil.getLong(scanner, "삭제할 게시물 번호");

            postService.deletePost(postId, memberId);
            System.out.println("게시물 삭제 성공!");

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 게시물 삭제가 취소되었습니다.");
        }
        pause();
    }

    private void handleEnterPost(Long projectId) {
        try {
            printPostList(postService.getPostList(projectId));

            Long postId = InputUtil.getLong(scanner, "접속할 게시물의 번호");

            Post post = postService.getPostInProject(projectId, postId);
            printPostDetail(post);

            replyController.showReplyMenu(postId);

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 게시물 접속이 취소되었습니다.");
        }
    }

    private String askEditField(String fieldName, String originalValue) {
        while (true) {
            String prompt = String.format("%s을 수정하시겠습니까? (Y/N)", fieldName);
            String c = InputUtil.getInput(scanner, prompt);

            if (c.equals("Y")) {
                return InputUtil.getInput(scanner, "수정할 " + fieldName + " 입력");
            } else if (c.equals("N")) {
                return originalValue;
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void printPostDetail(Post post) {
        System.out.println("---------- 게시물 번호 " + post.getId() + " ----------");
        System.out.println("게시물 제목: " + post.getTitle());
        System.out.println("게시물 내용");
        System.out.println(post.getContent());
        System.out.println();
    }

    private void printPostList(List<Post> postList) {
        System.out.println("---------- 게시물 목록 ----------");
        for (Post post : postList) {
            System.out.println(post.getId() + ". " + post.getTitle());
            System.out.println("게시물 내용: " + post.getContent());
            System.out.println();
        }
    }

    private void printError(Exception e) {
        System.out.println("[오류] " + e.getMessage());
    }

    private void pause() {
        System.out.print("\n엔터키를 누르면 게시물 기능으로 돌아갑니다.");
        scanner.nextLine();
    }
}