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
public class GameDetail {

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
            User user = userService.findById(userId);
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
            canFeedbackAndDownload = (ug != null && ug.getStatus() == 1);
            LocalDateTime now = LocalDateTime.now();
            if (ug != null) {
                if (now.isBefore(ug.getPurchaseDate().plusMinutes(30))) {
                    refund = false;
                }
            } else {
                refund = false;
            }
        }

        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("refund", refund);
        model.addAttribute("UserGame", userGame);
        model.addAttribute("canFeedbackandDownload", canFeedbackAndDownload);
        model.addAttribute("listFeedback", list);
        model.addAttribute("myFeedback", myFeedback);

        return "HTML/GameDetail";
    }
}
