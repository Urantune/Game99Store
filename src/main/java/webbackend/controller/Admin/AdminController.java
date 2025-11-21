package webbackend.controller.Admin;

import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
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
public class AdminController {

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




    @GetMapping({"", "/"})
    public String homeAdmin(Model model,
                            HttpSession session) {

        Admin admin = (Admin) session
                .getAttribute("admin");

        return "ADMIN/IndexAdmin";
    }




    @GetMapping("/dunglai")
    public String check(Model model) {
        return "HTML/hehe";
    }






    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam("username") String adminname,
                                   @RequestParam("password") String password,
                                   HttpSession session) {

        var admin = adminSevice.findByUsername(adminname);
        if (admin == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error"
                            , "Tài khoản không tồn tại!"));
        }
        if (!passwordEncoder.matches(password
                , admin.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error"
                            , "Sai mật khẩu!"));
        }

        session.setAttribute("id"
                , admin.getAdminid());
        session.setAttribute("adminName"
                , admin.getAdminName());
        return ResponseEntity.ok(Map.of("success"
                , true));
    }













    private String nvl(String s) {
        return s == null
                ? ""
                : s.trim();
    }

    private boolean hasText(String s) {
        return s != null
                && !s.trim()
                .isEmpty();
    }

    private String join2(String a, String b) {
        return nvl(a) + "||" + nvl(b);
    }

    private String join3(String a, String b, String c) {
        return nvl(a) + "||" + nvl(b) + "||" + nvl(c);
    }

}
