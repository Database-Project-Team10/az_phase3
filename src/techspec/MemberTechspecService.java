package src.techspec;

import src.member.Member;
import java.util.List;
import src.utils.Azconnection;
import java.sql.Connection;
import java.sql.SQLException;

public class MemberTechspecService {
    private final MemberTechspecRepository memberTechspecRepository = new MemberTechspecRepository();

    /**
     * 1. 내 스택 목록 보기
     * @param currentUser 현재 로그인한 사용자
     */
    public void viewMyTechspecs(Member currentUser) {
        System.out.println("\n---------- " + currentUser.getName() + "님의 스택 목록 ----------");

        List<Techspec> myTechs = memberTechspecRepository.findTechspecsByMemberId(currentUser.getId());

        if (myTechs.isEmpty()) {
            System.out.println("아직 등록된 스펙이 없습니다.");
        }else{
            for (Techspec tech : myTechs) {
                System.out.println(tech.getId()+". "+tech.getName());
            }
        }
    }

    /**
     * 2. 스택 추가
     * @param currentUser 현재 로그인한 사용자
     * @param techName [!] Controller에서 입력받은 기술 이름
     */
    public void addTechspec(Member currentUser,String techName) {
        // 1. (SELECT) 먼저 마스터 테이블에 기술이 있는지 "밖에서" 확인합니다.
        Long techspecId = memberTechspecRepository.findTechspecIdByName(techName);

        Connection conn = null;
        try {
            // 2. [트랜잭션 시작] Connection을 Service에서 관리합니다.
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false); // [!] 트랜잭션 시작

            // 3. [비즈니스 로직] 1번에서 기술 ID가 없었다면(null),
            //    "새 기술"을 마스터 테이블에 INSERT합니다. (트랜잭션 안에서)
            if (techspecId == null) {
                System.out.println("'" + techName + "' 스택을 마스터 테이블에 새로 등록합니다.");
                // Repository에 트랜잭션 Connection을 전달
                techspecId = memberTechspecRepository.createTechspecAndGetId(conn, techName);
            }

            // 4. [핵심 작업] "회원-기술" 연결을 INSERT합니다. (트랜잭션 안에서)
            //    Repository에 트랜잭션 Connection을 전달
            boolean isSuccess = memberTechspecRepository.addMemberTechspec(conn, currentUser.getId(), techspecId);

            // 5. [트랜잭션 성공] 모든 작업이 성공했으므로 commit
            conn.commit();

            if (isSuccess) { // addMemberTechspec이 true를 반환했다면
                System.out.println("'" + techName + "' 스택이 성공적으로 추가되었습니다.");
            }

        } catch (SQLException e) {
            // [트랜잭션 롤백]
            if (e.getErrorCode() == 1) { // ORA-00001 (Unique Constraint)
                System.out.println("오류: '" + techName + "'(은)는 이미 추가된 스택입니다.");
            } else {
                System.err.println("DB 작업 중 오류 발생: " + e.getMessage());
            }

            try {
                if (conn != null) conn.rollback(); // [!] 모든 작업을 취소
            } catch (SQLException ex) {
                System.err.println("Rollback 실패: " + ex.getMessage());
            }
        } finally {
            // [Connection 반환]
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Connection을 원래 상태로 복구
                    conn.close(); // Connection Pool에 반환
                }
            } catch (SQLException e) {
                System.err.println("Connection 종료 실패: " + e.getMessage());
            }
        }

    }


    /**
     * (개발 중) 3. 스택 삭제 (D)
     * @param currentUser 현재 로그인한 사용자
     * @param techspecId [!] Controller에서 입력받은 기술 이름
     */
    public boolean removeTechspec(Member currentUser, Long techspecId) {
        // [Service 로직 1] Repository를 호출하여 MemberTechspec에서 삭제
        // (findTechspecIdByName이 더 이상 필요 없음!)
        return memberTechspecRepository.deleteMemberTechspec(currentUser.getId(), techspecId);
    }
}
