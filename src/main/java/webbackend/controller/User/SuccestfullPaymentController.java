package webbackend.controller.User;

import webbackend.entity.*;
import webbackend.repository.UserGameRepository;
import webbackend.repository.UserRepository;
import webbackend.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/welcome")
public class SuccestfullPaymentController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired private GameSevice gameSevice;
    @Autowired private UserGameService userGameService;
    @Autowired private UserGameRepository userGameRepository;
    @Autowired private VouncherService vouncherService;
    @Autowired private VoucherGameService voucherGameService;
    @Autowired private UserTransactionService transactionService;
    @Autowired private AdminSevice adminSevice;


    @GetMapping("/user-bill")
    public String userBill(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/welcome/about";
        }

        List<String> displayStatuses = Arrays.asList("waitPay", "refuse","wait");
        List<UserGame> userGames = userGameRepository.findByUserAndStatusIn(sessionUser, displayStatuses);
        model.addAttribute("userGames", userGames);

        Map<String, List<UserGame>> groupedMap = new LinkedHashMap<>();

        for (UserGame ug : userGames) {
            String key;
            if (ug.getPurchaseDate() == null) {
                key = "single|" + ug.getId();
            } else {
                key = ug.getStatus() + "|" + ug.getPurchaseDate();
            }
            groupedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(ug);
        }

        List<List<UserGame>> groupedUserGames = new ArrayList<>(groupedMap.values());
        model.addAttribute("groupedUserGames", groupedUserGames);

        List<Map<String, Double>> billTotals = new ArrayList<>();
        for (List<UserGame> group : groupedUserGames) {
            double subtotal = 0.0;
            double discount = 0.0;

            for (UserGame ug : group) {
                Game game = ug.getGame();
                if (game == null) continue;

                double price = game.getPrice();
                subtotal += price;

                Vouncher voucher = ug.getVouncher();
                if (voucher != null) {
                    double d = price * voucher.getSale() / 100.0;
                    discount += d;
                }
            }

            double payable = subtotal - discount;
            if (payable < 0) payable = 0.0;

            Map<String, Double> totals = new HashMap<>();
            totals.put("totalOriginal", subtotal);
            totals.put("totalDiscount", discount);
            totals.put("totalPayable", payable);

            billTotals.add(totals);
        }
        model.addAttribute("billTotals", billTotals);

        return "HTML/UserBills";
    }

    @GetMapping("/cancel-bill")
    public String cancelBill(
            @RequestParam("billTime")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime billDateTime,
            HttpSession session) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/welcome/login";
        }


        List<UserGame> billItems =
                userGameRepository.findByUserAndPurchaseDateAndStatus(sessionUser, billDateTime, "wait");

        if (billItems == null || billItems.isEmpty()) {

            return "redirect:/welcome/user-bill";
        }


        for (UserGame ug : billItems) {
            userGameRepository.delete(ug);
        }

        return "redirect:/welcome/user-bill";
    }




    @PostMapping("/waitCheckPay")
    public String waitCheckPayment(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, name = "selectedIds") List<UUID> selectedIds,
            @RequestParam(required = false) UUID voucherId,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/welcome/about";
        }

        if (userId != null && selectedIds != null && !selectedIds.isEmpty()) {

            User user = userService.findById(userId);

            Vouncher voucher = null;
            if (voucherId != null) {
                voucher = vouncherService.findByUuid(voucherId);
            }

            // 🔥 billTime CHUNG CHO CẢ BILL, bỏ nano để dễ so sánh
            LocalDateTime billTime = LocalDateTime.now().withNano(0);

            for (UUID gameId : selectedIds) {
                Game game = gameSevice.findGameById(gameId);
                if (game == null) continue;

                UserGame ug = userGameService.findByGameAndUser(game, user);
                if (ug == null) {
                    ug = new UserGame();
                    ug.setUser(user);
                    ug.setGame(game);
                }

                ug.setStatus("wait");
                ug.setPurchaseDate(billTime);


                if (voucher != null) {
                    List<VoucherGame> voucherGames = voucherGameService.getVoucherGamesByGame(game);
                    boolean applicable = false;
                    if (voucherGames != null) {
                        for (VoucherGame vg : voucherGames) {
                            if (vg.getVouncher() != null &&
                                    vg.getVouncher().getVoucherid().equals(voucher.getVoucherid())) {
                                applicable = true;
                                break;
                            }
                        }
                    }
                    if (applicable) {
                        ug.setVouncher(voucher);
                    } else {
                        ug.setVouncher(null);
                    }
                } else {
                    ug.setVouncher(null);
                }

                userGameService.saveUserGame(ug);
            }
        }

        model.addAttribute("userid", (userId != null) ? userId.toString() : "ok");

        StringBuilder idsBuilder = new StringBuilder();
        if (selectedIds != null && !selectedIds.isEmpty()) {
            for (UUID id : selectedIds) {
                idsBuilder.append(id.toString()).append(",");
            }
            idsBuilder.setLength(idsBuilder.length() - 1);
        } else {
            idsBuilder.append("ok");
        }
        model.addAttribute("selectedids", idsBuilder.toString());

        return "HTML/WaitCheckPayment";
    }




    @GetMapping("/succestpayment")
    public String succestPayment(
            @RequestParam("billTime")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime billDateTime,
            HttpSession session) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/welcome/login";

        // 1. Lấy tất cả item của bill đang waitPay
        List<UserGame> billItems =
                userGameRepository.findByUserAndPurchaseDateAndStatus(sessionUser, billDateTime, "waitPay");

        if (billItems == null || billItems.isEmpty()) {
            return "redirect:/welcome/user-bill";
        }

        double totalPay = 0;

        // 2. Tính tổng tiền sau voucher
        for (UserGame ug : billItems) {
            Game game = ug.getGame();
            if (game == null) continue;

            double basePrice = game.getPrice();
            double discount = 0;

            if (ug.getVouncher() != null) {
                discount = basePrice * ug.getVouncher().getSale() / 100.0;
            }

            double finalPrice = basePrice - discount;
            if (finalPrice < 0) finalPrice = 0;

            totalPay += finalPrice;
        }

        // 3. Check số dư
        User user = sessionUser;
        if (user.getPrice() < totalPay) {
            System.out.println(">>> NOT ENOUGH BALANCE. Need = " + totalPay + ", have = " + user.getPrice());
            return "redirect:/welcome/user-bill";
        }

        // 4. Trừ tiền
        user.setPrice(user.getPrice() - totalPay);
        userRepository.save(user);

        // 5. Update từng UserGame → owned
        LocalDateTime now = LocalDateTime.now().withNano(0);

        for (UserGame ug : billItems) {
            Game game = ug.getGame();
            if (game == null) continue;

            double base = game.getPrice();
            double discount = 0;
            if (ug.getVouncher() != null) {
                discount = base * ug.getVouncher().getSale() / 100.0;
            }

            double finalPrice = base - discount;
            if (finalPrice < 0) finalPrice = 0;

            ug.setStatus("owned");
            ug.setPurchasePrice(finalPrice);
            ug.setPurchaseDate(now); // thời điểm thanh toán thành công
            userGameRepository.save(ug);
        }

        // 6. Lưu transaction
        UserTransaction tx = new UserTransaction();
        tx.setUser(user);
        tx.setAmount(totalPay);
        tx.setType("QR CODE");
        tx.setStatus("SUCCESS");
        tx.setDescription(null);
        tx.setStatucDetail(null);
        tx.setTransactionDate(now);

        transactionService.save(tx);

        return "redirect:/welcome/user-bill";
    }



}
