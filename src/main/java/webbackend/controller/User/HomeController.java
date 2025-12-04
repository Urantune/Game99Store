package webbackend.controller.User;

import webbackend.entity.*;
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
    private webbackend.service.UserTransactionService transactionService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VouncherService vouncherService;
    @Autowired
    private ImageGameService imageGameService;

    @GetMapping
    public String homepage(Model model, HttpSession session) {


        List<Game> listgame = gameSevice.list20GameIntoGame();

        if (!model.containsAttribute("showForm")) {
            model.addAttribute("showForm", "");
        }
        // System.out.println(gameCore.imageLinkGame(listgame.get(0)).getMainImage());

        model.addAttribute("gameMain", gameSevice.findGameByStatus("main"));
        model.addAttribute("listGame", listgame);
        model.addAttribute("gameCore", gameCore);



        UUID userId = (UUID) session.getAttribute("userId");
        Users user = null;

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

            ImageGame imageGame = imageGameService.findByGameId(game.getGameId());
            String mainImage = "/img/notfound.png";

            if (imageGame != null && imageGame.getMainImage() != null && !imageGame.getMainImage().isBlank()) {
                mainImage = "/" + imageGame.getMainImage();
            }

            g.put("image", mainImage);
            return g;
        }).toList();
    }

}
