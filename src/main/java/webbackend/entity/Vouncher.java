package webbackend.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "voucher")
public class Vouncher {

    @Id
    @GeneratedValue
    @Column(name = "voucherid")
    private UUID voucherid;
    @Column(name = "name")
    private String name;
    @Column(name = "sale")
    private double sale;
    @Column(name = "type")
    private String type;
    @Column(name = "date_start")
    private LocalDateTime date_start;
    @Column(name = "date_end")
    private LocalDateTime date_end;



    public Vouncher(String name, double sale, String type, LocalDateTime date_start, LocalDateTime date_end) {
        this.name = name;
        this.sale = sale;
        this.type = type;
        this.date_start = date_start;
        this.date_end = date_end;
    }

    public Vouncher() {

    }

    public UUID getVoucherid() {
        return voucherid;
    }

    public void setVoucherId(UUID voucherId) {
        this.voucherid = voucherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSale() {
        return sale;
    }

    public void setSale(double sale) {
        this.sale = sale;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }



    public LocalDateTime getDate_start() {
        return date_start;
    }

    public void setDate_start(LocalDateTime date_start) {
        this.date_start = date_start;
    }

    public LocalDateTime getDate_end() {
        return date_end;
    }

    public void setDate_end(LocalDateTime date_end) {
        this.date_end = date_end;
    }

    @Transient
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return (date_start == null || !now.isBefore(date_start))
                && (date_end == null || !now.isAfter(date_end));
    }


}
