package src.member;

import src.techspec.TechspecService;
import java.util.Scanner;

public class MemberController {
    private final MemberService memberService = new MemberService();
    private final TechspecService techspecService = new TechspecService();
    private final Scanner scanner = new Scanner(System.in);


    public void showMemberMenu() {
        while (true) {
            System.out.println("\n---------- 회원 기능 ----------");

            // [수정] 로그인 상태에 따라 다른 메뉴 표시
            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("1. 로그아웃");
                System.out.println("2. 회원 정보 수정");
                System.out.println("3. 내 MBTI 입력/수정");
                System.out.println("4. 회원 탈퇴");
                System.out.println("5. 내 테크스펙 관리");
                System.out.println("b. 뒤로 가기");
            } else {
                System.out.println("1. 회원 가입");
                System.out.println("2. 로그인");
                System.out.println("b. 뒤로 가기");
            }

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            if (memberService.isLoggedIn()) {
                // 로그인 상태일 때
                switch (choice) {
                    case "1":
                        memberService.logout();
                        break;
                    case "2":
                        memberService.editMemberInfo();
                        break;
                    case "3":
                        memberService.manageMyMbti();
                         break;
                    case "4":
                        if (memberService.deleteMember()){
                          System.out.println("탈퇴가 완료되었습니다.");
                        }
                        else {
                          System.out.println("탈퇴에 실패했습니다.");
                        }
                        break;
                    case "5":
                        this.showTechspecMenu(memberService.getCurrentUser());
                        break;
                    case "b":
                        return; // 메인 메뉴로
                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            } else {
                // 로그아웃 상태일 때
                switch (choice) {
                    case "1":
                        memberService.signUp();
                        break;
                    case "2":
                        memberService.login();
                        break;
                    case "b":
                        return; // 메인 메뉴로
                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            }
        }
    }
    /**
     * 테크스택 관리 서브 메뉴
     * @param currentUser 현재 로그인한 사용자 정보
     */
    private void showTechspecMenu(Member currentUser) {

        while (true) {
            System.out.println("\n---------- 내 테크스펙 관리 ----------");
            System.out.println("현재 로그인: " + currentUser.getEmail());
            System.out.println("1. 내 스택 목록 보기");
            System.out.println("2. 스택 추가");
            System.out.println("3. 스택 삭제");
            System.out.println("b. 뒤로 가기");
            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    techspecService.viewMyTechspecs(currentUser);
                    break;
                case "2":
                    System.out.print("추가할 기술 스택 이름 (예: Java): ");
                    String techNameToAdd = scanner.nextLine();
                    techspecService.addTechspec(currentUser, techNameToAdd);
                    break;
                case "3":
                    // [!] "삭제" 로직 변경
                    // 1. 먼저 현재 목록을 보여줘서 번호(ID)를 확인하게 함
                    techspecService.viewMyTechspecs(currentUser);
                    System.out.println("----------------------------------------");
                    System.out.print("삭제할 기술 스택의 번호(ID)를 입력하세요 (취소: b): ");
                    String idInput = scanner.nextLine();

                    if ("b".equalsIgnoreCase(idInput)) {
                        System.out.println("삭제를 취소했습니다.");
                        break; // switch 문 종료
                    }

                    try {
                        // 2. 입력받은 번호(String)를 Long으로 변환
                        Long idToDelete = Long.parseLong(idInput);
                        // 3. Service의 "ID로 삭제" 메서드 호출
                        techspecService.removeTechspec(currentUser, idToDelete);
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
