package webbackend.repository;

import webbackend.entity.UserTransaction;
import webbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, UUID> {
    List<UserTransaction> findByUser(User user);
    List<UserTransaction> findByUserAndType(User user, String type);
}
