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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/welcome")
public class CheckoutController {

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


    @PostMapping("/checkout")
    public String checkout(@RequestParam(value = "selectedIds", required = false) List<UUID> selectedIds,
                           @RequestParam("userId") UUID userIdFromForm,
                           HttpSession session,
                           Model model,
                           RedirectAttributes ra) {
        UUID sessionUserId = (UUID) session.getAttribute("userId");
        if (sessionUserId == null) return "redirect:/welcome/login";
        if (!sessionUserId.equals(userIdFromForm)) {
            ra.addFlashAttribute("message", "Phiên không hợp lệ. Vui lòng thử lại.");
            return "redirect:/welcome/Cart/" + sessionUserId;
        }

        if (selectedIds == null || selectedIds.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<UUID> idsInSession = (List<UUID>) session.getAttribute("checkoutSelectedIds");
            if (idsInSession == null || idsInSession.isEmpty()) {
                ra.addFlashAttribute("message", "Vui lòng chọn game để thanh toán.");
                return "redirect:/welcome/Cart/" + sessionUserId;
            }
            selectedIds = idsInSession;
        }

        Users user = userService.findById(sessionUserId);
        if (user == null) return "redirect:/welcome/login";

        List<Game> payable = new ArrayList<>();
        for (UUID id : selectedIds) {
            Game g = gameSevice.findGameById(id);
            if (g != null) payable.add(g);
        }
        if (payable.isEmpty()) {
            ra.addFlashAttribute("message", "Không có game hợp lệ để thanh toán.");
            return "redirect:/welcome/Cart/" + sessionUserId;
        }

        double total = 0d;
        for (Game g : payable) {
            total += (g.getPrice() > 0 ? g.getPrice() : 0d);
        }

        if (user.getPrice() < total) {
            model.addAttribute("thongbao", "khongdusodu");
            model.addAttribute("listGame", payable);
            model.addAttribute("user", user);
            return "HTML/Buy";
        }

        user.setPrice(user.getPrice() - total);
        userService.save(user);

        for (Game g : payable) {
            UserGame ug = userGameService.findByGameAndUser(g, user);
            if (ug == null) {
                ug = new UserGame(user, g, LocalDateTime.now(), "owned",g.getPrice());
            } else {
                ug.setStatus("owned");
                ug.setPurchaseDate(LocalDateTime.now());
            }
            userGameService.saveUserGame(ug);
        }

        session.removeAttribute("checkoutSelectedIds");
        ra.addFlashAttribute("message", "Thanh toán thành công!");
        return "redirect:/welcome/Cart/" + sessionUserId;
    }



}
