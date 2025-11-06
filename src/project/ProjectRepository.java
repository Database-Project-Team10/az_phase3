package src.project;

import src.member.Member;
import src.utils.Azconnection;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class ProjectRepository {

    public List<String> findProjects(int cnt) {
        List<String> projectList = new ArrayList<>();
        String sql = "SELECT title FROM project ORDER BY created_at DESC FETCH FIRST ? ROWS ONLY";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cnt);

            try (ResultSet rs = pstmt.executeQuery()) {
                // 결과가 있다면 (rs.next())
                while (rs.next()) {
                    String title = rs.getString("title");
                    projectList.add(title);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }

        return projectList;
    }

    public void save(Project project) throws SQLException {
        String sql = "INSERT INTO project (title, description, created_at, updated_at) VALUES (?, ?, ?, ?)";

        Connection conn = Azconnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, project.getTitle());
        pstmt.setString(2, project.getDescription());
        pstmt.setObject(3, project.getCreatedAt());
        pstmt.setObject(4, project.getModifiedAt());

        int  affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("[Debug] Repository: DB에 " + project.getTitle() + " 저장됨.");
        }

    }
}
