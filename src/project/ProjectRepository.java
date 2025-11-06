package src.project;

import src.utils.Azconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {

    public List<Project> findProjects(int cnt) {
        List<Project> projectList = new ArrayList<>();
        String sql = "SELECT * FROM project ORDER BY created_at DESC FETCH FIRST ? ROWS ONLY";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cnt);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Project project = new Project(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getObject("created_at", LocalDateTime.class),
                            rs.getObject("updated_at", LocalDateTime.class)
                    );
                    projectList.add(project);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return projectList;
    }

    public List<Project> findProjectsByMemberId(long memberId) {
        List<Project> projectList = new ArrayList<>();

        String sql = "SELECT p.id, p.title, p.description, p.created_at, p.updated_at " +
                "FROM project p " +
                "JOIN participant pa ON p.id = pa.project_id " +
                "WHERE pa.member_id = ? " +
                "ORDER BY p.updated_at DESC";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Project project = new Project(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getObject("created_at", LocalDateTime.class),
                            rs.getObject("updated_at", LocalDateTime.class)
                    );
                    projectList.add(project);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return projectList;
    }

    public Long save(Connection conn, Project project) throws SQLException {
        String sql = "INSERT INTO project (title, description, created_at, updated_at) VALUES (?, ?, ?, ?)";

        String[] generatedColumns = {"id"};

        PreparedStatement pstmt = conn.prepareStatement(sql, generatedColumns);

        pstmt.setString(1, project.getTitle());
        pstmt.setString(2, project.getDescription());
        pstmt.setObject(3, project.getCreatedAt());
        pstmt.setObject(4, project.getModifiedAt());

        int  affectedRows = pstmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("프로젝트 생성 실패: 영향받은 행이 없습니다.");
        }
        // [핵심] 2. 생성된 ID(PK)를 반환
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1); // 1번째 컬럼(생성된 ID) 반환
            } else {
                throw new SQLException("프로젝트 생성 실패: ID를 가져오지 못했습니다.");
            }
        }

    }
}
