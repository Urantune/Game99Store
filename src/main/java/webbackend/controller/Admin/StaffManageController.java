package webbackend.controller.Admin;

import webbackend.entity.*;
import webbackend.SucDat.GameCore;
import webbackend.SucDat.SendMailTest;
import webbackend.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


@Controller
@RequestMapping(value = "/welcomeAdmin")
public class StaffManageController {

    @Autowired
    private AdminSevice
            adminSevice;

    @Autowired
    private UserService
            userService;

    @Autowired
    private GameSevice
            gameSevice;

    @Autowired
    private GameCore
            gameCore;

    @Autowired
    private EventService
            eventService;

    @Autowired
    private PasswordEncoder
            passwordEncoder;

    @Autowired
    private VouncherService
            vouncherService;

    @Autowired
    private UserGameService
            userGameService;

    @Autowired
    private SendMailTest
            sendMailTest;

    @Autowired
    private ImageGameService imageGameService;

    @Autowired
    private VoucherUserService voucherUserService;

    @Autowired
    private VoucherGameService voucherGameService;





    @GetMapping("/staff/list")
    public String listStaff(Model model) {
        List<Admin> staffList = adminSevice.findByRole("STAFF");
        model.addAttribute("staffList", staffList);
        return "ADMIN/ListStaff";
    }

    @GetMapping("/staff/form")
    public String staffForm(@RequestParam(required = false) UUID id,
                            Model model) {

        Admin staff;
        String mode;

        if (id != null) {
            staff = adminSevice.findByAdminid(id);
            if (staff == null) {
                return "redirect:/welcomeAdmin/staff/list";
            }
            mode = "edit";
        } else {
            staff = new Admin();
            mode = "create";
        }

        model.addAttribute("staff", staff);
        model.addAttribute("mode", mode);
        return "ADMIN/EditStaff";
    }


    @PostMapping("/staff/save")
    public String saveStaff(@RequestParam(required = false) UUID id,
                            @RequestParam("username") String username,
                            @RequestParam(required = false, name = "rawPassword") String rawPassword,
                            @RequestParam(required = false, name = "status") String status,
                            RedirectAttributes ra) {

        Admin staff = (id != null) ? adminSevice.findByAdminid(id) : new Admin();
        if (staff == null) staff = new Admin();

        staff.setAdminName(username.trim());
        staff.setRole("STAFF");

        if (staff.getDateCreateAcc() == null) {
            staff.setDateCreateAcc(LocalDateTime.now());
        }

        String st = (status == null || status.isBlank()) ? "ACTIVE" : status.trim().toUpperCase();
        staff.setStatus(st);

        if (rawPassword != null && !rawPassword.isBlank()) {
            staff.setPassword(passwordEncoder.encode(rawPassword.trim()));
        }

        adminSevice.save(staff);

        ra.addFlashAttribute("ok",
                (id == null) ? "Tạo staff thành công" : "Cập nhật staff thành công");

        return "redirect:/welcomeAdmin/staff/list";
    }


    @PostMapping("/staff/toggle-status")
    public String toggleStaffStatus(@RequestParam UUID id,
                                    RedirectAttributes ra) {
        Admin staff = adminSevice.findByAdminid(id);
        if (staff != null && "STAFF".equalsIgnoreCase(staff.getRole())) {
            String current = staff.getStatus() == null ? "" : staff.getStatus().toUpperCase();
            if ("DISABLED".equals(current)) {
                staff.setStatus("ACTIVE");
                ra.addFlashAttribute("ok", "Đã kích hoạt lại tài khoản staff.");
            } else {
                staff.setStatus("DISABLED");
                ra.addFlashAttribute("ok", "Đã vô hiệu hóa tài khoản staff.");
            }
            adminSevice.save(staff);
        }
        return "redirect:/welcomeAdmin/staff/form?id=" + id;
    }

    @PostMapping("/staff/reset-password")
    public String resetStaffPassword(@RequestParam UUID id,
                                     @RequestParam String newPassword,
                                     RedirectAttributes ra) {
        Admin staff = adminSevice.findByAdminid(id);
        if (staff == null || !"STAFF".equalsIgnoreCase(staff.getRole())) {
            ra.addFlashAttribute("error", "Không tìm thấy staff.");
            return "redirect:/welcomeAdmin/staff/list";
        }

        if (newPassword == null || newPassword.isBlank()) {
            ra.addFlashAttribute("error", "Mật khẩu trống. Hãy bấm Shuffle trước khi Apply.");
            return "redirect:/welcomeAdmin/staff/form?id=" + id;
        }

        staff.setPassword(passwordEncoder.encode(newPassword.trim()));
        adminSevice.save(staff);


        ra.addFlashAttribute("ok", "Đã đặt lại mật khẩu cho staff. Hãy gửi mật khẩu mới cho nhân viên.");
        return "redirect:/welcomeAdmin/staff/form?id=" + id;
    }



}
