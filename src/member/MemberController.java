package src.member;

import src.mbti.member.MemberMbtiController;
import src.techspec.MemberTechspecController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MemberController {
    private final MemberService memberService = new MemberService();
    //private final MemberTechspecService memberTechspecService = new MemberTechspecService();
    private final MemberTechspecController memberTechspecController = new MemberTechspecController();
    private final MemberMbtiController memberMbtiController = new MemberMbtiController();
    private final Scanner scanner = new Scanner(System.in);


    public void showMemberMenu() {
        while (true) {
            System.out.println("\n---------- 회원 기능 ----------");

            if (memberService.isLoggedIn()) {
                System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
                System.out.println("1. 로그아웃");
                System.out.println("2. 비밀번호 수정");
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
                        if (memberService.logout()) {
                            System.out.println("'로그아웃 성공!");
                        }
                        else {
                            System.out.println("로그인 상태가 아닙니다.");
                        }
                        break;
                    case "2":
                        System.out.println("----- 회원 정보 수정 -----");
                        System.out.println("현재 이메일: " + memberService.getCurrentUser().getEmail());

                        System.out.print("새 비밀번호: ");
                        String newPassword = scanner.nextLine();
                        System.out.print("새 비밀번호 확인: ");
                        String confirmPassword = scanner.nextLine();

                        if (memberService.editPassword(newPassword, confirmPassword)){
                            System.out.println("비밀번호 변경 성공!");
                        }
                        else{
                            System.out.println("비밀번호 변경 실패!");
                        }
                        break;
                    case "3":
                        memberMbtiController.showMemberMbtiMenu(memberService.getCurrentUser());
                        break;
                    case "4":
                        System.out.print("정말로 탈퇴하시겠습니까? (Y/N) ");
                        if (memberService.deleteMember(scanner.nextLine())){
                          System.out.println("탈퇴가 완료되었습니다.");
                        }
                        else {
                          System.out.println("탈퇴에 실패했습니다.");
                        }
                        break;
                    case "5":
                        memberTechspecController.showMemberTechspecMenu(memberService.getCurrentUser());
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
                        System.out.println("---------- 회원 가입 ----------");
                        System.out.print("사용할 이메일: ");
                        String newEmail = scanner.nextLine();
                        System.out.print("사용할 비밀번호: ");
                        String newPassword = scanner.nextLine();
                        System.out.print("비밀번호 확인: ");
                        String confirmPassword = scanner.nextLine();
                        System.out.print("이름: ");
                        String newName = scanner.nextLine();
                        System.out.print("생년월일(YYYY-MM-DD): ");
                        String newBirthDate = scanner.nextLine();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate localBirthDate = LocalDate.parse(newBirthDate, formatter);

                        Member member = new Member(newEmail, newPassword, newName, localBirthDate);

                        if (memberService.signUp(member, confirmPassword)){
                            System.out.println("'" + member.getEmail() + "'님, 회원가입이 완료되었습니다!");
                        }
                        else {
                            System.out.println("회원 가입에 실패했습니다.");
                        }
                        break;
                    case "2":
                        System.out.println("---------- 로그인 ----------");
                        System.out.print("이메일: ");
                        String email = scanner.nextLine();
                        System.out.print("비밀번호: ");
                        String password = scanner.nextLine();
                        if (memberService.login(email, password)){
                            System.out.println("로그인 성공! '" + email + "'님, 환영합니다.");
                        }
                        else {
                            System.out.println("로그인 실패! 이메일 혹은 비밀번호가 잘못되었습니다.");
                        }
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
