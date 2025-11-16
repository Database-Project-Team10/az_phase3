package src.techspec;

import src.member.Member;
import java.util.Scanner;

/**
 * [Member]의 테크스택 관련 "입력(UI)"을 담당하는 컨트롤러
 */
public class MemberTechspecController {

    // [!] 방금 이름을 바꾼 Service를 사용
    private final MemberTechspecService memberTechspecService = new MemberTechspecService();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * "내 테크스택 관리" 서브 메뉴 (CRUD 메뉴)
     * (MemberController로부터 호출됨)
     * @param currentUser 현재 로그인한 사용자 정보
     */
    public void showMemberTechspecMenu(Member currentUser) {

        while (true) {
            System.out.println("\n---------- 내 테크스택 관리 ----------");
            System.out.println("현재 로그인: " + currentUser.getEmail());
            System.out.println("1. 내 스택 목록 보기 (R)");
            System.out.println("2. 스택 추가 (C)");
            System.out.println("3. 스택 삭제 (D)");
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
                    memberTechspecService.viewMyTechspecs(currentUser); // 목록 먼저 표시
                    System.out.println("----------------------------------------");
                    System.out.print("삭제할 기술 스택의 번호(ID)를 입력하세요 (취소: b): ");
                    String idInput = scanner.nextLine();

                    if ("b".equalsIgnoreCase(idInput)) {
                        System.out.println("삭제를 취소했습니다.");
                        break;
                    }
                    try {
                        Long idToDelete = Long.parseLong(idInput);
                        memberTechspecService.removeTechspec(currentUser, idToDelete);
                    } catch (NumberFormatException e) {
                        System.out.println("오류: 유효한 숫자를 입력해야 합니다.");
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