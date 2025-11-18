package src.link;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import src.utils.Azconnection;

public class LinkRepository {

    
     //(C) Create: 새로운 Link 레코드를 DB에 저장합니다.
     
    public boolean save(Link link) {
        String sql = "INSERT INTO Link (project_id, title, url) VALUES (?, ?, ?)";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, link.getProjectId());
            pstmt.setString(2, link.getTitle());
            pstmt.setString(3, link.getUrl());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("DB 저장 중 오류 발생 (Link): " + e.getMessage());
        }
        return false;
    }

    
     // (R) Read: 특정 프로젝트 ID에 속한 모든 링크를 조회합니다.
    public List<Link> findByProjectId(Long projectId) {
        List<Link> linkList = new ArrayList<>();
        String sql = "SELECT id, project_id, title, url FROM Link WHERE project_id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Link link = new Link(
                            rs.getLong("id"),
                            rs.getLong("project_id"),
                            rs.getString("title"),
                            rs.getString("url")
                    );
                    linkList.add(link);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생 (Link): " + e.getMessage());
        }
        return linkList;
    }
    

     // (R) Read: 단일 Link ID로 특정 링크를 조회합니다.
    public Link findById(Long linkId) {
        String sql = "SELECT id, project_id, title, url FROM Link WHERE id = ?";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, linkId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Link(
                            rs.getLong("id"),
                            rs.getLong("project_id"),
                            rs.getString("title"),
                            rs.getString("url")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 단일 조회 중 오류 발생 (Link): " + e.getMessage());
        }
        return null;
    }


     // (U) Update: 특정 Link의 제목과 URL을 수정합니다.
    public boolean update(Link link) {
        String sql = "UPDATE Link SET title = ?, url = ? WHERE id = ?";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, link.getTitle());
            pstmt.setString(2, link.getUrl());
            pstmt.setLong(3, link.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("DB 수정 중 오류 발생 (Link): " + e.getMessage());
        }
        return false;
    }

     // (D) Delete: 특정 Link ID를 DB에서 삭제합니다.
    public boolean delete(Long linkId) {
        String sql = "DELETE FROM Link WHERE id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, linkId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("DB 삭제 중 오류 발생 (Link): " + e.getMessage());
        }
        return false;
    }
}