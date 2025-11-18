package src.reply;

import src.member.MemberService;
import src.reply.dto.ReplyRequestDto;
import src.reply.dto.ReplyResponseDto;

import java.util.List;
import java.util.Scanner;

public class ReplyController {

    private final MemberService memberService = new MemberService();
    private final ReplyService replyService = new ReplyService();
    private final Scanner scanner = new Scanner(System.in);

    public void showReplyMenu(Long postId) {
        while (true) {
            printMenuHeader(postId);

            if (!memberService.isLoggedIn()) {
                System.out.println("로그인해야합니다.");
                return;
            }

            String choice = getMenuChoice();

            switch (choice) {
                case "1":
                    handleShowAllReplies(postId);
                    break;

                case "2":
                    handleShowMyReplies(postId);
                    break;

                case "3":
                    handleCreateReply(postId);
                    break;

                case "4":
                    handleUpdateReply(postId);
                    break;

                case "5":
                    handleDeleteReply(postId);
                    break;

                case "b":
                    return;

                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void printMenuHeader(Long postId) {
        System.out.println("\n---------- 댓글 기능 ----------");
        System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
        System.out.println("현재 게시물: " + postId);
        System.out.println("1. 전체 댓글 보기");
        System.out.println("2. 내가 쓴 댓글 보기");
        System.out.println("3. 댓글 작성");
        System.out.println("4. 댓글 수정");
        System.out.println("5. 댓글 삭제");
        System.out.println("b. 뒤로 가기");
    }

    private String getMenuChoice() {
        System.out.print("메뉴를 선택하세요: ");
        return scanner.nextLine();
    }

    private void handleShowAllReplies(Long postId) {
        try {
            showReplyList(replyService.getReplyList(postId));
        } catch (Exception e) {
            printError(e);
        }
        pause();
    }

    private void handleShowMyReplies(Long postId) {
        try {
            showMyReplyList(replyService.getMyReplyList(
                    postId,
                    memberService.getCurrentUser().getId()
            ));
        } catch (Exception e) {
            printError(e);
        }
        pause();
    }

    private void handleCreateReply(Long postId) {
        try {
            System.out.println("---------- 댓글 작성 ----------");
            System.out.print("댓글 내용: ");
            String content = scanner.nextLine();

            ReplyRequestDto replyRequestDto = new ReplyRequestDto(content);

            replyService.createReply(postId, memberService.getCurrentUser().getId(), replyRequestDto);
            System.out.println("댓글 생성 성공!");

        } catch (Exception e) {
            printError(e);
        }
        pause();
    }

    private void handleUpdateReply(Long postId) {
        try {
            System.out.println("---------- 댓글 수정 ----------");
            showMyReplyList(replyService.getMyReplyList(
                    postId,
                    memberService.getCurrentUser().getId()
            ));

            System.out.print("수정할 댓글 ID를 입력하세요: ");
            Long choiceReplyId = Long.valueOf(scanner.nextLine());

            Reply myReply = replyService.getReply(choiceReplyId);

            System.out.print("수정할 내용 입력: ");
            String content = scanner.nextLine();
            ReplyRequestDto replyRequestDto = new ReplyRequestDto(content);

            replyService.updateReply(postId, myReply.getId(), memberService.getCurrentUser().getId(), replyRequestDto);
            System.out.println("댓글 수정 성공!");

        } catch (Exception e) {
            printError(e);
        }
        pause();
    }

    private void handleDeleteReply(Long postId) {
        try {
            System.out.println("---------- 댓글 삭제 ----------");
            showMyReplyList(replyService.getMyReplyList(
                    postId,
                    memberService.getCurrentUser().getId()
            ));

            System.out.print("삭제할 댓글 ID를 입력하세요: ");
            Long choiceReplyId = Long.valueOf(scanner.nextLine());

            replyService.deleteReply(choiceReplyId, memberService.getCurrentUser().getId());
            System.out.println("댓글 삭제 성공!");

        } catch (Exception e) {
            printError(e);
        }
        pause();
    }

    private void printError(Exception e) {
        System.out.println("[오류] " + e.getMessage());
    }

    private void pause() {
        System.out.print("\n엔터키를 누르면 댓글 기능으로 돌아갑니다.");
        scanner.nextLine();
    }

    private void showReplyList(List<ReplyResponseDto> replyList) {
        System.out.println("---------- 댓글 목록 (최신순) ----------");
        for (ReplyResponseDto reply : replyList) {
            System.out.println("- " + reply.getName() + ": " + reply.getContent());
        }
        System.out.println();
    }

    private void showMyReplyList(List<Reply> replyList) {
        System.out.println("---------- 내 댓글 목록 ----------");
        for (Reply reply : replyList) {
            System.out.println(reply.getId() + ". " + reply.getContent());
        }
        System.out.println();
    }
}