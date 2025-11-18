package src.member;

import src.member.dto.MemberInfoDto;
import src.utils.Azconnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


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

//    public Member findById(Long id) {
//        String sql = "SELECT * FROM member WHERE id = ?";
//        try (Connection conn = Azconnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setLong(1, id);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                // 결과가 있다면 (rs.next())
//                if (rs.next()) {
//                    // 찾은 정보로 Member 객체를 생성하여 반환
//                    return new Member(
//                            rs.getLong("id"),
//                            rs.getString("email"),
//                            rs.getString("password"),
//                            rs.getString("name"),
//                            rs.getObject("birth_date", LocalDate.class),
//                            rs.getObject("created_at", LocalDateTime.class)
//                    );
//                }
//            }
//        } catch (SQLException e) {
//            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
//        }
//
//        return null;
//    }

    public boolean save(Member member){
        String sql = "INSERT INTO member (email, password, name, birth_date, created_at) VALUES (?, ?, ?, ? ,?)";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getEmail());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.setObject(4, member.getBirthDate());
            pstmt.setObject(5, member.getCreatedAt());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows != 0;

        } catch (SQLException e) {
            System.err.println("DB 저장 중 오류 발생: " + e.getMessage());
        }
        return false;
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
                return true; // 1개 이상의 행이 변경되었으면 성공
            }

        } catch (SQLException e) {
            System.err.println("DB 업데이트 중 오류 발생: " + e.getMessage());
        }

        return false; // 업데이트 실패
    }

    public boolean delete(Long id){
        String sql = "DELETE FROM member WHERE id = ?";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return true;
            }

        } catch (SQLException e) {
            System.err.println("DB 업데이트 중 오류 발생: " + e.getMessage());
        }

        return false; // 업데이트 실패
    }

    public MemberInfoDto getAllInfoById(Long id){
        String sql = "SELECT\n" +
                "    m.id,\n" +
                "    m.name,\n" +
                "    m.email,\n" +
                "    m.birth_date,\n" +
                "    (\n" +
                "        SELECT\n" +
                "            LISTAGG(mm.selected_option, '') WITHIN GROUP (ORDER BY mm.mbti_id)\n" +
                "        FROM\n" +
                "            MemberMbti mm\n" +
                "        WHERE\n" +
                "            mm.member_id = m.id\n" +
                "    ) AS mbti,\n" +
                "    (\n" +
                "        SELECT\n" +
                "            LISTAGG(t.name, ', ') WITHIN GROUP (ORDER BY t.name)\n" +
                "        FROM\n" +
                "            MemberTechspec mt\n" +
                "        JOIN\n" +
                "            Techspec t ON mt.techspec_id = t.id\n" +
                "        WHERE\n" +
                "            mt.member_id = m.id\n" +
                "    ) AS tech_specs,\n" +
                "    (\n" +
                "        SELECT\n" +
                "            LISTAGG(p.title || ' (' || pa.role || ')', '; ') WITHIN GROUP (ORDER BY p.title)\n" +
                "        FROM\n" +
                "            Participant pa\n" +
                "        JOIN\n" +
                "            Project p ON pa.project_id = p.id\n" +
                "        WHERE\n" +
                "            pa.member_id = m.id\n" +
                "    ) AS projects_and_roles\n" +
                "FROM\n" +
                "    Member m\n" +
                "WHERE\n" +
                "    m.id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                // 결과가 있다면 (rs.next())
                if (rs.next()) {
                    // 찾은 정보로 Member 객체를 생성하여 반환
                    return new MemberInfoDto(
                            rs.getLong(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getObject(4, LocalDate.class),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7)
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }

        // 일치하는 사용자가 없으면 null을 반환
        return null;

    }
}
