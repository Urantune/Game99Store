package WebBackEnd.Controller;

import WebBackEnd.Entity.*;
import WebBackEnd.SucDat.GameCore;
import WebBackEnd.SucDat.SendMailTest;
import WebBackEnd.repository.UserGameRepository;
import WebBackEnd.repository.UserRepository;
import WebBackEnd.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping(value = "/welcome")
public class ChangePasswordController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GameSevice gameSevice;
    @Autowired
    private UserGameService userGameService;
    @Autowired
    private SendMailTest sendMailTest;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private GameCore gameCore;
    @Autowired
    private UserGameRepository userGameRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private WebBackEnd.service.UserTransactionService transactionService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VouncherService vouncherService;


    @GetMapping("/changepass")
    public String changePass(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/welcome/about";
        User user = userService.findById(UUID.fromString("6CE0FCF6-B584-4A63-AEDF-FAED48E78665"));
        LocalDateTime timeEnd = LocalDateTime.now().plusMinutes(1);

        int day = timeEnd.getDayOfMonth();
        int hour = timeEnd.getHour();
        int minute = timeEnd.getMinute();
        int second = timeEnd.getSecond();

        String statuss = "changePass||" + day + "||" + hour + "||" + minute + "||" + second;

        user.setStatus(statuss);
        userService.save(user);

        String input = "wait" + user.getId();
        String fi;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            fi = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String title = "Đổi mật khẩu của bạn";
        String link = "http://localhost:8080/veryAccount/donePass/" + user.getId() + "/" + fi;

        String content =
                "<p>Hãy nhấp vào liên kết dưới đây để thay đổi mật khẩu của bạn:</p>"
                        + "<p><a href=\"" + link + "\">Nhấn vào đây để đổi</a></p>"
                        + "<p>Nếu không bấm được, copy link sau dán vào trình duyệt:<br>" + link + "</p>";
        sendMailTest.testSend(user.getEmail(), title, content);

        return "HTML/SendPassDone";
    }
}
