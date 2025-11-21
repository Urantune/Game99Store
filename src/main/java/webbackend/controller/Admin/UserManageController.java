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
public class UserManageController {

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



    @GetMapping("/listuser")
    public String editUser(Model model) {
        model
                .addAttribute("listUser",
                        userService.findAll());
        return "ADMIN/ListUser";
    }


    @GetMapping("/edituser/{id}")
    public String editUser(@PathVariable UUID id,
                           Model model) {
        User user = userService.findById(id);
        model
                .addAttribute("user",
                        user);
        model
                .addAttribute("id",
                        id);
        return "ADMIN/EditUser";
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetPass(@PathVariable UUID id,
                            @RequestParam String newPassword,
                            RedirectAttributes ra) {
        User user = userService.findById(id);
        if (user == null) {
            ra
                    .addFlashAttribute("error", "Không tìm thấy người dùng.");
            return "redirect:/welcomeAdmin/listuser";
        }

        if (newPassword == null || newPassword.isBlank()) {
            ra
                    .addFlashAttribute("error", "Mật khẩu trống. Hãy bấm Shuffle trước khi Apply.");
            return "redirect:/welcomeAdmin/edituser/" + id;
        }


        user.setPassword(passwordEncoder
                .encode(newPassword
                        .trim()));
        userService
                .save(user);


        String title = "Mật khẩu mới của bạn";
        String content = "<p>Xin chào <b>" + user
                .getUsername() + "</b>,</p>"
                + "<p>Mật khẩu mới của bạn là: <b>"
                + newPassword
                + "</b></p>"
                + "<p>Vui lòng đăng nhập và đổi lại mật khẩu sau khi vào hệ thống.</p>";
        sendMailTest.testSend(user.getEmail(), title, content);

        ra.addFlashAttribute("success"
                , "Đã đặt lại mật khẩu và gửi email cho "
                        + user.getEmail());
        return "redirect:/welcomeAdmin/edituser/" + id;
    }

    @PostMapping("/ban")
    public String banAccount(@RequestParam UUID id) {
        User u = userService.findById(id);

        if (u.getStatus()
                .equalsIgnoreCase("active")) {
            u.setStatus("banned");
            userService.save(u);
        } else if (u.getStatus()
                .equalsIgnoreCase("banned")) {
            u.setStatus("active");
            userService.save(u);
        }
        return "redirect:/welcomeAdmin/edituser/" + id;
    }

    @PostMapping("/deleteuser")
    public String deleteUser(@RequestParam UUID id
            , RedirectAttributes ra) {
        userGameService.DeleteByUser(userService
                .findById(id));
        userService.deleteById(id);
        ra.addFlashAttribute("ok"
                , "Đã xóa người dùng");
        return "redirect:/welcomeAdmin/listuser";
    }


    @PostMapping("/edituser/{id}")
    public String updateUser(@PathVariable UUID id,
                             @ModelAttribute("user") User form,
                             RedirectAttributes ra) {
        User u = userService.findById(id);
        u.setUsername(form.getUsername());
        u.setEmail(form.getEmail());
        userService.save(u);
        ra.addFlashAttribute("ok"
                , "Đã lưu thay đổi");

        return "redirect:/welcomeAdmin/edituser/" + id;
    }
}
