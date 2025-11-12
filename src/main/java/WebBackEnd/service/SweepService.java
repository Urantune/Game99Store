package WebBackEnd.service;

import WebBackEnd.Entity.User;
import WebBackEnd.repository.UserRepository;
import jakarta.transaction.Transactional; // hoặc: org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SweepService {

    @Autowired
    private UserRepository userRepository;


    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void sweep() {
        LocalDateTime now = LocalDateTime.now();


        List<User> waitUsers = userRepository.findByStatusContaining("wait");
        for (User u : waitUsers) {
            LocalDateTime end = parseEndFromStatus(u.getStatus(), now);
            if (end != null && (end.isBefore(now) || end.isEqual(now))) {
                userRepository.deleteById(u.getId());
            }
        }


        List<User> changeUsers = userRepository.findByStatusContaining("changePass");
        for (User u : changeUsers) {
            LocalDateTime end = parseEndFromStatus(u.getStatus(), now);
            if (end != null && (end.isBefore(now) || end.isEqual(now))) {
                u.setStatus("active");
                userRepository.save(u);
            }
        }
    }

      private LocalDateTime parseEndFromStatus(String status, LocalDateTime baseNow) {
        if (status == null) return null;
        String[] arr = status.split("\\|\\|");
        if (arr.length < 5) return null;
        try {
            int day    = Integer.parseInt(arr[1]);
            int hour   = Integer.parseInt(arr[2]);
            int minute = Integer.parseInt(arr[3]);
            int second = Integer.parseInt(arr[4]);
            return LocalDateTime.of(baseNow.getYear(), baseNow.getMonthValue(), day, hour, minute, second);
        } catch (NumberFormatException | DateTimeException e) {

            return null;
        }
    }
}
