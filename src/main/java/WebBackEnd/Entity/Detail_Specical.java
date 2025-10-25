package WebBackEnd.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "detailSpecical")
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

    public Detail_Specical(int detailId, String detailType, String detailTital, String detailDeception) {
        this.detailId = detailId;
        this.detailType = detailType;
        this.detailTital = detailTital;
        this.detailDeception = detailDeception;
    }

    public Detail_Specical() {

    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getDetailTital() {
        return detailTital;
    }

    public void setDetailTital(String detailTital) {
        this.detailTital = detailTital;
    }

    public String getDetailDeception() {
        return detailDeception;
    }

    public void setDetailDeception(String detailDeception) {
        this.detailDeception = detailDeception;
    }
}


