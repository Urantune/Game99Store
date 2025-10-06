/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Do Trong Tri - CE190397 - Sep 23, 2025
 */
// Đối tượng sản phẩm trong giỏ hàng
public class CartItem {

    private int userId;
    private int gameId;
    private String gameName;
    private double price;
    private int quantity;

    public CartItem(int userId, int gameId, String gameName, double price, int quantity) {
        this.userId = userId;
        this.gameId = gameId;
        this.gameName = gameName;
        this.price = price;
        this.quantity = quantity;
    }

    // Getter & Setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
