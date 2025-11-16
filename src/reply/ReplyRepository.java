package src.reply;

import src.post.Post;
import src.utils.Azconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReplyRepository {

    public List<Reply> findAllByPostId(Long postId){
        List<Reply> replyList = new ArrayList<>();
        String sql = "select * from reply where post_id = ? ORDER BY modified_at DESC";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reply reply = new Reply(
                            rs.getLong("id"),
                            rs.getLong("member_id"),
                            rs.getString("content"),
                            rs.getObject("created_at", LocalDateTime.class)
                    );
                    replyList.add(reply);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return replyList;
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
                            rs.getLong("member_id"),
                            rs.getString("content"),
                            rs.getObject("created_at", LocalDateTime.class)
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

    public boolean save(Reply reply){
        String sql = "INSERT INTO reply (post_id, member_id, content, created_at, modified_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, reply.getPostId());
            pstmt.setLong(2, reply.getMemberId());
            pstmt.setString(3, reply.getContent());
            pstmt.setObject(4, reply.getCreatedAt());
            pstmt.setObject(5, reply.getModifiedAt());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows != 0;

        } catch (SQLException e) {
            System.err.println("DB 저장 중 오류 발생: " + e.getMessage());
        }
        return false;
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
