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
public class BillManageController {

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

        Map<String, StaffController.BillView> billMap = new LinkedHashMap<>();

        for (UserGame ug : pendingGames) {
            User user = ug.getUser();
            Game game = ug.getGame();
            if (user == null || game == null || ug.getPurchaseDate() == null) continue;

            LocalDate date = ug.getPurchaseDate().toLocalDate();
            String key = user.getId() + "_" + date.toString();

            StaffController.BillView bill = billMap.get(key);
            if (bill == null) {
                bill = new StaffController.BillView();
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

            StaffController.BillGameItem existingItem = null;
            for (StaffController.BillGameItem item : bill.getGames()) {
                if (item.getGameId().equals(game.getGameId())) {
                    existingItem = item;
                    break;
                }
            }

            double originalPrice = game.getPrice();
            double paidPrice = originalPrice;

            if (existingItem == null) {
                StaffController.BillGameItem item = new StaffController.BillGameItem();
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

        List<StaffController.BillView> bills = new ArrayList<>(billMap.values());
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



    private Admin getLoggedStaff(HttpSession session) {

        UUID adminId = (UUID) session.getAttribute("id");
        if (adminId == null) return null;


        return adminSevice.findByAdminid(adminId);
    }

}
