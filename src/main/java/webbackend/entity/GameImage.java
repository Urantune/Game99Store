package webbackend.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "GameImage")
public class GameImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uniqueidentifier",nullable = false)
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gameId", referencedColumnName = "id")
    private GameAccount gameAccount;
    @Column(name = "imageMain", nullable = false)
    private String imageMain;
    @Column(name = "imageVV", nullable = false)
    private String imageVV;
    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;

    public GameImage() {
    }

    public GameImage(UUID id, GameAccount gameAccount, String imageMain, String imageVV, String imageURL) {
        this.id = id;
        this.gameAccount = gameAccount;
        this.imageMain = imageMain;
        this.imageVV = imageVV;
        this.imageUrl = imageURL;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public GameAccount getGameAccount() {
        return gameAccount;
    }

    public void setGameAccount(GameAccount gameAccount) {
        this.gameAccount = gameAccount;
    }

    public String getImageMain() {
        return imageMain;
    }

    public void setImageMain(String imageMain) {
        this.imageMain = imageMain;
    }

    public String getImageVV() {
        return imageVV;
    }

    public void setImageVV(String imageVV) {
        this.imageVV = imageVV;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageURL) {
        this.imageUrl = imageURL;
    }
}
