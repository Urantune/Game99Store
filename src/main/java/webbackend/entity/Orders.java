package webbackend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id",columnDefinition = "uniqueidentifier",nullable = false)
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private Users user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voucherId", referencedColumnName = "id")
    private Voucher voucher;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staffId",referencedColumnName = "id")
    private Administrator staff;
    @Column(name = "totalPrice")
    private BigDecimal totalPrice;
    @Column(name = "quantity" )
    private int quantity;
    @Column(name = "createdDate")
    private LocalDateTime createdDate;
    @Column(name = "status")
    private String status;


    public Orders() {
    }

    public Orders(UUID id, Users user, Voucher voucher, Administrator staff, BigDecimal totalPrice, int quantity, LocalDateTime createdDate, String status) {
        this.id = id;
        this.user = user;
        this.voucher = voucher;
        this.staff = staff;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.createdDate = createdDate;
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

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public Administrator getStaff() {
        return staff;
    }

    public void setStaff(Administrator staff) {
        this.staff = staff;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
