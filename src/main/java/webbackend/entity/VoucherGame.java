package webbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name ="VoucherGame")
public class VoucherGame {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Vouncher vouncher;
    @Column(name = "date_received")
    private LocalDateTime dateApplied;
    public VoucherGame() {
    }
    public VoucherGame(Game game, Vouncher vouncher, LocalDateTime dateApplied) {
        this.game = game;
        this.vouncher = vouncher;
        this.dateApplied = dateApplied;
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

    public Vouncher getVouncher() {
        return vouncher;
    }

    public void setVouncher(Vouncher vouncher) {
        this.vouncher = vouncher;
    }

    public LocalDateTime getDateApplied() {
        return dateApplied;
    }

    public void setDateApplied(LocalDateTime dateApplied) {
        this.dateApplied = dateApplied;
    }
}
