package src.techspec;

import src.utils.Azconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectTechspecRepository {
    /**
     * (R) Read: 특정 프로젝트의 요구 스택 목록을 조회 (ID로 정렬)
     * (단순 조회이므로 Repository가 conn 관리)
     */
    public List<Techspec> findTechspecsByProjectId(Long projectId) {
        List<Techspec> projectTechs = new ArrayList<>();
        String sql = "SELECT t.id, t.name " +
                "FROM ProjectTechspec pt " +
                "JOIN Techspec t ON pt.techspec_id = t.id " +
                "WHERE pt.project_id = ? " +
                "ORDER BY t.id";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    projectTechs.add(new Techspec(
                            rs.getLong("id"),
                            rs.getString("name")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return projectTechs;
    }

    /**
     * (C) Create: ProjectTechspec 테이블에 연결고리 INSERT
     * (Service의 트랜잭션에 포함되어야 하므로 conn을 전달받음)
     */
    public boolean addProjectTechspec(Connection conn, Long projectId, Long techspecId) throws SQLException {
        String sql = "INSERT INTO ProjectTechspec (project_id, techspec_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, projectId);
            pstmt.setLong(2, techspecId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
        // catch는 Service에서 처리
    }

    /**
     * (D) Delete: ProjectTechspec 테이블에서 연결고리 DELETE
     * (단순 삭제이므로 Repository가 conn 관리)
     */
    public boolean deleteProjectTechspec(Long projectId, Long techspecId) {
        String sql = "DELETE FROM ProjectTechspec WHERE project_id = ? AND techspec_id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, projectId);
            pstmt.setLong(2, techspecId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("ProjectTechspec 삭제 중 오류: " + e.getMessage());
            return false;
        }
    }
}
