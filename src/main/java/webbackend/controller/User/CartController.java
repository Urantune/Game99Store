package webbackend.controller.User;

import webbackend.entity.Game;
import webbackend.entity.Users;
import webbackend.entity.UserGame;
import webbackend.SucDat.GameCore;
import webbackend.SucDat.SendMailTest;
import webbackend.repository.UserGameRepository;
import webbackend.repository.UserRepository;
import webbackend.service.*;
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
    private webbackend.service.UserTransactionService transactionService;
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

        Users user = userService.findById(userId);
        Game game = gameSevice.findGameById(gameId);
        if (user == null || game == null) {
            ra.addFlashAttribute("message", "Yêu cầu không hợp lệ.");
            return "redirect:/welcome/Cart/" + userId;
        }

        UserGame ug = userGameService.findByGameAndUser(game, user);
        if (ug != null && ug.getStatus() == "cart") {
            userGameRepository.delete(ug);
            ra.addFlashAttribute("cartSuccess", "Đã xóa khỏi giỏ hàng");
        } else {
            ra.addFlashAttribute("cartError", "Không thể xóa mục này");
        }

        return "redirect:/welcome/Cart/" + userId;
    }



    @PostMapping("/addGameToCard/{game_id}")
    public String addGameToCard(@PathVariable("game_id") UUID gameId,
                                HttpSession session,
                                RedirectAttributes ra) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/welcome/login";
        }

        var user = userService.findById(userId);
        var game = gameSevice.findGameById(gameId);

        boolean existed = userGameService.findUserGameByUserAndGame(user, game);

        if (existed) {
            ra.addAttribute("cartError", "Bạn đã thêm game này vào giỏ hàng rồi");
        } else {
            userGameService.saveUserGame(new UserGame(user, game, java.time.LocalDateTime.now(), "cart",game.getPrice()));
            ra.addAttribute("cartSuccess", "Đã thêm vào giỏ hàng!");
        }


        return "redirect:/welcome/gamedetail/{game_id}";
    }

}
