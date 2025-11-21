package webbackend.controller.User;

import webbackend.entity.*;
import webbackend.SucDat.GameCore;
import webbackend.SucDat.SendMailTest;
import webbackend.repository.UserGameRepository;
import webbackend.repository.UserRepository;
import webbackend.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;


@Controller
@RequestMapping("/welcome")
public class UserProfileController {


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



    @GetMapping("/profile/{id}")
    public String userDetail(@PathVariable UUID id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/welcome";

        List<Game> game = userGameService.showGameInProfile(id);
        game.removeIf(g -> {
            UserGame userGame = userGameService.findByGameAndUser(g, user);
            return userGame != null && userGame.getStatus() .equalsIgnoreCase("owned") == false;
        });

        model.addAttribute("user", user);
        model.addAttribute("id", id);
        model.addAttribute("listGame", game);
        model.addAttribute("gameCore", gameCore);
        return "HTML/ProfileUser";
    }


    @PostMapping("/profile/{id}/avatar")
    @ResponseBody
    public ResponseEntity<?> updateAvatar(@PathVariable UUID id,
                                          @RequestParam("avatarPath") String avatarPath,
                                          HttpSession session) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Bạn chưa đăng nhập"));
        }

        if (!sessionUser.getId().equals(id)) {
            return ResponseEntity.status(403).body(Map.of("error", "Không có quyền đổi avatar của người khác"));
        }


        List<String> allowed = List.of(
                "/img/a.png", "/img/NataliKhang.jpg", "/img/JoLong.jpg",
                "/img/khangbo.jpg", "/img/BiTrong.jpg", "/img/5000.jpg"
        );
        if (!allowed.contains(avatarPath)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ảnh không hợp lệ"));
        }


        User user = userService.findById(id);
        user.setImageLinks(avatarPath.startsWith("/") ? avatarPath.substring(1) : avatarPath);

        userService.save(user);


        session.setAttribute("user", user);

        return ResponseEntity.ok(Map.of("success", true, "avatar", "/" + user.getImageLinks()));
    }

    @PostMapping("/profile/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> requestDeleteProfile(@PathVariable UUID id,
                                                  HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "error", "Bạn chưa đăng nhập"
            ));
        }

        if (!sessionUser.getId().equals(id)) {
            return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "error", "Không thể xóa tài khoản của người khác"
            ));
        }

        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "error", "Tài khoản không tồn tại"
            ));
        }

        user.setStatus("deleting");
        user.setExpirationDate(LocalDateTime.now().plusMinutes(2));
        userService.save(user);

        String input = "deleting" + id;
        String code;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            code = sb.toString();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Lỗi tạo mã xác thực xóa tài khoản"
            ));
        }

        String link = "http://localhost:8080/veryAccount/doneDelete/" + id + "/" + code;
        String subject = "Xác nhận xoá tài khoản – GameStore";
        String content =
                "<p>Bạn đã yêu cầu xoá tài khoản GameStore.</p>" +
                        "<p>Nhấn vào link dưới đây để xác nhận (hạn sử dụng 2 phút):</p>" +
                        "<p><a href=\"" + link + "\">Xác nhận xoá tài khoản</a></p>" +
                        "<p>Nếu không phải bạn thực hiện, hãy bỏ qua email này.</p>";

        try {
            sendMailTest.testSend(user.getEmail(), subject, content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Gửi email xác nhận thất bại, thử lại sau"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã gửi email xác nhận xoá tài khoản. Vui lòng kiểm tra hộp thư.",
                "redirectUrl", "/logout"
        ));
    }






}
