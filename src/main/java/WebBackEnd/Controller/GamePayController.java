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

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping(value = "/welcome")
public class GamePayController {



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

    @GetMapping("/gamePay")
    public String gamePay(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/welcome/about";
        User user = (User) session.getAttribute("user");

        List<UserGame> userGames = userGameService.getGamesByUser(user);
        userGames.sort(Comparator.comparing(UserGame::getPurchaseDate));

        Map<LocalDate, Double> spendByDate = new TreeMap<>();
        for (UserGame ug : userGames) {
            if (ug.getPurchaseDate() == null || ug.getGame() == null) continue;
            if (ug.getStatus() != 1) continue;
            spendByDate.merge(ug.getPurchaseDate().toLocalDate(), ug.getGame().getPrice(), Double::sum);
        }

        LocalDate start = user.getDateCreateAccount() != null
                ? user.getDateCreateAccount().toLocalDate()
                : (spendByDate.isEmpty() ? LocalDate.now() : spendByDate.keySet().iterator().next());

        List<String> labels = new ArrayList<>();
        List<Double> cumulative = new ArrayList<>();
        double totalDouble = 0.0;

        labels.add(start.toString());
        cumulative.add(0.0);

        for (Map.Entry<LocalDate, Double> e : spendByDate.entrySet()) {
            if (e.getKey().isBefore(start)) continue;
            totalDouble += e.getValue();
            labels.add(e.getKey().toString());
            cumulative.add(totalDouble);
        }


        long balance = Math.round(totalDouble);
        model.addAttribute("user", user);
        model.addAttribute("timecreateAcc", user.getDateCreateAccount());
        model.addAttribute("labels", labels);
        model.addAttribute("spendingData", cumulative);
        model.addAttribute("balance", balance);
        return "HTML/GamePay";
    }


}
