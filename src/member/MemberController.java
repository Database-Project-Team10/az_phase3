package src.member;

import src.mbti.MemberMbtiService;
import src.mbti.MbtiDimension;
import src.techspec.MemberTechspecController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MemberController {
    private final MemberService memberService = new MemberService();
    //private final MemberTechspecService memberTechspecService = new MemberTechspecService();
    private final MemberTechspecController memberTechspecController = new MemberTechspecController();
    private final MemberMbtiService memberMbtiService = new MemberMbtiService();
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
                         this.manageMyMbti(memberService.getCurrentUser());
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

    private void manageMyMbti(Member currentUser) {
        System.out.println("---------- 내 MBTI 입력/수정 ----------");

        // 1. (Service 호출) DB에서 MBTI 4가지 차원 정의를 가져옴
        List<MbtiDimension> dimensions = memberMbtiService.getMbtiDimensions();
        if (dimensions == null || dimensions.isEmpty()) {
            System.out.println("오류: MBTI 마스터 데이터를 불러올 수 없습니다.");
            return;
        }

        // 2. (Service 호출) 현재 설정된 MBTI 값을 가져옴
        Map<Long, String> currentMbti = memberMbtiService.getMbtiMapByMemberId(currentUser.getId());
        System.out.print("현재 설정된 MBTI: ");
        for (MbtiDimension dim : dimensions) {
            System.out.print(currentMbti.getOrDefault(dim.getId(), "?"));
        }
        System.out.println("\n(4가지 차원을 모두 입력합니다.)");


        // 3. (Controller) 사용자에게 4가지 차원 "입력받기"
        Map<Long, String> newMbtiMap = new HashMap<>();
        for (MbtiDimension dim : dimensions) {
            String input = "";
            while (true) {
                System.out.printf("%d. %s (%s/%s): ", dim.getId(), dim.getDimensionType(), dim.getOption1(), dim.getOption2());
                input = scanner.nextLine().toUpperCase(); // 대문자로 변경

                if (input.equals(dim.getOption1()) || input.equals(dim.getOption2())) {
                    newMbtiMap.put(dim.getId(), input); // Map에 저장 (key: mbti_id, value: 'E')
                    break;
                } else {
                    System.out.printf("잘못된 입력입니다. %s 또는 %s를 입력해주세요.\n", dim.getOption1(), dim.getOption2());
                }
            }
        }

        // 4. (Service 호출) DB에 저장 요청
        boolean isSuccess = memberMbtiService.saveMyMbti(currentUser.getId(), newMbtiMap);

        if (isSuccess) {
            System.out.println("MBTI 정보가 성공적으로 저장되었습니다.");
        } else {
            System.out.println("오류: MBTI 정보 저장에 실패했습니다.");
        }
    }

}
