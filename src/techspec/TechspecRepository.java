package src.techspec;

import src.utils.Azconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TechspecRepository {
    /**
     * 특정 회원이 보유한 모든 기술 스택의 "이름"을 조회합니다.
     * @param memberId 조회할 회원의 ID
     * @return 기술 스택 이름(String)이 담긴 List
     */
    public List<String> findTechspecsByMemberId(Long memberId) {
        List<String> myTechs = new ArrayList<>();

        // [!] Phase 2의 DDL을 기반으로 2개 테이블을 JOIN하는 SQL
        // MemberTechspec (회원의 답안지)
        // Techspec (기술 이름 마스터)
        String sql = "SELECT t.name " +
                "FROM MemberTechspec mt " +
                "JOIN Techspec t ON mt.techspec_id = t.id " +
                "WHERE mt.member_id = ?";

        // [!] 새 규칙: Repository에서 직접 Connection을 가져와서 관리
        // try-with-resources: conn, pstmt, rs가 자동으로 close() 됩니다.
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId); // SQL의 '?'에 파라미터 바인딩

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // "Java", "Python" 등 이름(String)을 리스트에 추가
                    myTechs.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
            // (오류가 나면 빈 리스트가 반환됩니다)
        }

        return myTechs; // 조회된 리스트 반환
    }
}
