package WebBackEnd.model.Entity;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "voucher")
public class Vouncher {

    @Id
    @GeneratedValue
    @Column(name = "voucherid")
    private UUID voucherId;
}
