package src.techspec;

import src.utils.Azconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TechspecRepository {

    public Long createTechspec(Connection conn, String techName) throws SQLException {
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

    public Techspec findTechspecIdByName(String techName) {
        String sql = "SELECT * FROM Techspec WHERE UPPER(name) = UPPER(?)";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, techName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Techspec(
                            rs.getLong("id"),
                            rs.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Techspec ID 조회 중 오류: " + e.getMessage());
        }
        return null;
    }
}
