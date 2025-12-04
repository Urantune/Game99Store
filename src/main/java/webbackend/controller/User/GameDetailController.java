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

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/welcome")
public class GameDetailController {

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

    @GetMapping("/gamedetail/{game_id}")
    public String gameDetail(@PathVariable("game_id") UUID game_id,
                             Model model,
                             HttpSession session) {
        Game game = gameSevice.findGameById(game_id);
        model.addAttribute("game", game);

        UUID userId = (UUID) session.getAttribute("userId");
        boolean userGame = false;
        boolean canFeedbackAndDownload = false;
        Feedback myFeedback = null;
        boolean refund = true;
        List<Feedback> list = new ArrayList<>(feedbackService.findFeedbackByGameId(game_id));

        if (userId != null) {
            Users user = userService.findById(userId);
            for (Iterator<Feedback> it = list.iterator(); it.hasNext(); ) {
                Feedback f = it.next();
                if (f.getUserId().equals(userId)) {
                    myFeedback = f;
                    it.remove();
                    break;
                }
            }

            UserGame ug = userGameService.findByGameAndUser(game, user);
            userGame = (ug != null);
            canFeedbackAndDownload = (ug != null && ug.getStatus().equalsIgnoreCase("owned"));
            LocalDateTime now = LocalDateTime.now();
            if (ug != null) {
                if (now.isBefore(ug.getPurchaseDate().plusMinutes(30))) {
                    refund = false;
                }
            } else {
                refund = false;
            }
        }
        ImageGame img = imageGameService.findByGameId(game_id);
        model.addAttribute("img", img);
        System.out.println(img.getImageOne());
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("refund", refund);
        model.addAttribute("UserGame", userGame);
        model.addAttribute("canFeedbackandDownload", canFeedbackAndDownload);
        model.addAttribute("listFeedback", list);
        model.addAttribute("myFeedback", myFeedback);

        return "HTML/GameDetail";
    }


    @PostMapping("/gamedetail/{game_id}")
    public String saveFeedback(@PathVariable("game_id") UUID gameId,
                               @RequestParam("star") Double star,
                               @RequestParam("comment") String cmt,
                               HttpSession session) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) return "redirect:/welcome/login";

        List<Feedback> lst = feedbackService.findFeedbackByGameId(gameId);
        Feedback my = lst.stream()
                .filter(f -> f.getUserId().equals(userId))
                .findFirst().orElse(null);

        if (my == null) {
            my = new Feedback(gameId, userId, cmt, star);
        } else {
            my.setStar(star);
            my.setComment(cmt);
        }
        feedbackService.saveFeedback(my);

        return "redirect:/welcome/gamedetail/" + gameId;
    }




    @GetMapping("/game/detail/{id}")
    public String gameDetail(@PathVariable UUID id, Model model) {
        Game game = gameSevice.findGameById(id);
        model.addAttribute("game", game);
        return "HTML/GameDetail";
    }
}
