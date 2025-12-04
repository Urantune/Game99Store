package webbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="UserGameArchive")
public class UserGameArchive {

    @Id
    @GeneratedValue
    private UUID id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Admin staff;
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    @Column(name = "expire_date")
    private LocalDate expireDate;
    @Column(name = "original_price")
    private double originalPrice;
    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Vouncher vouncher;
    private String status;

    public UserGameArchive() {

    }

    public UserGameArchive(Users user, Game game, Admin staff, LocalDate purchaseDate, LocalDate expireDate, double originalPrice, Vouncher vouncher, String status) {
        this.user = user;
        this.game = game;
        this.staff = staff;
        this.purchaseDate = purchaseDate;
        this.expireDate = expireDate;
        this.originalPrice = originalPrice;
        this.vouncher = vouncher;
        this.status = status;

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Admin getStaff() {
        return staff;
    }

    public void setStaff(Admin staff) {
        this.staff = staff;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Vouncher getVouncher() {
        return vouncher;
    }

    public void setVouncher(Vouncher vouncher) {
        this.vouncher = vouncher;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
