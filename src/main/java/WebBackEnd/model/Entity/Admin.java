package WebBackEnd.model.Entity;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "admims")
public class Admin {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private UUID admin_id;
    @Column(name = "adminName")
    private String adminName;
    @Column(name = "password")
    private String password;
    @Column(name = "imageLinks")
    private String imageLinks;

    public Admin(UUID admin_id, String adminName, String imageLinks, String password) {
        this.admin_id = admin_id;
        this.adminName = adminName;
        this.imageLinks = imageLinks;
        this.password = password;
    }
    public Admin() {

    }

    public UUID getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(UUID admin_id) {
        this.admin_id = admin_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }
}
