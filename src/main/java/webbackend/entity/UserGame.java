package webbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "UserGame")
public class UserGame {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "uniqueidentifier")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", columnDefinition = "uniqueidentifier")
    private Game game;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "status")
    private String status;

    @Column(name = "purchase_price", nullable = false)
    private double purchasePrice;

    public UserGame() {}

    public UserGame(User user, Game game, LocalDateTime purchaseDate, String status, double purchasePrice) {
        this.user = user;
        this.game = game;
        this.purchaseDate = purchaseDate;
        this.status = status;
        this.purchasePrice = purchasePrice;
    }



    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    @Override
    public String toString() {
        return "UserGame{" +
                "user=" + (user != null ? user.getId() : "null") +
                ", game=" + (game != null ? game.getGameId() : "null") +
                ", purchaseDate=" + purchaseDate +
                ", status=" + status +
                '}';
    }
}
