package src.mbti;

import src.utils.Azconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ProjectMbtiRepository {

    public Map<Long, String> findMbtiMapByProjectId(Long projectId) {
        Map<Long, String> mbtiMap = new HashMap<>();

        String sql = "SELECT mbti_id, preferred_option FROM ProjectMbti WHERE project_id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mbtiMap.put(rs.getLong("mbti_id"), rs.getString("preferred_option"));
                }
            }
        } catch (SQLException e) {
            System.err.println("프로젝트 MBTI 조회 중 오류: " + e.getMessage());
        }
        return mbtiMap;
    }


    public boolean saveProjectMbti(Long projectId, Map<Long, String> mbtiMap) {
        String sql = "MERGE INTO ProjectMbti m " +
                "USING (SELECT ? AS p_id, ? AS b_id, ? AS pre_opt FROM dual) src " +
                "ON (m.project_id = src.p_id AND m.mbti_id = src.b_id) " +
                "WHEN MATCHED THEN " +
                "  UPDATE SET m.preferred_option = src.pre_opt " +
                "WHEN NOT MATCHED THEN " +
                "  INSERT (project_id, mbti_id, preferred_option) " +
                "  VALUES (src.p_id, src.b_id, src.pre_opt)";

        Connection conn = null;
        try {
            conn = Azconnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Map.Entry<Long, String> entry : mbtiMap.entrySet()) {
                    pstmt.setLong(1, projectId);
                    pstmt.setLong(2, entry.getKey());
                    pstmt.setString(3, entry.getValue());
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("DB 저장 중 오류 발생: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback 실패: " + ex.getMessage());
            }
            return false;
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
}