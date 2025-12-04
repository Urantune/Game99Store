package webbackend.controller.Admin;

import webbackend.entity.*;
import webbackend.SucDat.GameCore;
import webbackend.SucDat.SendMailTest;
import webbackend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping(value = "/welcomeAdmin")
public class VoucherManageController {

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




    @GetMapping("/listvoucher")
    public String listVoucher(Model model) {
        model.addAttribute("vouncherList", vouncherService.findAll());
        model.addAttribute("now", LocalDateTime.now());
        return "ADMIN/listVoucher";
    }


    @GetMapping("/voucher/form")
    public String form(@RequestParam(required = false) UUID id,
                       Model model) {
        Vouncher v;
        String mode;

        if (id != null) {
            v = vouncherService.findByUuid(id);
            if (v == null) return "redirect:/welcomeAdmin/listvoucher";
            mode = "edit";
        } else {
            v = new Vouncher();
            mode = "create";
        }


        List<Users> allUsers = userService.findAll();
        List<Game> allGames = gameSevice.findAllGame();


        List<UUID> selectedUserIds = voucherUserService
                .getVoucherUserByVouncher(v)
                .stream()
                .map(vu -> vu.getUser().getId())
                .toList();


        List<UUID> selectedGameIds = voucherGameService
                .getVoucherGameByVouncher(v)
                .stream()
                .map(vg -> vg.getGame().getGameId())
                .toList();

        model.addAttribute("voucher", v);
        model.addAttribute("mode", mode);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("allGames", allGames);
        model.addAttribute("selectedUserIds", selectedUserIds);
        model.addAttribute("selectedGameIds", selectedGameIds);

        return "ADMIN/CUVouncher";
    }



    @GetMapping("/voucher/info")
    public String voucherInfo(@RequestParam UUID id, Model model,
                              RedirectAttributes ra) {

        Vouncher v = vouncherService.findByUuid(id);
        if (v == null) {
            ra.addFlashAttribute("error", "Không tìm thấy voucher");
            return "redirect:/welcomeAdmin/listvoucher";
        }

        List<VoucherUser> vu = voucherUserService.getVoucherUserByVouncher(v);
        List<VoucherGame> vg = voucherGameService.getVoucherGameByVouncher(v);

        model.addAttribute("voucher", v);
        model.addAttribute("users", vu);
        model.addAttribute("games", vg);

        return "ADMIN/VoucherInfo";
    }


    @GetMapping("/voucher/delete")
    public String delete(@RequestParam UUID id, RedirectAttributes ra) {
        Vouncher v = vouncherService.findByUuid(id);
        if (v != null) {

            v.setDate_end(LocalDateTime.now().minusSeconds(1));
            vouncherService.save(v);
            ra.addFlashAttribute("ok", "Đã vô hiệu hóa voucher (hết hạn)");
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy voucher");
        }
        return "redirect:/welcomeAdmin/listvoucher";
    }


    @PostMapping("/voucher/save")
    public String save(@RequestParam(required = false) UUID voucherId,
                       @RequestParam String name,
                       @RequestParam double sale,
                       @RequestParam String type,
                       @RequestParam("date_start")
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart,
                       @RequestParam("date_end")
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd,

                       // NEW: list user + game
                       @RequestParam(required = false, name = "userIds") List<UUID> userIds,
                       @RequestParam(required = false, name = "gameIds") List<UUID> gameIds,

                       RedirectAttributes ra) {

        Vouncher v = (voucherId != null)
                ? vouncherService.findByUuid(voucherId)
                : new Vouncher();
        if (v == null) v = new Vouncher();

        v.setName(name);
        v.setSale(sale);
        v.setType(type);
        v.setDate_start(LocalDateTime.of(dateStart, LocalTime.MIN));
        v.setDate_end(LocalDateTime.of(dateEnd, LocalTime.of(23, 59, 59)));


        vouncherService.save(v);


        voucherUserService.updateUsersForVoucher(v, userIds);


        voucherGameService.updateGamesForVoucher(v, gameIds);

        ra.addFlashAttribute("ok",
                voucherId == null ? "Tạo thành công" : "Cập nhật thành công");
        return "redirect:/welcomeAdmin/listvoucher";
    }




}
