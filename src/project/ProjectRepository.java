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

    public Project findById(Long projectId) {
        String sql = "select * from project where id=?";

        Project project = null;

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    project = new Project(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getObject("created_at", LocalDateTime.class),
                            rs.getObject("updated_at", LocalDateTime.class)
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return project;
    }

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

    public Project save(Connection conn, Project project) throws SQLException {
        String sql = "INSERT INTO project (title, description, created_at, updated_at) VALUES (?, ?, ?, ?)";
        String[] generatedColumns = {"id"};

        try (PreparedStatement pstmt = conn.prepareStatement(sql, generatedColumns)) {

            pstmt.setString(1, project.getTitle());
            pstmt.setString(2, project.getDescription());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(project.getCreatedAt()));
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(project.getModifiedAt()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("프로젝트 생성 실패: 영향받은 행이 없습니다.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long generatedId = generatedKeys.getLong(1);

                    // 새로운 Project 객체 생성 후 반환
                    return new Project(
                            generatedId,
                            project.getTitle(),
                            project.getDescription(),
                            project.getCreatedAt(),
                            project.getModifiedAt()
                    );

                } else {
                    throw new SQLException("프로젝트 생성 실패: ID를 가져오지 못했습니다.");
                }
            }
        }
    }

    public boolean updateProject(Project project) {
        String sql = "UPDATE project SET title = ?, description = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, project.getTitle());
            pstmt.setString(2, project.getDescription());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(project.getModifiedAt()));
            pstmt.setLong(4, project.getId());

            // 쿼리 실행 (영향받은 행의 수 반환)
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return true; // 1개 이상의 행이 변경되었으면 성공
            }

        } catch (SQLException e) {
            System.err.println("DB 업데이트 중 오류 발생: " + e.getMessage());
        }

        return false;
    }

    public boolean deleteProject(Long projectId) {
        String sql = "DELETE FROM project WHERE id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);

            // 쿼리 실행 (영향받은 행의 수 반환)
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return true;
            }

        } catch (SQLException e) {
            System.err.println("DB 업데이트 중 오류 발생: " + e.getMessage());
        }

        return false;
    }

    /**
     * @param memberId 현재 로그인한 회원의 ID
     * @param projectId 사용자가 선택한 프로젝트의 ID
     * @return 찾았으면 Project 객체, 못 찾았으면(참여 안 했으면) null
     */
    public Project findMyProjectByIdAndMemberId(Long memberId, Long projectId) {
        String sql = "SELECT p.* " +
                "FROM Project p " +
                "JOIN Participant pa ON p.id = pa.project_id " +
                "WHERE p.id = ? AND pa.member_id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);  // 1번째 ? (p.id)
            pstmt.setLong(2, memberId);   // 2번째 ? (pa.member_id)

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Project(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getObject("created_at", LocalDateTime.class),
                            rs.getObject("updated_at", LocalDateTime.class)
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return null;
    }


    /**
     * @param conn Service에서 전달받은 트랜잭션 Connection
     * @param projectId 조회할 프로젝트의 ID
     * @return
     * @throws SQLException
     */
    public Project findProjectById(Connection conn, Long projectId) throws SQLException {
        String sql = "SELECT * FROM Project WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Project(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getObject("created_at", LocalDateTime.class),
                            rs.getObject("updated_at", LocalDateTime.class)
                    );
                } else {
                    throw new SQLException("Project 조회 실패: ID " + projectId + "를 찾을 수 없습니다.");
                }
            }
        }
    }

}
