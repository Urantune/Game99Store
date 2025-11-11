package WebBackEnd.service;

import WebBackEnd.Entity.User;
import WebBackEnd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SweepService {

    @Autowired
    private UserRepository userRepository;

    @Scheduled(fixedDelay = 60_000)
    public void sweep() {
        Instant now = Instant.now();


        List<User> users = userRepository.findAll();


        //tam de day nua lam sau


//        for (User u : users) {
//            String status = u.getStatus();
//            if (status == null || !status.contains("@")) continue;
//
//            int idx = status.indexOf('@');
//            if (idx < 0) continue;
//
//            String tag = status.substring(0, idx);
//            String timeStr = status.substring(idx + 1);
//
//            Instant until;
//            try {
//                until = Instant.parse(timeStr);
//            } catch (Exception e) {
//                continue;
//            }
//
//
//            if (now.isAfter(until)) {
//                if (tag.equals("WAIT_VERIFY")) {
//
//                    // userRepo.delete(u);
//                }
//                else if (tag.equals("WAIT_CHANGE_PASS")) {
//
//                    // u.setStatus("ACTIVATE");
//                    // userRepo.save(u);
//                }
//            }
//        }
    }
}
