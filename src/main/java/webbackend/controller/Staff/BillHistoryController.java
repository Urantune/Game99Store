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
public class BillHistoryController {

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

    @GetMapping("/bill-history")
    public String billHistory(Model model) {
        // lịch sử các bill đã thanh toán
        List<UserGame> list = userGameRepository
                .findByStatusOrderByPurchaseDateDesc("owned");
        model.addAttribute("userGames", list);
        return "STAFF/BillHistory";
    }

}
