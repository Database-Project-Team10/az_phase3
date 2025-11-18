package src.reply;

import src.reply.dto.ReplyResponseDto;
import src.utils.Azconnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReplyRepository {

    public List<ReplyResponseDto> findAllByPostId(Long postId){
        List<ReplyResponseDto> replyResponseList = new ArrayList<>();
        String sql = "SELECT r.id, r.content, m.name FROM Reply r JOIN Member m ON r.member_id = m.id WHERE r.post_id = ? ORDER BY r.modified_at DESC";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReplyResponseDto reply = new ReplyResponseDto(
                            rs.getLong(1),
                            rs.getString(2),
                            rs.getString(3)
                    );
                    replyResponseList.add(reply);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return replyResponseList;
    }

    public List<Reply> findMyReplyByPostIdAndMemberId(Long postId, Long memberId){
        List<Reply> replyList = new ArrayList<>();
        String sql = "select * from reply where post_id = ? AND member_id = ? ORDER BY modified_at DESC";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, postId);
            pstmt.setLong(2, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reply reply = new Reply(
                            rs.getLong("id"),
                            rs.getLong("post_id"),
                            rs.getLong("member_id"),
                            rs.getString("content"),
                            rs.getObject("created_at", LocalDateTime.class),
                            rs.getObject("modified_at", LocalDateTime.class)
                    );
                    replyList.add(reply);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return replyList;
    }

    public Reply findById(Long replyId){
        String sql = "SELECT * FROM reply WHERE id=?";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, replyId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Reply(
                            rs.getLong("id"),
                            rs.getLong("post_id"),
                            rs.getLong("member_id"),
                            rs.getString("content"),
                            rs.getObject("created_at", LocalDateTime.class),
                            rs.getObject("modified_at", LocalDateTime.class)
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return null;
    }

    public Reply save(Reply reply) {
        String sql = "INSERT INTO reply (post_id, member_id, content, created_at, modified_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        String[] generatedColumns = {"id"};

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, generatedColumns)) {

            pstmt.setLong(1, reply.getPostId());
            pstmt.setLong(2, reply.getMemberId());
            pstmt.setString(3, reply.getContent());

            pstmt.setTimestamp(4, Timestamp.valueOf(reply.getCreatedAt()));
            pstmt.setTimestamp(5, Timestamp.valueOf(reply.getModifiedAt()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("댓글 저장 실패: 영향받은 행이 없습니다.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);

                    return new Reply(
                            id,
                            reply.getPostId(),
                            reply.getMemberId(),
                            reply.getContent(),
                            reply.getCreatedAt(),
                            reply.getModifiedAt()
                    );
                } else {
                    throw new SQLException("댓글 저장 실패: ID를 가져올 수 없습니다.");
                }
            }

        } catch (SQLException e) {
            System.err.println("DB 저장 중 오류 발생: " + e.getMessage());
            return null;
        }
    }

    public boolean update(Reply reply){
        String sql = "UPDATE reply SET content=?, modified_at=? WHERE id=?";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reply.getContent());
            pstmt.setObject(2, reply.getModifiedAt());
            pstmt.setLong(3, reply.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows != 0;

        } catch (SQLException e) {
            System.err.println("DB 저장 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(Long replyId) {
        String sql = "DELETE FROM reply WHERE id=?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, replyId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("[Debug] Repository: " + replyId + "삭제됨.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("DB 업데이트 중 오류 발생: " + e.getMessage());
        }

        return false;
    }

}
