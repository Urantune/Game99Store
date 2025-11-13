package WebBackEnd.Controller;

import WebBackEnd.Entity.Game;
import WebBackEnd.Entity.User;
import WebBackEnd.Entity.UserGame;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/welcome")
public class CartController {

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


    @GetMapping("/Cart/{id}")
    public String payMent(@PathVariable UUID id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/welcome/about";
        model.addAttribute("listGame", userGameService.showGameInCart(id));
        model.addAttribute("user", userService.findById(id));
        return "HTML/Cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("gameId") UUID gameId,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) return "redirect:/welcome/login";

        User user = userService.findById(userId);
        Game game = gameSevice.findGameById(gameId);
        if (user == null || game == null) {
            ra.addFlashAttribute("message", "Yêu cầu không hợp lệ.");
            return "redirect:/welcome/Cart/" + userId;
        }

        UserGame ug = userGameService.findByGameAndUser(game, user);
        if (ug != null && ug.getStatus() == 0) {
            userGameRepository.delete(ug);
            ra.addFlashAttribute("cartSuccess", "Đã xóa khỏi giỏ hàng");
        } else {
            ra.addFlashAttribute("cartError", "Không thể xóa mục này");
        }

        return "redirect:/welcome/Cart/" + userId;
    }
}
