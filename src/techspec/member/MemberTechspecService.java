package src.techspec.member;

import src.member.Member;
import src.techspec.Techspec;
import src.techspec.TechspecRepository;
import src.utils.Azconnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MemberTechspecService {
    private final MemberTechspecRepository memberTechspecRepository = new MemberTechspecRepository();
    private final TechspecRepository techspecRepository = new TechspecRepository();

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
        Long techspecId = techspecRepository.findTechspecIdByName(techName);

        Connection conn = null;
        try {
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false); // [!] 트랜잭션 시작

            if (techspecId == null) {
                System.out.println("'" + techName + "' 스택을 마스터 테이블에 새로 등록합니다.");
                techspecId = techspecRepository.createTechspecAndGetId(conn, techName);
            }

            boolean isSuccess = memberTechspecRepository.addMemberTechspec(conn, currentUser.getId(), techspecId);

            conn.commit();

            if (isSuccess) {
                System.out.println("'" + techName + "' 스택이 성공적으로 추가되었습니다.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001 (Unique Constraint)
                System.out.println("오류: '" + techName + "'(은)는 이미 추가된 스택입니다.");
            } else {
                System.err.println("DB 작업 중 오류 발생: " + e.getMessage());
            }

            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback 실패: " + ex.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Connection 종료 실패: " + e.getMessage());
            }
        }

    }


    /**
     * @param currentUser 현재 로그인한 사용자
     * @param techspecId [!] Controller에서 입력받은 기술 이름
     */
    public boolean removeTechspec(Member currentUser, Long techspecId) {
        return memberTechspecRepository.deleteMemberTechspec(currentUser.getId(), techspecId);
    }
}
