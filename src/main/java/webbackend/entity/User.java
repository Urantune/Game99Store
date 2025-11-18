        package webbackend.entity;

        import jakarta.persistence.*;

        import java.time.LocalDateTime;
        import java.util.UUID;

        @Entity
        @Table(name = "users")
        public class User {

            @Id
            @GeneratedValue
            @Column(name = "user_Id")
            private UUID id;

            @Column(name = "userName")
            private String username;

            @Column(name = "password")
            private String password;

            @Column(name = "email")
            private String email;


            @Column(name = "status")
            private String status;

            @Column(name = "dateCreateAcc")
            private LocalDateTime dateCreateAccount;

            @Column(name = "imageLinks", nullable = true)
            private String imageLinks;

            @Column(name = "balance")
            private double balance;

            private LocalDateTime expirationDate;




            public User() {
            }

            public User(UUID id, String username, String password, String email,  String status, LocalDateTime dateCreateAccount, String imageLinks, double balance) {
                this.id = id;
                this.username = username;
                this.password = password;
                this.email = email;

                this.status = status;
                this.dateCreateAccount = dateCreateAccount;
                this.imageLinks = imageLinks;
                this.balance = balance;
            }


            public UUID getId() {
                return id;
            }

            public void setId(UUID id) {
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




            public double getPrice() {
                return balance;
            }

            public void setPrice(double price) {
                this.balance = price;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public LocalDateTime getDateCreateAccount() {
                return dateCreateAccount;
            }



            public void setDateCreateAccount(LocalDateTime dateCreateAccount) {
                this.dateCreateAccount = dateCreateAccount;
            }

            public String getImageLinks() {
                return imageLinks;
            }

            public void setImageLinks(String imageLinks) {
                this.imageLinks = imageLinks;
            }

            public double getBalance() {
                return balance;
            }

            public void setBalance(double balance) {
                this.balance = balance;
            }

            public LocalDateTime getExpirationDate() {
                return expirationDate;
            }

            public void setExpirationDate(LocalDateTime expirationDate) {
                this.expirationDate = expirationDate;
            }
        }