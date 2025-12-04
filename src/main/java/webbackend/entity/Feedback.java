    package webbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Feeback")
public class Feedback {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uniqueidentifier",nullable = false)
    private UUID id;

    @Column(name = "game_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID gameId;

    @Column(name = "userId", nullable = false, columnDefinition = "uniqueidentifier")
    @JoinColumn(name = "Users",referencedColumnName = "id")
    private UUID userId;

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "star")
    private Double star;

    @Column(name = "date_created")
    private LocalDateTime dateCreated = LocalDateTime.now();



    public Feedback() { }

    public Feedback(UUID gameId, UUID userId, String comment, Double star) {
        this.gameId = gameId;
        this.userId = userId;
        this.comment = comment;
        this.star = star;
        this.dateCreated = LocalDateTime.now();
    }



    public UUID getId() { return id; }
    public void setId(UUID feedbackId) { this.id = feedbackId; }

    public UUID getGameId() { return gameId; }
    public void setGameId(UUID gameId) { this.gameId = gameId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Double getStar() { return star; }
    public void setStar(Double star) { this.star = star; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }




}


