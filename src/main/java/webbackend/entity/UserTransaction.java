package webbackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "UserTransaction")
public class UserTransaction {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "uniqueidentifier")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false, columnDefinition = "uniqueidentifier")
    private Game game;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false, columnDefinition = "uniqueidentifier")
    private Vouncher vouncher;

    private double amount;

    private String type;
    private String description;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    private String status;

    @Column(name = "status_detail")
    private String statucDetail;

    public UserTransaction() {
        this.transactionDate = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
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

    public String getStatucDetail() {
        return statucDetail;
    }

    public void setStatucDetail(String statucDetail) {
        this.statucDetail = statucDetail;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
