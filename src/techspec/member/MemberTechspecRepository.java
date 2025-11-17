package src.techspec.member;

import src.techspec.Techspec;
import src.utils.Azconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberTechspecRepository {

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

    public boolean addMemberTechspec(Connection conn, Long memberId, Long techspecId) throws SQLException {
        String sql = "INSERT INTO MemberTechspec (member_id, techspec_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);
            pstmt.setLong(2, techspecId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        }
    }

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
