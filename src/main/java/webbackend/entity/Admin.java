package webbackend.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "AdminAccount")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private UUID admin_id;
    @Column(name = "adminName")
    private String adminName;
    @Column(name = "password")
    private String password;
    private LocalDateTime dateCreateAcc;
    private String role;
    private String status;
    @Column(name = "imageLinks")
    private String imageLinks;

    public Admin(UUID admin_id, String adminName, String password, LocalDateTime dateCreateAcc, String role, String status, String imageLinks) {
        this.admin_id = admin_id;
        this.adminName = adminName;
        this.password = password;
        this.dateCreateAcc = dateCreateAcc;
        this.role = role;
        this.status = status;
        this.imageLinks = imageLinks;
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


    public LocalDateTime getDateCreateAcc() {
        return dateCreateAcc;
    }

    public void setDateCreateAcc(LocalDateTime dateCreateAcc) {
        this.dateCreateAcc = dateCreateAcc;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
