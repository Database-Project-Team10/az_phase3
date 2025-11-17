package src.mbti;

import src.utils.Azconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MbtiRepository {

    public List<MbtiDimension> findAllMbtiDimensions() {
        List<MbtiDimension> dimensions = new ArrayList<>();
        String sql = "SELECT id, dimension_type, option1, option2 FROM MBTI ORDER BY id";

        try (Connection conn = Azconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                dimensions.add(new MbtiDimension(
                        rs.getLong("id"),
                        rs.getString("dimension_type"),
                        rs.getString("option1"),
                        rs.getString("option2")
                ));
            }
        } catch (SQLException e) {
            System.err.println("MBTI 차원 조회 중 오류: " + e.getMessage());
            return null;
        }
        return dimensions;
    }
}
