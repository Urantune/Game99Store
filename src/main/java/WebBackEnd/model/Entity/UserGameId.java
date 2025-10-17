package WebBackEnd.model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserGameId implements Serializable {

    @Column(name="user_id", columnDefinition="uniqueidentifier")
    private UUID userId;

    @Column(name="game_id", columnDefinition="uniqueidentifier")
    private UUID gameId;

    public UserGameId() {}
    public UserGameId(UUID userId, UUID gameId) {
        this.userId = userId; this.gameId = gameId;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getGameId() { return gameId; }
    public void setGameId(UUID gameId) { this.gameId = gameId; }


    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGameId that)) return false;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(gameId, that.gameId);
    }
    @Override public int hashCode() {
        return Objects.hash(userId, gameId);
    }

}