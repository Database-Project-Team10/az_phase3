package src.participant;

import java.sql.*;

public class ParticipantRepository {

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

}
