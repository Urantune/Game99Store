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

    @Autowired
    private UserGameArchiveService userGameArchiveService;


    @GetMapping("/refundGame")
    public String showRefundForm(@RequestParam("gameId") UUID gameId,
                                 Model model,
                                 HttpSession session) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/welcome/login";
        }

        Game game = gameSevice.findGameById(gameId);
        if (game == null) {
            return "redirect:/welcome/my-games";
        }

        model.addAttribute("game", game);
        return "HTML/RefundGame";
    }

    @PostMapping("/refundGame")
    public String refundGamePost(
            @RequestParam("gameId") UUID gameId,
            @RequestParam("reason") String reason,
            Model model,
            HttpSession session) {

        if (reason == null || reason.isBlank()) {
            reason = "No reason provided";
        }

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/welcome/login";
        }

        User user = userService.findById(sessionUser.getId());
        Game game = gameSevice.findGameById(gameId);

        UserGame ug = userGameService.findByGameAndUser(game, user);
        if (ug == null) {
            return "redirect:/welcome/my-games";
        }

        double refundAmount = ug.getPurchasePrice() > 0 ? ug.getPurchasePrice() : game.getPrice();

        // + tiền lại cho user
        user.setPrice(user.getPrice() + refundAmount);
        userService.save(user);

        // Lưu archive
        UserGameArchive archive = new UserGameArchive();
        archive.setUser(user);
        archive.setGame(game);
        archive.setStaff(ug.getStaff());
        archive.setPurchaseDate(ug.getPurchaseDate() != null ? ug.getPurchaseDate().toLocalDate() : null);
        archive.setExpireDate(java.time.LocalDate.now());
        archive.setOriginalPrice(ug.getPurchasePrice());
        archive.setVouncher(ug.getVouncher());
        archive.setStatus("refuse");
        userGameArchiveService.save(archive);

        // Xoá khỏi UserGame
        userGameService.DeleteByUserGame(user, game);

        // Lưu UserTransaction
        UserTransaction tx = new UserTransaction();
        tx.setUser(user);
        tx.setGame(game);
        tx.setVouncher(ug.getVouncher());
        tx.setAmount(refundAmount);
        tx.setType("REFUND");
        tx.setStatus("refuse");
        tx.setDescription("deception");
        tx.setStatucDetail(reason);
        tx.setTransactionDate(LocalDateTime.now());
        transactionService.save(tx);

        model.addAttribute("game", game);
        model.addAttribute("refundAmount", refundAmount);
        model.addAttribute("reason", reason);

        return "HTML/SuccestRefund";
    }


}
