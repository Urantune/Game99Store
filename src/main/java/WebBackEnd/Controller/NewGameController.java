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
public class NewGameController {

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

    @GetMapping("/Newgame")
    public String newgame(Model model, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("userId");
        User user = null;
        if (userId != null) {
            user = userService.getUserById(userId);
        }
        model.addAttribute("user", user);

        model.addAttribute("gameCore", gameCore);
        model.addAttribute("eventMain", eventService.findEventByType("event_main"));
        model.addAttribute("eventNext", eventService.findEventByType("event_next"));

        var smallEvents = eventService.findEventsByType("event_small");
        model.addAttribute("events", smallEvents != null ? smallEvents : java.util.Collections.emptyList());

        model.addAttribute("games", java.util.Collections.emptyList());
        model.addAttribute("registeredGames", java.util.Collections.emptySet());

        return "HTML/NewGame";
    }

}
