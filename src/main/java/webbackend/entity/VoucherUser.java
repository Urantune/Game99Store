package webbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "VoucherUser")
public class VoucherUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Vouncher vouncher;
    private LocalDateTime date_received;
    public VoucherUser() {
    }
    public VoucherUser(User user, Vouncher vouncher, LocalDateTime date_received) {
        this.user = user;
        this.vouncher = vouncher;
        this.date_received = date_received;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vouncher getVouncher() {
        return vouncher;
    }

    public void setVouncher(Vouncher vouncher) {
        this.vouncher = vouncher;
    }

    public LocalDateTime getDate_received() {
        return date_received;
    }

    public void setDate_received(LocalDateTime date_received) {
        this.date_received = date_received;
    }
}
