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

import java.security.MessageDigest;
import java.time.LocalDateTime;
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
    public ResponseEntity<?> verifyEmailForReset(
            @RequestParam("id") UUID id,
            @RequestParam("email") String email
    ) {
        try {

            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Email không được để trống"
                ));
            }


            Users user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "error", "Không tìm thấy người dùng"
                ));
            }


            if (!user.getEmail().equalsIgnoreCase(email.trim())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Email không trùng với email tài khoản"
                ));
            }


            String input = "wait" + id;
            String code;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(input.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }
                code = sb.toString();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Lỗi tạo mã xác thực reset mật khẩu"
                ));
            }


            user.setStatus("changePass");
            user.setExpirationDate(LocalDateTime.now().plusMinutes(2));
            userService.save(user);


            String resetLink = "http://localhost:8080/veryAccount/donePass/"
                    + id.toString() + "/" + code;


            String subject = "GameStore - Đổi mật khẩu tài khoản";
            String body = "Xin chào " + user.getUsername() + ",\n\n"
                    + "Bạn vừa yêu cầu đổi mật khẩu cho tài khoản GameStore.\n"
                    + "Vui lòng nhấn vào link bên dưới để tiếp tục đổi mật khẩu (hạn sử dụng 2 phút):\n\n"
                    + resetLink + "\n\n"
                    + "Nếu bạn không yêu cầu, hãy bỏ qua email này.\n\n"
                    + "Trân trọng,\nGameStore Team";

            try {

                sendMailTest.testSend(email.trim(), subject, body);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Xác thực OK nhưng gửi email thất bại. Thử lại sau."
                ));
            }


            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xác thực email thành công. Vui lòng kiểm tra hộp thư để đổi mật khẩu."
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Lỗi xử lý yêu cầu reset mật khẩu"
            ));
        }
    }


}
