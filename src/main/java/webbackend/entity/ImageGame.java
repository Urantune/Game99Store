package webbackend.entity;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "ImageGame")
public class ImageGame {

    @Id
    @GeneratedValue
    private UUID id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id", nullable = false, columnDefinition = "uniqueidentifier")
    private Game game;
    private String mainImage;
    private String video;
    private String imageOne;
    private String imageTwo;
    private String imageThree;
    public ImageGame() {
    }
    public ImageGame(Game game, String mainImage, String video, String imageOne, String imageTwo, String imageThree) {
        this.game = game;
        this.mainImage = mainImage;
        this.video = video;
        this.imageOne = imageOne;
        this.imageTwo = imageTwo;
        this.imageThree = imageThree;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImageOne() {
        return imageOne;
    }

    public void setImageOne(String imageOne) {
        this.imageOne = imageOne;
    }

    public String getImageTwo() {
        return imageTwo;
    }

    public void setImageTwo(String imageTwo) {
        this.imageTwo = imageTwo;
    }

    public String getImageThree() {
        return imageThree;
    }

    public void setImageThree(String imageThree) {
        this.imageThree = imageThree;
    }
}
