package src.document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import src.utils.Azconnection;

public class DocumentRepository {


    public boolean save(Document document) {
        String sql = "INSERT INTO Document (project_id, title, location) VALUES (?, ?, ?)";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, document.getProjectId());
            pstmt.setString(2, document.getTitle());
            pstmt.setString(3, document.getLocation());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("DB 저장 중 오류 발생 (Document): " + e.getMessage());
        }
        return false;
    }


    public List<Document> findByProjectId(Long projectId) {
        List<Document> documentList = new ArrayList<>();
        String sql = "SELECT id, project_id, title, location FROM Document WHERE project_id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Document document = new Document(
                            rs.getLong("id"),
                            rs.getLong("project_id"),
                            rs.getString("title"),
                            rs.getString("location") // [!] location으로 변경
                    );
                    documentList.add(document);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 조회 중 오류 발생 (Document): " + e.getMessage());
        }
        return documentList;
    }
    
    public Document findById(Long documentId) {
        String sql = "SELECT id, project_id, title, location FROM Document WHERE id = ?";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, documentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Document(
                            rs.getLong("id"),
                            rs.getLong("project_id"),
                            rs.getString("title"),
                            rs.getString("location")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 단일 조회 중 오류 발생 (Document): " + e.getMessage());
        }
        return null;
    }

    public boolean update(Document document) {
        String sql = "UPDATE Document SET title = ?, location = ? WHERE id = ?";
        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getLocation()); // [!] location으로 변경
            pstmt.setLong(3, document.getId()); 

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("DB 수정 중 오류 발생 (Document): " + e.getMessage());
        }
        return false;
    }

    public boolean delete(Long documentId) {
        String sql = "DELETE FROM Document WHERE id = ?";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, documentId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("DB 삭제 중 오류 발생 (Document): " + e.getMessage());
        }
        return false;
    }
}