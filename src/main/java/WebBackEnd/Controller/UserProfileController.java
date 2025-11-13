package WebBackEnd.Controller;

import WebBackEnd.Entity.*;
import WebBackEnd.SucDat.GameCore;
import WebBackEnd.SucDat.SendMailTest;
import WebBackEnd.repository.UserGameRepository;
import WebBackEnd.repository.UserRepository;
import WebBackEnd.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.MessageDigest;
import java.time.LocalDate;
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
    private WebBackEnd.service.UserTransactionService transactionService;
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
            return userGame != null && userGame.getStatus() != 1;
        });

        model.addAttribute("user", user);
        model.addAttribute("id", id);
        model.addAttribute("listGame", game);
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

}
