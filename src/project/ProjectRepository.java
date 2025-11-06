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

    public List<String> findProjectsByMemberId(Long memberId) {
        List<String> projectList = new ArrayList<>();
        String sql = "SELECT p.title " +
                "FROM project p " +
                "JOIN participant pa ON p.id = pa.project_id " +
                "WHERE pa.member_id = ? " +
                "ORDER BY p.updated_at DESC";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
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
