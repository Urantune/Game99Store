package webbackend.controller.Staff;

import webbackend.entity.*;
import webbackend.SucDat.GameCore;
import webbackend.SucDat.SendMailTest;
import webbackend.service.*;

import java.time.LocalDate;

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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Controller
@RequestMapping(value = "/welcomeStaff")
public class StaffController {

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
    private ImageGameService
            imageGameService;



    @GetMapping({"", "/"})
    public String homeAdmin(Model model,
                            HttpSession session) {



        return "STAFF/IndexStaff";
    }

    @GetMapping("/billManagement")
    public String billManagement(Model model,
                                    HttpSession session) {
        return "STAFF/BillManager";
    }



}
