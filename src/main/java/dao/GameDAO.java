/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import util.DBContext;
import java.sql.*;
import model.Game;

public class GameDAO {

    public Game getGameById(int id) {
        try ( Connection conn = new DBContext().getConnection()) {
            String sql = "SELECT gameId, name, price, releaseDate FROM Game WHERE gameId = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Game(
                        rs.getInt("gameId"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDate("releaseDate").toString() // 👈 nếu bạn cần String
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
