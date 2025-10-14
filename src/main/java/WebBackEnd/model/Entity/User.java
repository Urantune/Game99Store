package WebBackEnd.model.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "[User]")
public class User {

    @Id
    @Column(name = "user_Id")
    private String id;

    @Column(name = "userName")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "score")
    private int score;

    @Column(name = "status")
    private int status;

    @Column(name = "dateCreateAcc")
    private LocalDate dateCreateAccount;


    @Column(name = "imageLinks", nullable = true)
    private String imageLinks;




    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }




    public String getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }

    public LocalDate getDateCreateAccount() {
        return dateCreateAccount;
    }

    public void setDateCreateAccount(LocalDate dateCreateAccount) {
        this.dateCreateAccount = dateCreateAccount;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
