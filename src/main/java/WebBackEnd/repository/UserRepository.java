package WebBackEnd.repository;

import WebBackEnd.model.Entity.Game;
import WebBackEnd.model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String> {
    public User findByUsername(String username);




}
