package WebBackEnd.model.Entity;

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

    public UserGame(User user, Game game, LocalDateTime purchaseDate) {
        this.id = new UserGameId(user.getId(), game.getGameId());
        this.user = user;
        this.game = game;
        this.purchaseDate = purchaseDate;

    }

// Nho them cai me gi quen roi
}
