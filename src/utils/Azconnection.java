package src.utils;

import java.sql.*;

public class Azconnection {

    public static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    public static final String USER_NAME = "az";
    public static final String USER_PASSWORD = "dtob";

    public Azconnection() {}

    // 드라이버 로드
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load Oracle driver: " + e.getMessage());
            System.exit(1);
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER_NAME, USER_PASSWORD);
            return conn;
        } catch (SQLException ex) {
            System.err.println("Database connection or operation failed: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

}
