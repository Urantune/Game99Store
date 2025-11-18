package webbackend.controller.User;

import webbackend.entity.*;
import webbackend.SucDat.GameCore;
import webbackend.SucDat.SendMailTest;
import webbackend.repository.UserGameRepository;
import webbackend.repository.UserRepository;
import webbackend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping(value = "/welcome")
public class VerifyResetController {


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

    @PostMapping("/verify-reset")
    @ResponseBody
    public ResponseEntity<?> verifyEmailForReset(@RequestBody Map<String, String> payload) {
        try {
            String idStr = payload.get("id");
            String email = payload.get("email");

            if (idStr == null || email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu tham số id/email"));
            }

            UUID id = UUID.fromString(idStr);
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy người dùng"));
            }

            if (!user.getEmail().equalsIgnoreCase(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email không trùng với email tài khoản"));
            }

            return ResponseEntity.ok(Map.of("message", "Xác thực email thành công"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Yêu cầu không hợp lệ"));
        }
    }
}
