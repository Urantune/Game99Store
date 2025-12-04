package webbackend.service;

import webbackend.entity.Users;
import webbackend.entity.UserTransaction;
import webbackend.repository.UserTransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserTransactionService {
    private final UserTransactionRepository repo;

    public UserTransactionService(UserTransactionRepository repo) {
        this.repo = repo;
    }

    public List<UserTransaction> getTransactionsByUser(Users user) {
        return repo.findByUser(user);
    }

    public List<UserTransaction> getTopups(Users user) {
        return repo.findByUserAndType(user, "TOPUP");
    }

    public List<UserTransaction> getRefunds(Users user) {
        return repo.findByUserAndType(user, "REFUND");
    }

    public void save(UserTransaction userTransaction) {
        repo.save(userTransaction);
    }
}
