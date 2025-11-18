package src.meeting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.Types;
import java.util.List;
import src.utils.Azconnection;

public class MeetingRepository {

	public Meeting save(Meeting meeting) {
        String sql = "INSERT INTO Meeting (project_id, title, description, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        String[] generatedColumns = {"id"};

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, generatedColumns)) {

            pstmt.setLong(1, meeting.getProjectId());
            pstmt.setString(2, meeting.getTitle());
            pstmt.setString(3, meeting.getDescription());

            if (meeting.getStartTime() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(meeting.getStartTime()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }
            
            if (meeting.getEndTime() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(meeting.getEndTime()));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("회의록 저장 실패: 영향받은 행이 없습니다.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    return new Meeting(
                            id,
                            meeting.getProjectId(),
                            meeting.getTitle(),
                            meeting.getDescription(),
                            meeting.getStartTime(),
                            meeting.getEndTime()
                    );
                } else {
                    throw new SQLException("회의록 저장 실패: ID를 가져올 수 없습니다.");
                }
            }

        } catch (SQLException e) {
            System.err.println("DB 저장 중 오류 발생 (Meeting): " + e.getMessage());
            return null;
        }
    }

    public List<Meeting> findByProjectId(Long projectId) {
        List<Meeting> meetingList = new ArrayList<>();
        String sql = "SELECT id, project_id, title, description, start_time, end_time FROM Meeting WHERE project_id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Meeting meeting = new Meeting(
                            rs.getLong("id"),
                            rs.getLong("project_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getObject("start_time", LocalDateTime.class),
                            rs.getObject("end_time", LocalDateTime.class)
                    );
                    meetingList.add(meeting);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생 (Meeting): " + e.getMessage());
        }
        return meetingList;
    }
    
    public Meeting findById(Long meetingId) {
        String sql = "SELECT id, project_id, title, description, start_time, end_time FROM Meeting WHERE id = ?";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, meetingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Meeting(
                            rs.getLong("id"),
                            rs.getLong("project_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getObject("start_time", LocalDateTime.class),
                            rs.getObject("end_time", LocalDateTime.class)
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 단일 조회 중 오류 발생 (Meeting): " + e.getMessage());
        }
        return null;
    }

    public boolean update(Meeting meeting) {
        String sql = "UPDATE Meeting SET title = ?, description = ?, start_time = ?, end_time = ? WHERE id = ?";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, meeting.getTitle());
            pstmt.setString(2, meeting.getDescription());
            if (meeting.getStartTime() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(meeting.getStartTime()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }
            
            if (meeting.getEndTime() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(meeting.getEndTime()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }
            pstmt.setLong(5, meeting.getId()); 

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("DB 수정 중 오류 발생 (Meeting): " + e.getMessage());
        }
        return false;
    }

    public boolean delete(Long meetingId) {
        String sql = "DELETE FROM Meeting WHERE id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, meetingId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("DB 삭제 중 오류 발생 (Meeting): " + e.getMessage());
        }
        return false;
    }
}