package src.techspec.member;

import src.member.Member;
import java.util.Scanner;

public class MemberTechspecController {

    private final MemberTechspecService memberTechspecService = new MemberTechspecService();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * (MemberController로부터 호출됨)
     * @param currentUser 현재 로그인한 사용자 정보
     */
    public void showMemberTechspecMenu(Member currentUser) {

        while (true) {
            System.out.println("\n---------- 내 테크스택 관리 ----------");
            System.out.println("현재 로그인: " + currentUser.getEmail());
            System.out.println("1. 내 스택 목록 보기");
            System.out.println("2. 스택 추가");
            System.out.println("3. 스택 삭제");
            System.out.println("b. 뒤로 가기 (회원 메뉴)");
            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    memberTechspecService.viewMyTechspecs(currentUser);
                    break;
                case "2":
                    System.out.print("추가할 기술 스택 이름 (예: Java, Git): ");
                    String techNameToAdd = scanner.nextLine();
                    memberTechspecService.addTechspec(currentUser, techNameToAdd);
                    break;
                case "3":
                    memberTechspecService.viewMyTechspecs(currentUser);
                    System.out.println("----------------------------------------");
                    System.out.print("삭제할 기술 스택의 번호(ID)를 입력하세요 (취소: b): ");
                    String idInput = scanner.nextLine();

                    if ("b".equalsIgnoreCase(idInput)) {
                        System.out.println("삭제를 취소했습니다.");
                        break;
                    }

                    Long idToDelete = Long.parseLong(idInput);
                    if (memberTechspecService.removeTechspec(currentUser, idToDelete)) {
                        System.out.println("삭제 성공!");
                    } else {
                        System.out.println("삭제 실패!");
                    }
                    break;
                case "b":
                    return; // 회원 메뉴(showMemberMenu)로 복귀
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }
}