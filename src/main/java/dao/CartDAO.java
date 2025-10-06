package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.CartItem;

public class CartDAO {

    private static final String DB_NAME = "UserDB";
    private static final String DB_USER_NAME = "sa";
    private static final String DB_PASSWORD = "123456";

    // Kết nối DB
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = "jdbc:sqlserver://localhost:1433;databaseName=" + DB_NAME + ";encrypt=true;trustServerCertificate=true";
        return DriverManager.getConnection(url, DB_USER_NAME, DB_PASSWORD);
    }

    // 🟢 Thêm game vào giỏ hàng
    public void addToCart(int userId, int gameId, int quantity)
            throws SQLException, ClassNotFoundException {

        String sql = "IF EXISTS (SELECT 1 FROM Cart WHERE userId = ? AND gameId = ?) "
                + "UPDATE Cart SET quantity = quantity + ? WHERE userId = ? AND gameId = ? "
                + "ELSE "
                + "INSERT INTO Cart(userId, gameId, quantity) VALUES (?, ?, ?)";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            // IF EXISTS
            ps.setInt(1, userId);
            ps.setInt(2, gameId);

            // UPDATE
            ps.setInt(3, quantity);
            ps.setInt(4, userId);
            ps.setInt(5, gameId);

            // INSERT
            ps.setInt(6, userId);
            ps.setInt(7, gameId);
            ps.setInt(8, quantity);

            ps.executeUpdate();
        }
    }

    // 🟠 Xoá game khỏi giỏ hàng
    public void removeFromCart(int userId, int gameId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Cart WHERE userId = ? AND gameId = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, gameId);
            ps.executeUpdate();
        }
    }

    // 🟣 Lấy giỏ hàng theo userId
    public List<CartItem> getCartByUserId(int userId) throws SQLException, ClassNotFoundException {
        List<CartItem> cart = new ArrayList<>();
        String sql = "SELECT c.userId, c.gameId, g.gameName AS gameName, g.price, c.quantity "
                + "FROM Cart c JOIN Game g ON c.gameId = g.gameId "
                + "WHERE c.userId = ?";  // ✅ thêm WHERE

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cart.add(new CartItem(
                        rs.getInt("userId"),
                        rs.getInt("gameId"),
                        rs.getString("gameName"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
        }
        return cart;
    }

    // 🔵 Xoá toàn bộ giỏ hàng
    public void clearCart(int userId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Cart WHERE userId = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // 🔴 Kiểm tra game đã có trong giỏ chưa
    public boolean existsInCart(int userId, int gameId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM Cart WHERE userId = ? AND gameId = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, gameId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
