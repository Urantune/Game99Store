package WebBackEnd.model.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Detail_Specical {

    @Id
    @Column(name = "detailId")
    private int detailId;
    @Column(name= "detailType")
    private String detailType;
    @Column(name = "detailTital")
    private String detailTital;
    @Column(name = "detailDeception")
    private String detailDeception;


}
