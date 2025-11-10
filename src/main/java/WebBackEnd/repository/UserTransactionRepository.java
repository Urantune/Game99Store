package WebBackEnd.repository;

import WebBackEnd.Entity.UserTransaction;
import WebBackEnd.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, UUID> {
    List<UserTransaction> findByUser(User user);
    List<UserTransaction> findByUserAndType(User user, String type);
}
