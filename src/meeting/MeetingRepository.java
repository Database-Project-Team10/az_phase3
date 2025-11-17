package src.meeting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import src.utils.Azconnection;

public class MeetingRepository {

    public boolean save(Meeting meeting) {
        String sql = "INSERT INTO Meeting (project_id, title, description, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, meeting.getProjectId());
            pstmt.setString(2, meeting.getTitle());
            pstmt.setString(3, meeting.getDescription());
            pstmt.setTimestamp(4, Timestamp.valueOf(meeting.getStartTime()));
            pstmt.setTimestamp(5, Timestamp.valueOf(meeting.getEndTime()));

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("DB 저장 중 오류 발생 (Meeting): " + e.getMessage());
        }
        return false;
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
                            rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null,
                            rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null
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
                            rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null,
                            rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null
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
            pstmt.setTimestamp(3, Timestamp.valueOf(meeting.getStartTime()));
            pstmt.setTimestamp(4, Timestamp.valueOf(meeting.getEndTime()));
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