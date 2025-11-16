package src.techspec;

import src.utils.Azconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TechspecRepository {
    /**
     * 특정 회원이 보유한 모든 기술 스택의 "이름"을 조회합니다.
     * @param memberId 조회할 회원의 ID
     * @return 기술 스택 이름(String)이 담긴 List
     */
    public List<String> findTechspecsByMemberId(Long memberId) {
        List<String> myTechs = new ArrayList<>();

        // [!] Phase 2의 DDL을 기반으로 2개 테이블을 JOIN하는 SQL
        // MemberTechspec (회원의 답안지)
        // Techspec (기술 이름 마스터)
        String sql = "SELECT t.name " +
                "FROM MemberTechspec mt " +
                "JOIN Techspec t ON mt.techspec_id = t.id " +
                "WHERE mt.member_id = ?";

        // [!] 새 규칙: Repository에서 직접 Connection을 가져와서 관리
        // try-with-resources: conn, pstmt, rs가 자동으로 close() 됩니다.
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId); // SQL의 '?'에 파라미터 바인딩

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // "Java", "Python" 등 이름(String)을 리스트에 추가
                    myTechs.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
            // (오류가 나면 빈 리스트가 반환됩니다)
        }

        return myTechs; // 조회된 리스트 반환
    }
    public Long findTechspecIdByName(String techName) {
        String sql = "SELECT id FROM Techspec WHERE name = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, techName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id"); // ID를 찾아서 반환
                }
            }
        } catch (SQLException e) {
            System.err.println("Techspec ID 조회 중 오류: " + e.getMessage());
        }
        return null; // 일치하는 기술이 없으면 null 반환
    }
    // [!] --------------------------------------------------------------------
    // [!] 1. (C) Create - Techspec 마스터 테이블에 INSERT
    // [!] --------------------------------------------------------------------
    /**
     * Techspec 마스터 테이블에 새 기술을 INSERT하고, 생성된 ID를 반환합니다.
     * @param conn Service에서 전달받은 트랜잭션 Connection
     * @param techName 새로 추가할 기술 이름 (예: "Git")
     * @return 생성된 새 ID (Long)
     * @throws SQLException
     */
    public Long createTechspecAndGetId(Connection conn, String techName) throws SQLException {
        String sql = "INSERT INTO Techspec (name) VALUES (?)";
        // [!] ProjectRepository에서 썼던 방식과 동일하게, 생성된 ID를 반환받습니다.
        String[] generatedColumns = {"id"};

        try (PreparedStatement pstmt = conn.prepareStatement(sql, generatedColumns)) {
            pstmt.setString(1, techName);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1); // 1번째 컬럼(생성된 ID) 반환
                } else {
                    throw new SQLException("Techspec 생성 실패: ID를 가져오지 못했습니다.");
                }
            }
        }
        // conn.close()는 Service에서 하므로 여기서 닫지 않습니다.
    }

    // [!] --------------------------------------------------------------------
    // [!] 2. (C) Create - (수정) MemberTechspec 테이블에 추가
    // [!] --------------------------------------------------------------------
    /**
     * MemberTechspec 테이블에 회원ID와 기술ID를 INSERT 합니다. (스택 추가)
     * @param conn [!] Service에서 전달받은 트랜잭션 Connection
     * @param memberId 회원 ID
     * @param techspecId 기술 ID
     * @return INSERT 성공 시 true, 실패 시 false
     * @throws SQLException [!] 트랜잭션 관리를 위해 예외를 Service로 던집니다.
     */
    public boolean addMemberTechspec(Connection conn, Long memberId, Long techspecId) throws SQLException {
        String sql = "INSERT INTO MemberTechspec (member_id, techspec_id) VALUES (?, ?)";

        // [!] Repository가 Connection을 직접 만들지 않고, 전달받은 conn을 사용합니다.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);
            pstmt.setLong(2, techspecId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // 1줄 이상 삽입되었으면 true 반환

        }
        // [!] Service가 트랜잭션 처리를 할 수 있도록 catch 블록을 제거하고,
        //     throws SQLException을 메서드에 추가합니다.
    }
    // [!] --------------------------------------------------------------------
    // [!] 1. (D) Delete: MemberTechspec 테이블에서 삭제
    // [!] --------------------------------------------------------------------
    /**
     * MemberTechspec 테이블에서 회원ID와 기술ID가 일치하는 행을 DELETE 합니다. (스택 삭제)
     * @param memberId 회원 ID
     * @param techspecId 기술 ID
     * @return DELETE 성공 시 true, 실패(삭제된 행이 없음) 시 false
     */
    public boolean deleteMemberTechspec(Long memberId, Long techspecId) {
        String sql = "DELETE FROM MemberTechspec WHERE member_id = ? AND techspec_id = ?";

        // 이 작업은 트랜잭션이 필요 없으므로, Repository가 직접 Connection을 관리합니다.
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);
            pstmt.setLong(2, techspecId);

            int affectedRows = pstmt.executeUpdate();

            // 1줄 이상 삭제되었으면(성공) true, 0줄이면(실패) false 반환
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("MemberTechspec 삭제 중 오류: " + e.getMessage());
            return false;
        }
    }
}
