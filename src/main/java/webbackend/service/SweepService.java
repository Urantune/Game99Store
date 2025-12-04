package webbackend.service;

import webbackend.entity.Users;
import webbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SweepService {

    @Autowired
    private UserRepository userRepository;

    @Scheduled(fixedDelay = 120_000)
    @Transactional
    public void sweep() {
        LocalDateTime now = LocalDateTime.now();

        List<Users> waitUsers = userRepository.findByStatusContaining("wait");
        for (Users u : waitUsers) {
            if (u.getExpirationDate() != null &&
                    (u.getExpirationDate().isBefore(now) || u.getExpirationDate().isEqual(now))) {
                userRepository.deleteById(u.getId());
            }
        }

        List<Users> changeUsers = userRepository.findByStatusContaining("changePass");
        for (Users u : changeUsers) {
            if (u.getExpirationDate() != null &&
                    (u.getExpirationDate().isBefore(now) || u.getExpirationDate().isEqual(now))) {
                u.setStatus("active");
                u.setExpirationDate(null);
                userRepository.save(u);
            }
        }

        List<Users> deletingUsers = userRepository.findByStatusContaining("deleting");
        for (Users u : deletingUsers) {
            if (u.getExpirationDate() != null &&
                    (u.getExpirationDate().isBefore(now) || u.getExpirationDate().isEqual(now))) {
                u.setStatus("active");
                u.setExpirationDate(null);
                userRepository.save(u);
            }
        }
    }
}
