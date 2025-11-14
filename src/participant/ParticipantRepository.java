package src.participant;

import src.utils.Azconnection;

import java.sql.*;

public class ParticipantRepository {

    public boolean save(Long projectId, Long memberId){
        String sql = "INSERT INTO participant (member_id, project_id, role) Values (?, ?, 'MEMBER')";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);
            pstmt.setLong(2, projectId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("[Debug] Repository: 회원 번호(" + memberId + ")가 프로젝트 번호 (" + projectId + ")에 참여 완료");
                return true; // 1개 이상의 행이 변경되었으면 성공
            }

        } catch (SQLException e) {
            System.err.println("DB 업데이트 중 오류 발생: " + e.getMessage());
        }

        return false;
    }

    public void saveLeader(Connection conn, long memberId, long projectId) throws SQLException {
        // (테이블명 participant, 역할 'LEADER'로 가정)
        String sql = "INSERT INTO participant (member_id, project_id, role) VALUES (?, ?, 'LEADER')";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, memberId);
            pstmt.setLong(2, projectId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("참가자(리더) 추가 실패: 영향받은 행이 없습니다.");
            }
        }
    }

    public boolean exists(Long projectId, Long memberId){
        String sql = "SELECT * FROM participant WHERE project_id = ? AND member_id = ?";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);
            pstmt.setLong(2, memberId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("DB 업데이트 중 오류 발생: " + e.getMessage());
        }

        return false;
    }

}
