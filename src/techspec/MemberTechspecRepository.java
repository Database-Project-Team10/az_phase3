package src.techspec;

import src.utils.Azconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberTechspecRepository {
    /**
     * @param memberId 조회할 회원의 ID
     * @return 기술 스택 이름(String)이 담긴 List
     */
    public List<Techspec> findTechspecsByMemberId(Long memberId) {
        List<Techspec> myTechs = new ArrayList<>();

        String sql = "SELECT t.id, t.name " +
                "FROM MemberTechspec mt " +
                "JOIN Techspec t ON mt.techspec_id = t.id " +
                "WHERE mt.member_id = ?" +
                "ORDER BY t.id";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId); // SQL의 '?'에 파라미터 바인딩

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Long techspecId = rs.getLong(1);
                    String techspecName = rs.getString(2);
                    myTechs.add(new Techspec(techspecId, techspecName));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }

        return myTechs;
    }
    public Long findTechspecIdByName(String techName) {
        String sql = "SELECT id FROM Techspec WHERE UPPER(name) = UPPER(?)";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, techName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Techspec ID 조회 중 오류: " + e.getMessage());
        }
        return null;
    }
    /**
     * Techspec 마스터 테이블에 새 기술을 INSERT하고, 생성된 ID를 반환합니다.
     * @param conn Service에서 전달받은 트랜잭션 Connection
     * @param techName 새로 추가할 기술 이름 (예: "Git")
     * @return 생성된 새 ID (Long)
     * @throws SQLException
     */
    public Long createTechspecAndGetId(Connection conn, String techName) throws SQLException {
        String sql = "INSERT INTO Techspec (name) VALUES (UPPER(?))";
        // [!] ProjectRepository에서 썼던 방식과 동일하게, 생성된 ID를 반환받습니다.
        String[] generatedColumns = {"id"};

        try (PreparedStatement pstmt = conn.prepareStatement(sql, generatedColumns)) {
            pstmt.setString(1, techName);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("Techspec 생성 실패: ID를 가져오지 못했습니다.");
                }
            }
        }
    }

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

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);
            pstmt.setLong(2, techspecId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        }

    }
    /**
     * MemberTechspec 테이블에서 회원ID와 기술ID가 일치하는 행을 DELETE 합니다. (스택 삭제)
     * @param memberId 회원 ID
     * @param techspecId 기술 ID
     * @return DELETE 성공 시 true, 실패(삭제된 행이 없음) 시 false
     */
    public boolean deleteMemberTechspec(Long memberId, Long techspecId) {
        String sql = "DELETE FROM MemberTechspec WHERE member_id = ? AND techspec_id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);
            pstmt.setLong(2, techspecId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("MemberTechspec 삭제 중 오류: " + e.getMessage());
            return false;
        }
    }
}
