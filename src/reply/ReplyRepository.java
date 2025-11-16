package src.reply;

import src.utils.Azconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                            rs.getString("content")
                    );
                    replyList.add(reply);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return replyList;

    }

}
