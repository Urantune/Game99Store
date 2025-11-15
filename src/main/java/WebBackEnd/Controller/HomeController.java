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

import java.util.*;

@Controller
@RequestMapping(value = "/welcome")
public class HomeController {

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

    @GetMapping
    public String homepage(Model model, HttpSession session) {
        if (!model.containsAttribute("showForm")) {
            model.addAttribute("showForm", "");
        }
        model
                .addAttribute("gameMain", gameSevice
                .findGameByStatus("main"));
        model
                .addAttribute("listGame", gameSevice
                .list20GameIntoGame());
        model
                .addAttribute("linkimage", GameCore
                .imageLinkGame(gameSevice
                        .findGameByStatus("main")
                        .getImageLinks()));

        UUID userId = (UUID) session.getAttribute("userId");
        User user = null;
        if (userId != null) {
            user = userService.getUserById(userId);
        }
        model.addAttribute("user", user);

        return "HTML/Index";
    }






    @GetMapping("/games/all")
    @ResponseBody
    public List<Map<String, Object>> getAllGames() {
        return gameSevice.findAllGame().stream().map(game -> {
            Map<String, Object> g = new HashMap<>();
            g.put("id", game.getGameId());
            g.put("name", game.getGameName());
            String[] imgs = game.getLinkImage();
            String mainImage = "/img/notfound.png";
            if (imgs != null && imgs.length > 0) {
                for (String link : imgs) {
                    String lower = link.toLowerCase();
                    if (lower.contains("img/game") && lower.endsWith(".jpg")) {
                        mainImage = "/" + link;
                        break;
                    }
                }
                if (mainImage.equals("/img/notfound.png")) {
                    for (String link : imgs) {
                        if (!link.endsWith(".mp4")) {
                            mainImage = "/" + link;
                            break;
                        }
                    }
                }
            }
            g.put("image", mainImage);
            return g;
        }).toList();
    }
}
