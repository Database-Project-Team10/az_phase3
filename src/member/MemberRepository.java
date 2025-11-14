package src.member;

import src.utils.Azconnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberRepository {

    public Member findByEmail(String email) {
        String sql = "SELECT * FROM member WHERE email = ?";

        // try-with-resources: conn, pstmt, rs가 자동으로 close() 됩니다.
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                // 결과가 있다면 (rs.next())
                if (rs.next()) {
                    // 찾은 정보로 Member 객체를 생성하여 반환
                    return new Member(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getObject("birth_date", LocalDate.class),
                            rs.getObject("created_at", LocalDateTime.class)
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }

        // 일치하는 사용자가 없으면 null을 반환
        return null;
    }

    public void save(Member member) throws SQLException{
        // [SQL] members 테이블에 username과 password를 삽입합니다.
        String sql = "INSERT INTO member (email, password, name, birth_date, created_at) VALUES (?, ?, ?, ? ,?)";

        // try-with-resources: conn과 pstmt가 자동으로 close() 됩니다.
        Connection conn = Azconnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);

        // SQL의 '?'에 값을 바인딩합니다. (SQL Injection 방지)
        pstmt.setString(1, member.getEmail());
        pstmt.setString(2, member.getPassword());
        pstmt.setString(3, member.getName());
        pstmt.setObject(4, member.getBirthDate());
        pstmt.setObject(5, member.getCreatedAt());


        // 쿼리 실행
        int affectedRows = pstmt.executeUpdate();

        if (affectedRows > 0) {
            System.out.println("[Debug] Repository: DB에 " + member.getEmail() + " 저장됨.");
        }

    }

    public boolean updatePassword(String email, String newPassword) {
        // [SQL] username을 기준으로 password를 업데이트합니다.
        String sql = "UPDATE member SET password = ? WHERE email = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 1. ? (새 비밀번호)
            pstmt.setString(1, newPassword);
            // 2. ? (대상 email)
            pstmt.setString(2, email);

            // 쿼리 실행 (영향받은 행의 수 반환)
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("[Debug] Repository: " + email + "의 비밀번호 변경됨.");
                return true; // 1개 이상의 행이 변경되었으면 성공
            }

        } catch (SQLException e) {
            System.err.println("DB 업데이트 중 오류 발생: " + e.getMessage());
        }

        return false; // 업데이트 실패
    }
    /**
     * [추가] MBTI 마스터 테이블의 4가지 차원 정의를 모두 조회합니다.
     * (Service가 "설문지 원본"을 가져가기 위해 사용)
     */
    public List<MbtiDimension> findAllMbtiDimensions() {
        List<MbtiDimension> dimensions = new ArrayList<>();
        // Phase 2 DDL의 MBTI 테이블
        String sql = "SELECT id, dimension_type, option1, option2 FROM MBTI ORDER BY id";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // DB에서 가져온 한 줄(row)을 MbtiDimension 헬퍼 객체로 변환
                dimensions.add(new MbtiDimension(
                        rs.getLong("id"),
                        rs.getString("dimension_type"),
                        rs.getString("option1"),
                        rs.getString("option2")
                ));
            }
        } catch (SQLException e) {
            System.err.println("MBTI 차원 조회 중 오류: " + e.getMessage());
            return null; // 실패 시 Service가 알 수 있도록 null 반환
        }
        return dimensions; // 4개의 차원 정보가 담긴 리스트 반환
    }

    /**
     * [추가] 특정 회원의 현재 MBTI 설정을 Map으로 조회합니다.
     * (Service가 "기존 답안지"를 가져오기 위해 사용)
     */
    public Map<Long, String> findMbtiMapByMemberId(Long memberId) {
        Map<Long, String> mbtiMap = new HashMap<>();
        // Phase 2 DDL의 MemberMbti 테이블
        String sql = "SELECT mbti_id, selected_option FROM MemberMbti WHERE member_id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId); // SQL의 '?'에 로그인한 회원 ID 바인딩
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // (key: 1, value: "E"), (key: 2, value: "N") ...
                    // 조회된 결과를 Map에 담습니다.
                    mbtiMap.put(rs.getLong("mbti_id"), rs.getString("selected_option"));
                }
            }
        } catch (SQLException e) {
            System.err.println("회원 MBTI 조회 중 오류: " + e.getMessage());
        }
        // 조회된 결과가 없으면 빈 Map을 반환합니다.
        return mbtiMap;
    }

    /**
     * [추가] 회원의 MBTI 정보를 MERGE (Upsert) 합니다.
     * (Service가 "새 답안지"를 DB에 저장하기 위해 사용)
     *
     * @param conn Service 계층에서 트랜잭션 관리를 위해 전달하는 Connection
     * @param memberId 정보를 수정할 회원의 ID
     * @param mbtiMap 저장할 MBTI 정보 (Key: mbti_id, Value: selected_option)
     * @throws SQLException DB 오류 발생 시 Service가 감지하고 rollback 할 수 있도록 예외를 던짐
     */
    public void upsertMemberMbti(Connection conn, Long memberId, Map<Long, String> mbtiMap) throws SQLException {

        // Oracle의 MERGE 구문 (Upsert: Update + Insert)
        // "데이터가 이미 있으면(MATCHED) UPDATE 하고, 없으면(NOT MATCHED) INSERT 해라"
        String sql = "MERGE INTO MemberMbti m " +
                "USING (SELECT ? AS m_id, ? AS b_id, ? AS sel_opt FROM dual) src " +
                "ON (m.member_id = src.m_id AND m.mbti_id = src.b_id) " +
                "WHEN MATCHED THEN " +
                "  UPDATE SET m.selected_option = src.sel_opt " +
                "WHEN NOT MATCHED THEN " +
                "  INSERT (member_id, mbti_id, selected_option) " +
                "  VALUES (src.m_id, src.b_id, src.sel_opt)";

        // try-with-resources: pstmt만 자동으로 닫힙니다.
        // (conn은 Service가 관리하므로 여기서 닫으면 절대 안 됩니다!)
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Service가 전달한 mbtiMap에 담긴 4개의 항목(E/I, S/N, T/F, J/P)을
            // 반복문으로 4번 실행합니다.
            for (Map.Entry<Long, String> entry : mbtiMap.entrySet()) {
                Long mbtiId = entry.getKey();
                String selectedOption = entry.getValue();

                // SQL의 '?' 3개에 값을 바인딩
                pstmt.setLong(1, memberId);       // src.m_id (회원 ID)
                pstmt.setLong(2, mbtiId);         // src.b_id (MBTI 차원 ID)
                pstmt.setString(3, selectedOption); // src.sel_opt (선택한 값, 'E' 등)

                // 4번의 쿼리가 모두 Service의 트랜잭션 안에서 실행됩니다.
                pstmt.executeUpdate();
            }
        }
        // Service가 conn.commit() 이나 conn.rollback()을 호출할 것이므로
        // 이 메서드는 여기서 끝납니다.
    }
}
