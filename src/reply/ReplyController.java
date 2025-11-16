package src.reply;

import src.member.MemberService;

import java.util.List;
import java.util.Scanner;

public class ReplyController {

    private final MemberService memberService = new MemberService();
    private final ReplyService replyService = new ReplyService();
    private final Scanner scanner = new Scanner(System.in);

    public void showReplyMenu(Long postId) {
        while (true) {
            System.out.println("\n---------- 댓글 기능 ----------");
            if (memberService.isLoggedIn()){
                System.out.println("1. 댓글 전체 보기");
                System.out.println("2. 내가 쓴 댓글 보기");
                System.out.println("3. 댓글 쓰기");
                System.out.println("4. 댓글 수정");
                System.out.println("5. 댓글 삭제");
                System.out.println("b. 뒤로 가기");
            } else {
                System.out.println("로그인해야합니다.");
                return;
            }

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            if (memberService.isLoggedIn()) {
                switch (choice) {
                    case "1": // 댓글 목록
                        showReplyList(replyService.getReplyList(postId));
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

    private void showReplyList(List<Reply> replyList) {
        System.out.println("---------- 댓글 목록 (최신순) ----------");
        for (Reply reply : replyList){
            System.out.println("- "+memberService.getMemberInfo(reply.getMemberId()) + ": " + reply.getContent());
        }
        System.out.println();
    }
}
