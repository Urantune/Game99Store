package WebBackEnd.repository;

import WebBackEnd.model.Entity.Game;
import WebBackEnd.model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {
    public User findByUsername(String username);

    User findUserById(UUID id);


}
