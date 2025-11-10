package WebBackEnd.Service;

import WebBackEnd.Entity.User;
import WebBackEnd.Entity.UserTransaction;
import WebBackEnd.repository.UserTransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserTransactionService {
    private final UserTransactionRepository repo;

    public UserTransactionService(UserTransactionRepository repo) {
        this.repo = repo;
    }

    public List<UserTransaction> getTransactionsByUser(User user) {
        return repo.findByUser(user);
    }

    public List<UserTransaction> getTopups(User user) {
        return repo.findByUserAndType(user, "TOPUP");
    }

    public List<UserTransaction> getRefunds(User user) {
        return repo.findByUserAndType(user, "REFUND");
    }
}
