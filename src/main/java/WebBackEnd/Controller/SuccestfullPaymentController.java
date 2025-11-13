package WebBackEnd.Controller;

import WebBackEnd.Entity.Game;
import WebBackEnd.Entity.User;
import WebBackEnd.Entity.UserGame;
import WebBackEnd.Entity.UserGameId;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/welcome")
public class SuccestfullPaymentController {
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

    @GetMapping("/succestpayment")
    public String succestory(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, name = "selectedIds") List<UUID> selectedIds,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/welcome/about";
        }

        UUID goBackGameId = null;

        if (userId != null && selectedIds != null && !selectedIds.isEmpty()) {
            User user = userService.findById(userId);

            for (UUID gameId : selectedIds) {
                Game game = gameSevice.findGameById(gameId);
                UserGame existing = userGameService.findByGameAndUser(game, user);

                if (existing == null) {
                    UserGame newUG = new UserGame();
                    newUG.setId(new UserGameId(user.getId(), game.getGameId()));
                    newUG.setUser(user);
                    newUG.setGame(game);
                    newUG.setStatus(1);
                    newUG.setPurchaseDate(LocalDateTime.now());
                    userGameService.saveUserGame(newUG);
                } else if (existing.getStatus() == 0) {
                    existing.setStatus(1);
                    existing.setPurchaseDate(LocalDateTime.now());
                    userGameService.saveUserGame(existing);
                }
            }


            if (selectedIds.size() == 1) {
                goBackGameId = selectedIds.get(0);
            }
        }


        model.addAttribute("gameid", (goBackGameId != null) ? goBackGameId.toString() : "ok");
        return "HTML/Succest";
    }
}
