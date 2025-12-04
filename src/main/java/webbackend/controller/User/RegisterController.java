package webbackend.controller.User;

import webbackend.entity.*;
import webbackend.SucDat.GameCore;
import webbackend.SucDat.SendMailTest;
import webbackend.repository.UserGameRepository;
import webbackend.repository.UserRepository;
import webbackend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/welcome")
public class RegisterController {

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
    private webbackend.service.UserTransactionService transactionService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VouncherService vouncherService;

    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> registerAjax(@RequestBody Users user) {
        Map<String, Object> response = new HashMap<>();
        String username = user.getUsername();
        String email = user.getEmail();
        String rawPassword = user.getPassword();

        if (username == null || username.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được để trống");
            return response;
        }
        if (!username.matches("^[a-zA-Z0-9._]+$")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản chỉ được chứa chữ, số, dấu chấm hoặc gạch dưới");
            return response;
        }
        if (username.startsWith(".") || username.startsWith("_") ||
                username.endsWith(".") || username.endsWith("_")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được bắt đầu hoặc kết thúc bằng dấu chấm hoặc gạch dưới");
            return response;
        }
        if (username.contains("..") || username.contains("__") ||
                username.contains("._") || username.contains("_.")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được chứa ký tự đặc biệt liên tiếp");
            return response;
        }
        if (username.length() < 3 || username.length() > 20) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản phải có độ dài từ 3 đến 20 ký tự");
            return response;
        }
        if (userRepository.existsByUsername(username)) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản đã tồn tại");
            return response;
        }
        if (email == null || email.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email không được để trống");
            return response;
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            response.put("status", "error");
            response.put("message", "Mật khẩu không được để trống");
            return response;
        }
        if (rawPassword.length() < 8) {
            response.put("status", "error");
            response.put("message", "Mật khẩu phải từ 8 kí tự");
            return response;
        }
        if (rawPassword.contains("<script")) {
            response.put("status", "error");
            response.put("message", ";)");
            return response;
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setStatus("wait");
        user.setDateCreateAccount(LocalDateTime.now());
        user.setExpirationDate(LocalDateTime.now().plusMinutes(2));
        userRepository.save(user);

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

        String title = "Xác nhận tài khoản của bạn";
        String link = "http://localhost:8080/veryAccount/done/" + user.getId() + "/" + fi;
        String content =
                "<p>Hãy nhấp vào liên kết dưới đây để kích hoạt tài khoản của bạn (hạn 2 phút):</p>"
                        + "<p><a href=\"" + link + "\">Nhấn vào đây để kích hoạt</a></p>"
                        + "<p>Nếu không bấm được, copy link sau dán vào trình duyệt:<br>" + link + "</p>";
        sendMailTest.testSend(user.getEmail(), title, content);

        response.put("status", "success");
        response.put("message", "Đăng ký thành công! Một đường link xác thực tài khoản đã được gửi vào email của bạn.");
        return response;
    }
}
