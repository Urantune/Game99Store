package WebBackEnd.Controller;

import WebBackEnd.Entity.Game;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/welcome")
public class BuyController {

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

    @PostMapping("/buy")
    public String buy(@RequestParam(value = "selectedIds", required = false) List<UUID> selectedIds,
                      Model model,
                      HttpSession session,
                      RedirectAttributes ra) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) return "redirect:/welcome/login";

        if (selectedIds == null || selectedIds.isEmpty()) {
            ra.addFlashAttribute("message", "Vui lòng chọn ít nhất 1 game.");
            return "redirect:/welcome/Cart/" + userId;
        }

        List<Game> valid = new ArrayList<>();
        for (UUID id : selectedIds) {
            Game g = gameSevice.findGameById(id);
            if (g != null) valid.add(g);
        }
        if (valid.isEmpty()) {
            ra.addFlashAttribute("message", "Không tìm thấy game hợp lệ để thanh toán.");
            return "redirect:/welcome/Cart/" + userId;
        }

        session.setAttribute("checkoutSelectedIds", selectedIds);
        model.addAttribute("listGame", valid);
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("vouchers", vouncherService.findAll());
        return "HTML/Buy";
    }
}
