package src.post;

import src.utils.Azconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostRepository {
    public List<Post> findAllPostsByProjectId(Long projectId){
        List<Post> postList = new ArrayList<>();
        String sql = "SELECT * FROM post WHERE project_id =? ORDER BY modified_at DESC";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("content")
                    );
                    postList.add(post);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return postList;

    }

    public List<Post> findMyPostsByProjectIdAndMemberId(Long projectId, Long memberId) {
        List<Post> postList = new ArrayList<>();
        String sql = "SELECT * FROM post WHERE project_id=? AND member_id=? ORDER BY modified_at DESC";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);
            pstmt.setLong(2, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("content")
                    );
                    postList.add(post);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
        }
        return postList;
    }
}

