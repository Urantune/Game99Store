package webbackend.repository;

import webbackend.entity.UserTransaction;
import webbackend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, UUID> {
    List<UserTransaction> findByUser(Users user);
    List<UserTransaction> findByUserAndType(Users user, String type);

    List<UserTransaction> findAllByOrderByTransactionDateDesc();

}
