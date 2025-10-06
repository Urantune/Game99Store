/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author Do Trong Tri - CE190397 - Aug 11, 2025
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext implements Serializable {

    private static final String DB_NAME = "UserDB";
    private static final String DB_USER_NAME = "sa";
    private static final String DB_PASSWORD = "123456";

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        // String url = "jdbc:sqlserver://localhost:1433;databaseName=" + DB_NAME;
        String url = "jdbc:sqlserver://localhost:1433;databaseName=" + DB_NAME + ";encrypt=true;trustServerCertificate=true";
        conn = DriverManager.getConnection(url, DB_USER_NAME, DB_PASSWORD);
        return conn;
    }

    // run file ktra đã connect đc sql chưa nếu không bị lỗi
    public static void main(String[] args) {
        try ( Connection conn = DBContext.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Kết nối SQL Server thành công!");
            } else {
                System.out.println("❌ Kết nối thất bại!");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }
}
