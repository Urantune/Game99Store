package webbackend.controller.Staff;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webbackend.entity.*;
import webbackend.SucDat.GameCore;
import webbackend.SucDat.SendMailTest;
import webbackend.repository.UserGameRepository;
import webbackend.service.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping(value = "/welcomeStaff")
public class StaffController {

    @Autowired
    private AdminSevice adminSevice;

    @Autowired
    private UserService userService;

    @Autowired
    private GameSevice gameSevice;

    @Autowired
    private GameCore gameCore;

    @Autowired
    private EventService eventService;

    @Autowired
    private VouncherService vouncherService;

    @Autowired
    private UserGameService userGameService;

    @Autowired
    private UserGameRepository userGameRepository;

    @Autowired
    private SendMailTest sendMailTest;

    @Autowired
    private ImageGameService imageGameService;

    @Autowired
    private PasswordEncoder passwordEncoder;



    private Admin getLoggedStaff(HttpSession session) {

        UUID adminId = (UUID) session.getAttribute("id");
        if (adminId == null) return null;


        return adminSevice.findByAdminid(adminId);
    }


    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> loginStaff(@RequestParam("username") String username,
                                        @RequestParam("password") String password,
                                        HttpSession session) {

        Admin admin = adminSevice.findByUsername(username);
        if (admin == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Tài khoản không tồn tại!"));
        }


        if (admin.getRole() == null ||
                !(admin.getRole().equalsIgnoreCase("STAFF")
                        || admin.getRole().equalsIgnoreCase("ADMIN"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Tài khoản này không có quyền STAFF/ADMIN!"));
        }

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Sai mật khẩu!"));
        }


        session.setAttribute("id", admin.getAdminid());
        session.setAttribute("adminName", admin.getAdminName());
        session.setAttribute("avatar", admin.getImageLinks());
        session.setAttribute("adminRole", admin.getRole());

        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping({"", "/"})
    public String homeStaff(Model model, HttpSession session) {

        Admin staff = getLoggedStaff(session);
        model.addAttribute("staff", staff);
        return "STAFF/IndexStaff";
    }

    @GetMapping("/billManagement")
    public String billManagement(Model model, HttpSession session,
                                 RedirectAttributes ra) {



        List<UserGame> allUserGames = userGameService.findAll();

        List<UserGame> pendingGames = new ArrayList<>();
        for (UserGame ug : allUserGames) {
            if (ug.getStatus() != null && ug.getStatus().equalsIgnoreCase("wait")) {
                pendingGames.add(ug);
            }
        }

        Map<String, BillView> billMap = new LinkedHashMap<>();

        for (UserGame ug : pendingGames) {
            User user = ug.getUser();
            Game game = ug.getGame();
            if (user == null || game == null || ug.getPurchaseDate() == null) continue;

            LocalDate date = ug.getPurchaseDate().toLocalDate();
            String key = user.getId() + "_" + date.toString();

            BillView bill = billMap.get(key);
            if (bill == null) {
                bill = new BillView();
                bill.setId("BILL-" + (billMap.size() + 1));
                bill.setUserId(user.getId());
                bill.setUserName(user.getUsername());
                bill.setUserEmail(user.getEmail());
                bill.setPurchaseDate(ug.getPurchaseDate());
                bill.setStatus(ug.getStatus());
                bill.setGames(new ArrayList<>());
                bill.setSubTotal(0.0);
                bill.setTotalAmount(0.0);
                bill.setDiscountAmount(0.0);
                billMap.put(key, bill);
            }

            BillGameItem existingItem = null;
            for (BillGameItem item : bill.getGames()) {
                if (item.getGameId().equals(game.getGameId())) {
                    existingItem = item;
                    break;
                }
            }

            double originalPrice = game.getPrice();
            double paidPrice = originalPrice; // nếu có logic voucher từng game thì sửa chỗ này

            if (existingItem == null) {
                BillGameItem item = new BillGameItem();
                item.setGameId(game.getGameId());
                item.setGameName(game.getGameName());
                item.setPrice(originalPrice);
                item.setQuantity(1);
                bill.getGames().add(item);
            } else {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
            }

            bill.setSubTotal(bill.getSubTotal() + originalPrice);
            bill.setTotalAmount(bill.getTotalAmount() + paidPrice);
            bill.setDiscountAmount(bill.getSubTotal() - bill.getTotalAmount());

            if (bill.getVoucherCode() == null && ug.getVouncher() != null) {
                Vouncher v = ug.getVouncher();
                bill.setVoucherCode(v.getName());
                bill.setVoucherDescription("Voucher applied");
            }
        }

        List<BillView> bills = new ArrayList<>(billMap.values());
        model.addAttribute("bills", bills);

        return "STAFF/BillManager";
    }



    @PostMapping("/billManagement/approve")
    public String approveBill(@RequestParam("userId") UUID userId,
                              @RequestParam("billDate")
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate billDate,
                              HttpSession session,
                              RedirectAttributes ra) {

        Admin staff = getLoggedStaff(session);
        if (staff == null) {
            ra.addFlashAttribute("error", "Bạn cần đăng nhập STAFF trước khi xác nhận thanh toán.");
            return "redirect:/welcomeStaff/billManagement";
        }

        List<UserGame> allUserGames = userGameService.findAll();
        int count = 0;

        for (UserGame ug : allUserGames) {
            if (ug.getUser() != null
                    && ug.getUser().getId().equals(userId)
                    && ug.getPurchaseDate() != null
                    && ug.getPurchaseDate().toLocalDate().equals(billDate)
                    && ug.getStatus() != null
                    && ug.getStatus().equalsIgnoreCase("wait")) {

                ug.setStatus("waitPay");
                ug.setStaff(staff);
                userGameService.saveUserGame(ug);
                count++;
            }
        }

        ra.addFlashAttribute("message", "Approved " + count + " item(s).");
        return "redirect:/welcomeStaff/billManagement";
    }


    @PostMapping("/billManagement/reject")
    public String rejectBill(@RequestParam("userId") UUID userId,
                             @RequestParam("billDate")
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate billDate,
                             HttpSession session,
                             RedirectAttributes ra) {

        Admin staff = getLoggedStaff(session);
        if (staff == null) {
            ra.addFlashAttribute("error", "Bạn cần đăng nhập STAFF trước khi từ chối.");
            return "redirect:/welcomeStaff/billManagement";
        }

        List<UserGame> allUserGames = userGameService.findAll();
        int count = 0;

        for (UserGame ug : allUserGames) {
            if (ug.getUser() != null
                    && ug.getUser().getId().equals(userId)
                    && ug.getPurchaseDate() != null
                    && ug.getPurchaseDate().toLocalDate().equals(billDate)
                    && ug.getStatus() != null
                    && ug.getStatus().equalsIgnoreCase("wait")) {

                ug.setStatus("refuse");
                ug.setStaff(staff);
                userGameService.saveUserGame(ug);
                count++;
            }
        }

        ra.addFlashAttribute("message", "Rejected " + count + " item(s).");
        return "redirect:/welcomeStaff/billManagement";
    }


    @GetMapping("/bill-history")
    public String billHistory(Model model) {
        // lịch sử các bill đã thanh toán
        List<UserGame> list = userGameRepository
                .findByStatusOrderByPurchaseDateDesc("owned");
        model.addAttribute("userGames", list);
        return "STAFF/BillHistory";
    }


    public static class BillView {
        private String id;
        private UUID userId;
        private String userName;
        private String userEmail;
        private List<BillGameItem> games;
        private double subTotal;
        private double discountAmount;
        private double totalAmount;
        private String voucherCode;
        private String voucherDescription;
        private LocalDateTime purchaseDate;
        private String status;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

        public List<BillGameItem> getGames() { return games; }
        public void setGames(List<BillGameItem> games) { this.games = games; }

        public double getSubTotal() { return subTotal; }
        public void setSubTotal(double subTotal) { this.subTotal = subTotal; }

        public double getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

        public String getVoucherCode() { return voucherCode; }
        public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }

        public String getVoucherDescription() { return voucherDescription; }
        public void setVoucherDescription(String voucherDescription) { this.voucherDescription = voucherDescription; }

        public LocalDateTime getPurchaseDate() { return purchaseDate; }
        public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class BillGameItem {
        private UUID gameId;
        private String gameName;
        private double price;
        private int quantity;

        public UUID getGameId() { return gameId; }
        public void setGameId(UUID gameId) { this.gameId = gameId; }

        public String getGameName() { return gameName; }
        public void setGameName(String gameName) { this.gameName = gameName; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
