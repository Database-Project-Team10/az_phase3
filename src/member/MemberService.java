package src.member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import src.utils.Azconnection;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberService {

    private final MemberRepository memberRepository = new MemberRepository();
    private final Scanner sc = new Scanner(System.in);

    private static Member loggedInUser = null;

    public void signUp() {

        if (isLoggedIn()) {
            System.out.println("이미 로그인되어 있습니다. 먼저 로그아웃해주세요.");
            return;
        }

        System.out.println("---------- 회원 가입 ----------");
        System.out.print("사용할 이메일: ");
        String email = sc.nextLine();

        // [비즈니스 로직 1] 아이디 중복 검사
        if (memberRepository.findByEmail(email) != null) {
            System.out.println("이미 존재하는 아이디입니다. 다른 아이디를 입력해주세요.");
            return;
        }

        System.out.print("사용할 비밀번호: ");
        String password = sc.nextLine();
        System.out.print("비밀번호 확인: ");
        String confirmPassword = sc.nextLine();

        // [비즈니스 로직 2] 비밀번호 확인
        if (!password.equals(confirmPassword)) {
            System.out.println("비밀번호가 일치하지 않습니다. 다시 시도해주세요.");
            return;
        }

        System.out.print("이름: ");
        String name = sc.nextLine();
        System.out.print("생년월일(YYYY-MM-DD): ");
        String birthDate = sc.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localBirthDate = LocalDate.parse(birthDate, formatter);

        Member newMember = new Member(email, password, name, localBirthDate);
        // 2. Repository를 통해 저장
        try {
            memberRepository.save(newMember);
            System.out.println("'" + email + "'님, 회원가입이 완료되었습니다!");
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated
                System.err.println("저장 실패: 아이디 중복 (DB)");
            } else {
                System.err.println("DB 저장 중 오류 발생: " + e.getMessage());
            }
        }

    }

    public void login() {
        if (isLoggedIn()) {
            System.out.println("이미 로그인되어 있습니다.");
            return;
        }

        System.out.println("---------- 로그인 ----------");
        System.out.print("이메일: ");
        String email = sc.nextLine();
        System.out.print("비밀번호: ");
        String password = sc.nextLine();

        // [비즈니스 로직 3] 사용자 조회
        Member member = memberRepository.findByEmail(email);

        // [비즈니스 로직 4] 비밀번호 검증
        if (member != null && member.getPassword().equals(password)) {
            // 로그인 성공
            loggedInUser = member; // static 변수에 현재 로그인한 사용자 정보 저장
            System.out.println("로그인 성공! '" + loggedInUser.getEmail() + "'님, 환영합니다.");
        } else {
            // 로그인 실패
            System.out.println("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    public void editMemberInfo() {
        if (!isLoggedIn()) {
            System.out.println("로그인이 필요합니다.");
            return;
        }

        System.out.println("----- 회원 정보 수정 -----");
        System.out.println("현재 이메일: " + loggedInUser.getEmail());

        System.out.print("새 비밀번호: ");
        String newPassword = sc.nextLine();
        System.out.print("새 비밀번호 확인: ");
        String confirmPassword = sc.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return;
        }

        boolean isSuccess = memberRepository.updatePassword(loggedInUser.getEmail(), newPassword);

        if (isSuccess) {
            // 현재 메모리에 들고 있는 loggedInUser 객체의 정보도 동기화
            loggedInUser.setPassword(newPassword);

            System.out.println("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            System.out.println("오류가 발생하여 비밀번호 변경에 실패했습니다.");
        }
    }

    //Mbti 입력/수정
    public void manageMyMbti() {
        if (!isLoggedIn()) {
            System.out.println("로그인이 필요합니다.");
            return;
        }

        System.out.println("---------- 내 MBTI 입력/수정 ----------");

        // [Service 로직 1] DB에서 MBTI 4가지 차원 정의를 가져옴
        // (아직 MemberRepository에 만들지 않았지만, 곧 만들 예정)
        List<MbtiDimension> dimensions = memberRepository.findAllMbtiDimensions();
        if (dimensions == null || dimensions.isEmpty()) {
            System.out.println("오류: MBTI 마스터 데이터를 불러올 수 없습니다.");
            return;
        }

        // [Service 로직 2] 현재 설정된 MBTI 값을 가져옴 (key: mbti_id, value: selected_option)
        Map<Long, String> currentMbti = memberRepository.findMbtiMapByMemberId(loggedInUser.getId());

        System.out.print("현재 설정된 MBTI: ");
        for (MbtiDimension dim : dimensions) {
            // Map에서 mbti_id(dim.getId())를 키로 값을 찾고, 없으면 "?" 출력
            System.out.print(currentMbti.getOrDefault(dim.getId(), "?"));
        }
        System.out.println("\n(4가지 차원을 모두 입력합니다.)");


        // [Service 로직 3] 사용자에게 4가지 차원 입력받기
        Map<Long, String> newMbtiMap = new HashMap<>(); // (key: mbti_id, value: 'E')

        for (MbtiDimension dim : dimensions) {
            String input = "";
            while (true) {
                // 예: "1. Energy (E/I): "
                System.out.printf("%d. %s (%s/%s): ", dim.getId(), dim.getDimensionType(), dim.getOption1(), dim.getOption2());
                input = sc.nextLine().toUpperCase(); // 대문자로 변경

                // [비즈니스 로직] 유효성 검사
                if (input.equals(dim.getOption1()) || input.equals(dim.getOption2())) {
                    newMbtiMap.put(dim.getId(), input); // Map에 저장
                    break;
                } else {
                    System.out.printf("잘못된 입력입니다. %s 또는 %s를 입력해주세요.\n", dim.getOption1(), dim.getOption2());
                }
            }
        }

        // [Service 로직 4] DB에 트랜잭션으로 저장 (ProjectService의 createProject 참고)
        Connection conn = null;
        try {
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false); // [!] 트랜잭션 시작

            // (아직 MemberRepository에 만들지 않았지만, 곧 만들 예정)
            memberRepository.upsertMemberMbti(conn, loggedInUser.getId(), newMbtiMap);

            conn.commit(); // [!] 트랜잭션 성공 (커밋)
            System.out.println("MBTI 정보가 성공적으로 저장되었습니다.");

        } catch (SQLException e) {
            System.err.println("DB 저장 중 오류 발생: " + e.getMessage());
            try {
                if (conn != null) conn.rollback(); // [!] 트랜잭션 실패 (롤백)
            } catch (SQLException ex) {
                System.err.println("Rollback 실패: " + ex.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Auto-Commit 원상복구
                    conn.close(); // Connection 반환
                }
            } catch (SQLException e) {
                System.err.println("Connection 종료 실패: " + e.getMessage());
            }
        }
    }
    public boolean deleteMember() {
        if (!isLoggedIn()) {
            System.out.println("로그인이 필요합니다.");
            return false;
        }
        System.out.print("정말로 탈퇴하시겠습니까? (Y/N) ");
        String choice = sc.nextLine();
        if (choice.equalsIgnoreCase("Y")) {
            Long deleteUserId = loggedInUser.getId();
            logout();
            return memberRepository.delete(deleteUserId);
        }
        else if (choice.equalsIgnoreCase("N")) {
            return false;
        }
        else {
            System.out.println("잘못된 입력입니다.");
        }
        return false;
    }

    /**
     * 로그아웃 로직
     */
    public void logout() {
        if (!isLoggedIn()) {
            System.out.println("로그인 상태가 아닙니다.");
            return;
        }
        System.out.println("'" + loggedInUser.getEmail() + "'님이 로그아웃하셨습니다.");
        loggedInUser = null; // 로그인 정보 제거
    }

    // 로그인 상태 확인
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    //현재 로그인된 사용자 반환
    public Member getCurrentUser() {
        return loggedInUser;
    }
}
