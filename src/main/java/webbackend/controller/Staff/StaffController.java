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
