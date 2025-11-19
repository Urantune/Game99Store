package webbackend.controller.User;

import webbackend.entity.Game;
import webbackend.SucDat.GameCore;
import webbackend.SucDat.SendMailTest;
import webbackend.entity.VoucherGame;
import webbackend.entity.VoucherUser;
import webbackend.entity.Vouncher;
import webbackend.repository.UserGameRepository;
import webbackend.repository.UserRepository;
import webbackend.service.*;
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
    private webbackend.service.UserTransactionService transactionService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VouncherService vouncherService;
    @Autowired
    private VoucherUserService vouncherUserService;
    @Autowired
    private VoucherGameService voucherGameService;

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
        double subtotal = 0;

        for (UUID id : selectedIds) {
            Game g = gameSevice.findGameById(id);
            if (g != null) {
                valid.add(g);
                subtotal += g.getPrice();
            }
        }

        if (valid.isEmpty()) {
            ra.addFlashAttribute("message", "Không tìm thấy game hợp lệ để thanh toán.");
            return "redirect:/welcome/Cart/" + userId;
        }

        session.setAttribute("checkoutSelectedIds", selectedIds);

        double discount = 0;
        double grandTotal = subtotal - discount;

        model.addAttribute("listGame", valid);
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("vouchers", vouncherService.findAll());

        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("grandTotal", grandTotal);

        model.addAttribute("appliedVoucherCode", null);
        model.addAttribute("appliedVoucherId", null);     // NEW
        model.addAttribute("voucherMessage", "Nhập mã và bấm “Áp dụng”.");
        model.addAttribute("perGameDiscount", null);      // NEW

        return "HTML/Buy";
    }


    @PostMapping("/buy/voucher")
    public String buyConfirm(@RequestParam(value = "voucherCode", required = false) String voucherCode,
                             HttpSession session,
                             Model model,
                             RedirectAttributes ra) {

        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) return "redirect:/welcome/login";

        List<UUID> selectedIds = (List<UUID>) session.getAttribute("checkoutSelectedIds");
        if (selectedIds == null || selectedIds.isEmpty()) {
            ra.addFlashAttribute("message", "Giỏ hàng trống.");
            return "redirect:/welcome/Cart/" + userId;
        }

        // build list game + subtotal
        List<Game> list = new ArrayList<>();
        double total = 0;
        for (UUID id : selectedIds) {
            Game g = gameSevice.findGameById(id);
            if (g != null) {
                list.add(g);
                total += g.getPrice();
            }
        }

        double discount = 0;
        String appliedVoucherCode = null;
        UUID appliedVoucherId = null;                              // NEW
        String voucherMessage;

        java.util.Map<UUID, Double> perGameDiscount = new java.util.HashMap<>();

        if (voucherCode == null || voucherCode.isBlank()) {
            voucherMessage = "Vui lòng nhập mã voucher.";
        } else {
            String code = voucherCode.trim();

            Vouncher voucher = vouncherService.findByName(code);
            if (voucher == null) {
                voucherMessage = "Mã voucher không tồn tại.";
            } else {
                VoucherUser voucherUser = vouncherUserService
                        .getVoucherUserByVouncherAndUser(
                                voucher,
                                userService.findById(userId)
                        );

                if (voucherUser == null) {
                    voucherMessage = "Bạn không sở hữu voucher này.";
                } else {
                    for (Game game : list) {
                        List<VoucherGame> voucherGames =
                                voucherGameService.getVoucherGamesByGame(game);

                        if (voucherGames != null) {
                            for (VoucherGame vg : voucherGames) {
                                if (vg.getVouncher() != null &&
                                        vg.getVouncher().getVoucherid()
                                                .equals(voucher.getVoucherid())) {

                                    double gameDiscount = game.getPrice() * voucher.getSale() / 100.0;
                                    perGameDiscount.put(game.getGameId(), gameDiscount);
                                    discount += gameDiscount;
                                    break;
                                }
                            }
                        }
                    }

                    if (perGameDiscount.isEmpty()) {
                        voucherMessage = "This voucher is not valid for any selected games.";
                    } else {
                        appliedVoucherCode = code;
                        appliedVoucherId = voucher.getVoucherid();         // NEW
                        voucherMessage = "Đã áp dụng voucher: " + code;
                    }
                }
            }
        }

        double subtotal = total;
        double grandTotal = subtotal - discount;
        if (grandTotal < 0) grandTotal = 0;

        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("grandTotal", grandTotal);

        model.addAttribute("listGame", list);
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("vouchers", vouncherService.findAll());

        model.addAttribute("appliedVoucherCode", appliedVoucherCode);
        model.addAttribute("appliedVoucherId", appliedVoucherId);          // NEW
        model.addAttribute("voucherMessage", voucherMessage);
        model.addAttribute("perGameDiscount", perGameDiscount);            // NEW

        return "HTML/Buy";
    }




}
