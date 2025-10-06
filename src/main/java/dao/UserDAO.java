/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.User;
import util.DBContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    // Kiểm tra username đã tồn tại chưa
    public boolean isUsernameExists(String username) {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "SELECT userId FROM Users WHERE userName=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Kiểm tra email đã tồn tại chưa
    public boolean isEmailExists(String email) {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "SELECT userId FROM Users WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Đăng ký user mới
    public boolean register(String username, String password, String email) {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "INSERT INTO Users(userName, password, email, role) VALUES(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, "user");
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Đăng nhập
    public User login(String username, String password) {
        try ( Connection conn = new DBContext().getConnection()) {
            // chỉ lấy user theo username, không so sánh password ở SQL
            String sql = "SELECT userId, userName, password, email, role FROM Users WHERE userName=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                // kiểm tra password nhập vào với hashed password
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return new User(
                            rs.getInt("userId"),
                            rs.getString("userName"),
                            hashedPassword,
                            rs.getString("email"),
                            rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // trả về User theo email (nếu bạn đã có, dùng lại)
    public User findByEmail(String email) {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "SELECT userId, userName, password, role, email FROM Users WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("userId"), rs.getString("userName"),
                        rs.getString("password"), rs.getString("role"), rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
// Lưu tokenHash
    public void savePasswordResetToken(int userId, String tokenHash, long expireAt) throws Exception {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "INSERT INTO PasswordReset(userId, tokenHash, expireAt, createdAt) VALUES(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, tokenHash);
            ps.setLong(3, expireAt);
            ps.setLong(4, System.currentTimeMillis());
            ps.executeUpdate();
        }
    }
// Tìm userId theo tokenHash hợp lệ (chưa hết hạn)
    public Integer getUserIdByTokenHash(String tokenHash, long now) {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "SELECT TOP 1 userId FROM PasswordReset WHERE tokenHash = ? AND expireAt > ? ORDER BY createdAt DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tokenHash);
            ps.setLong(2, now);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("userId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
// Xóa token (sau khi dùng)
    public void deleteResetToken(String tokenHash) {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "DELETE FROM PasswordReset WHERE tokenHash = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tokenHash);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// Update password by userId (lưu hashed)
    public boolean updatePassword(int userId, String hashedPassword) {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "UPDATE Users SET password = ? WHERE userId = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public User findByUsername(String username) {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "SELECT userId, userName, password, email, role FROM Users WHERE userName = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("password"), // hashed password
                        rs.getString("email"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // Tìm user theo cả username và email
    public User findByUsernameAndEmail(String username, String email) {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "SELECT userId, userName, password, email, role FROM Users WHERE userName = ? AND email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean isGameRegistered(int userId, int gameId) throws ClassNotFoundException {
        String sql = "SELECT 1 FROM GameRegistration WHERE userId = ? AND gameId = ?";
        try ( Connection con = DBContext.getConnection();  PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, gameId);
            try ( ResultSet rs = ps.executeQuery()) {
                return rs.next(); // nếu có bản ghi -> đã đăng ký
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean registerGame(int userId, int gameId) throws ClassNotFoundException {
        String sql = "INSERT INTO GameRegistration(userId, gameId) VALUES(?, ?)";
        try ( Connection con = DBContext.getConnection();  PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, gameId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<Integer> getRegisteredGames(int userId) throws ClassNotFoundException {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT gameId FROM GameRegistration WHERE userId = ?";
        try ( Connection con = DBContext.getConnection();  PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("gameId"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // trong dao.UserDAO
    public boolean unregisterGame(int userId, int gameId) throws ClassNotFoundException {
        String sql = "DELETE FROM GameRegistration WHERE userId = ? AND gameId = ?";
        try ( Connection con = DBContext.getConnection();  PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, gameId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
