package WebBackEnd.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserGame")
public class UserGame {

    @EmbeddedId
    private UserGameId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", columnDefinition = "uniqueidentifier")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gameId")
    @JoinColumn(name = "game_id", columnDefinition = "uniqueidentifier")
    private Game game;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "status")
    private int status;

    public UserGame() {}

    public UserGame(User user, Game game, LocalDateTime purchaseDate, int status) {
        this.id = new UserGameId(user.getId(), game.getGameId());
        this.user = user;
        this.game = game;
        this.purchaseDate = purchaseDate;
        this.status = status;
    }

    // 🧩 Getter & Setter
    public UserGameId getId() { return id; }
    public void setId(UserGameId id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getStatusText() {
        return switch (status) {
            case 0 -> "Đang xử lý";
            case 1 -> "Hoàn tất";
            case 2 -> "Hoàn tiền";
            default -> "Không xác định";
        };
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
