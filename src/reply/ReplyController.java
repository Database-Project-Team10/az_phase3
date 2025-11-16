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
            String content = "";
            Long choiceReplyId = null;

            if (memberService.isLoggedIn()) {
                switch (choice) {
                    case "1": // 댓글 목록
                        showReplyList(replyService.getReplyList(postId));
                        System.out.print("\n엔터키를 누르면 댓글 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "2":
                        showMyReplyList(replyService.getMyReplyList(postId, memberService.getCurrentUser().getId()));
                        System.out.print("\n엔터키를 누르면 댓글 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "3":
                        System.out.println("---------- 댓글 작성 ----------");
                        System.out.print("댓글 내용: ");
                        content = scanner.nextLine();
                        Reply reply = new Reply(postId, memberService.getCurrentUser().getId(), content);
                        if (replyService.createReply(reply)){
                            System.out.println("댓글 생성 성공!");
                        }
                        else {
                            System.out.println("댓글 생성 실패!");
                        }
                        System.out.print("\n엔터키를 누르면 댓글 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "4":
                        showMyReplyList(replyService.getMyReplyList(postId, memberService.getCurrentUser().getId()));

                        System.out.print("수정할 댓글의 번호를 입력하세요: ");
                        choiceReplyId = Long.valueOf(scanner.nextLine());
                        Reply myReply = replyService.getReply(choiceReplyId);

                        System.out.print("수정할 내용 입력: ");
                        content = scanner.nextLine();

                        if (replyService.updateReply(
                                new Reply(
                                        myReply.getId(),
                                        content,
                                        myReply.getCreatedAt()
                                )
                        )){
                            System.out.println("댓글 수정 성공!");
                        }
                        else {
                            System.out.println("댓글 수정 실패!");
                        }
                        System.out.print("\n엔터키를 누르면 댓글 기능으로 돌아갑니다.");
                        scanner.nextLine();
                        break;
                    case "5":
                        showMyReplyList(replyService.getMyReplyList(postId, memberService.getCurrentUser().getId()));

                        System.out.print("삭제할 댓글의 번호를 입력하세요: ");
                        choiceReplyId = Long.valueOf(scanner.nextLine());
                        if (replyService.deleteReply(choiceReplyId)){
                            System.out.println("댓글 삭제 성공!");
                        }
                        else {
                            System.out.println("댓글 삭제 실패!");
                        }
                        System.out.print("\n엔터키를 누르면 댓글 기능으로 돌아갑니다.");
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

    private void showMyReplyList(List<Reply> replyList) {
        System.out.println("---------- 내 댓글 목록 ----------");
        for (Reply reply : replyList){
            System.out.println(reply.getId() + ". " + reply.getContent());
        }
        System.out.println();
    }
}
