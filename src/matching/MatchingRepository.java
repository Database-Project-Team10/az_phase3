package src.matching;

import src.utils.Azconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MatchingRepository {
    //MBTI 기반 매칭 (우선순위 상위 10개)
    public List<MatchedProject> findMbtiMatches(Long memberId) {
        List<MatchedProject> results = new ArrayList<>();
        String sql =
                "WITH UserMBTI AS ( " +
                        "    SELECT mbti_id, selected_option FROM MemberMbti WHERE member_id = ? " +
                        "), " +
                        "ProjectMatches AS ( " +
                        "    SELECT p.id, p.title, COUNT(pm.mbti_id) AS match_count, " +
                        "           LISTAGG(pm.preferred_option, ', ') WITHIN GROUP (ORDER BY pm.mbti_id) AS matching_traits " +
                        "    FROM ProjectMbti pm " +
                        "    JOIN UserMBTI um ON pm.mbti_id = um.mbti_id AND pm.preferred_option = um.selected_option " +
                        "    JOIN Project p ON pm.project_id = p.id " +
                        "    GROUP BY p.id, p.title " +
                        "), " +
                        "RankedMatches AS ( " +
                        "    SELECT id, title, match_count, matching_traits, " +
                        "           DENSE_RANK() OVER (ORDER BY match_count DESC) AS rnk " +
                        "    FROM ProjectMatches " +
                        ") " +
                        "SELECT id, title, match_count, matching_traits " +
                        "FROM RankedMatches " +
                        "WHERE rnk = 1 " +
                        "FETCH FIRST 10 ROWS ONLY";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new MatchedProject(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getInt("match_count"),
                            "일치 성향: " + rs.getString("matching_traits")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("MBTI 매칭 조회 중 오류: " + e.getMessage());
        }
        return results;
    }


     //테크스펙 기반 매칭 (일치 개수 많은 순 상위 10개)
    public List<MatchedProject> findTechMatches(Long memberId) {
        List<MatchedProject> results = new ArrayList<>();
        String sql =
                "WITH UserTech AS ( " +
                        "    SELECT techspec_id FROM MemberTechspec WHERE member_id = ? " +
                        ") " +
                        "SELECT p.id, p.title, COUNT(pt.techspec_id) AS match_count, " +
                        "       LISTAGG(t.name, ', ') WITHIN GROUP (ORDER BY t.name) AS matching_skills " +
                        "FROM ProjectTechspec pt " +
                        "JOIN UserTech ut ON pt.techspec_id = ut.techspec_id " +
                        "JOIN Project p ON pt.project_id = p.id " +
                        "JOIN Techspec t ON pt.techspec_id = t.id " +
                        "GROUP BY p.id, p.title " +
                        "ORDER BY match_count DESC " +
                        "FETCH FIRST 10 ROWS ONLY";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new MatchedProject(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getInt("match_count"),
                            "일치 스택: " + rs.getString("matching_skills")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Tech 매칭 조회 중 오류: " + e.getMessage());
        }
        return results;
    }


     //종합 매칭 (MBTI + Techspec 점수)
    public List<MatchedProject> findCombinedMatches(Long memberId) {
        List<MatchedProject> results = new ArrayList<>();
        String sql =
                "WITH UserMBTI AS ( " +
                        "    SELECT mbti_id, selected_option FROM MemberMbti WHERE member_id = ? " +
                        "), " +
                        "UserTech AS ( " +
                        "    SELECT techspec_id FROM MemberTechspec WHERE member_id = ? " +
                        "), " +
                        "MbtiMatches AS ( " +
                        "    SELECT pm.project_id, COUNT(pm.mbti_id) AS mbti_match_count " +
                        "    FROM ProjectMbti pm " +
                        "    JOIN UserMBTI um ON pm.mbti_id = um.mbti_id AND pm.preferred_option = um.selected_option " +
                        "    GROUP BY pm.project_id " +
                        "), " +
                        "TechMatches AS ( " +
                        "    SELECT pt.project_id, COUNT(pt.techspec_id) AS tech_match_count " +
                        "    FROM ProjectTechspec pt " +
                        "    JOIN UserTech ut ON pt.techspec_id = ut.techspec_id " +
                        "    GROUP BY pt.project_id " +
                        ") " +
                        "SELECT p.id, p.title, mm.mbti_match_count, tm.tech_match_count, " +
                        "       (mm.mbti_match_count * 2) + tm.tech_match_count AS total_score " +
                        "FROM Project p " +
                        "JOIN MbtiMatches mm ON p.id = mm.project_id " +
                        "JOIN TechMatches tm ON p.id = tm.project_id " +
                        "ORDER BY total_score DESC " +
                        "FETCH FIRST 10 ROWS ONLY";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);
            pstmt.setLong(2, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int mbtiCount = rs.getInt("mbti_match_count");
                    int techCount = rs.getInt("tech_match_count");
                    String desc = String.format("MBTI 일치: %d개, Tech 일치: %d개", mbtiCount, techCount);

                    results.add(new MatchedProject(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getInt("total_score"),
                            desc
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("종합 매칭 조회 중 오류: " + e.getMessage());
        }
        return results;
    }
}