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
    // [!] "내 테크스택 관리" 서브 메뉴 메서드 (변경 없음)
    /**
     * 테크스택 관리 서브 메뉴 (CRUD 메뉴)
     * @param currentUser 현재 로그인한 사용자 정보
     */
    private void showTechspecMenu(Member currentUser) {

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
                    // (다음) techspecService.viewMyTechspecs(currentUser);
                    System.out.println("(개발 중) 내 스택 목록 보기");
                    break;
                case "2":
                    // (다음) techspecService.addTechspec(currentUser);
                    System.out.println("(개발 중) 스택 추가");
                    break;
                case "3":
                    // (다음) techspecService.removeTechspec(currentUser);
                    System.out.println("(개발 중) 스택 삭제");
                    break;
                case "b":
                    return; // 회원 메뉴(showMemberMenu)로 복귀
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }
}
