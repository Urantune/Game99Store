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
@RequestMapping("/welcome")
public class RefundGameController {

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


    @PostMapping("/refundGame")
    public String refundGamePost(@RequestParam("gameId") UUID gameId, Model model,
                                 HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/welcome/login";
        }

        User user = userService.findById(userId);
        Game game = gameSevice.findGameById(gameId);

        userGameService.DeleteByUserGame(user, game);

        user.setPrice(user.getPrice() + game.getPrice());
        userService.save(user);
        model.addAttribute("gameid", gameId);

        return "HTML/SuccestRefund";
    }
}
