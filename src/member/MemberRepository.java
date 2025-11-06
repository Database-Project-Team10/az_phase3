package src.member;

import src.utils.Azconnection;

import java.sql.*;

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
                    String foundEmail = rs.getString("email");
                    String foundPassword = rs.getString("password");
                    // 찾은 정보로 Member 객체를 생성하여 반환
                    return new Member(foundEmail, foundPassword);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }

        // 일치하는 사용자가 없으면 null을 반환
        return null;
    }

    public void save(Member member) {
        // [SQL] members 테이블에 username과 password를 삽입합니다.
        String sql = "INSERT INTO member (email, password, name, birth_date, created_at) VALUES (?, ?, ?, ? ,?)";

        // try-with-resources: conn과 pstmt가 자동으로 close() 됩니다.
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // SQL의 '?'에 값을 바인딩합니다. (SQL Injection 방지)
            pstmt.setString(1, member.getEmail());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.setDate(4, Date.valueOf(member.getBirthDate()));
            pstmt.setDate(5, Date.valueOf(member.getCreatedAt()));


            // 쿼리 실행
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("[Debug] Repository: DB에 " + member.getEmail() + " 저장됨.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated
                System.err.println("저장 실패: 아이디 중복 (DB)");
            } else {
                System.err.println("DB 저장 중 오류 발생: " + e.getMessage());
            }
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
}
