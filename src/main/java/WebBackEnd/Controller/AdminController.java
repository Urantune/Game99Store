package WebBackEnd.Controller;

import WebBackEnd.Entity.*;
import WebBackEnd.SucDat.GameCore;
import WebBackEnd.SucDat.SendMailTest;
import WebBackEnd.service.*;

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


    @GetMapping({"", "/"})
    public String homeAdmin(Model model,
                            HttpSession session) {

        Admin admin = (Admin) session
                .getAttribute("admin");

        return "ADMIN/IndexAdmin";
    }


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


    @GetMapping("/dunglai")
    public String check(Model model) {
        return "HTML/hehe";
    }


    @GetMapping("/listgame")
    public String editGame(Model model) {
        model
                .addAttribute("listGame"
                        , gameSevice
                                .findAllGame());
        return "ADMIN/ListGame";
    }

    @GetMapping("/editgame/{id}")
    public String editGame(@PathVariable UUID id
            , Model model) {
        Game game = gameSevice.findById(id);
        model
                .addAttribute("game"
                        , game);
        return "ADMIN/EditGame";
    }

    @PostMapping("/game/{id}/set-main")
    public String setMainGame(@PathVariable
                              UUID id) {

        List<Game> all = gameSevice.findAllGame();

        for (Game g : all) {
            if (g.getGameId()
                    .equals(id)) {
                g.setStatus("main");
            } else {
                if (!g.getStatus()
                        .equals("DELISTED")) {
                    g.setStatus("activate");
                }
            }
            gameSevice.save(g);
        }

        return "redirect:/welcomeAdmin/listgame";
    }

    @PostMapping("/editgame/{id}")
    public String updateGame(@PathVariable UUID id,
                             @RequestParam String gameName,
                             @RequestParam String price,
                             @RequestParam String version,
                             @RequestParam String category,
                             @RequestParam String status,
                             @RequestParam(required = false
                                     , name = "img_video") String imgVideo,
                             @RequestParam(required = false
                                     , name = "img_1") String img1,
                             @RequestParam(required = false
                                     , name = "img_2") String img2,
                             @RequestParam(required = false
                                     , name = "img_3") String img3,
                             @RequestParam(required = false
                                     , name = "img_cover") String imgCover,
                             @RequestParam(required = false
                                     , name = "dec_1") String dec1,
                             @RequestParam(required = false
                                     , name = "dec_2") String dec2,
                             @RequestParam(required = false
                                     , name = "dec_3") String dec3,
                             @RequestParam(required = false
                                     , name = "dec_4") String dec4,
                             @RequestParam(required = false
                                     , name = "dec_5") String dec5,
                             RedirectAttributes ra) {

        Game g = gameSevice.findById(id);

        g.setGameName(gameName);

        String p = price == null ? "" : price
                .replace(","
                        , "")
                .trim();
        if (!p.isEmpty()) g.setPrice(Double.parseDouble(p));

        g.setGame_version(version);
        g.setStatus(status);
        g.setGameCategory(category);

        java.util.function.Function<String, String> norm = s -> {
            if (s == null) return "";
            String t = s.trim();
            if (t.startsWith("/")) t = t.substring(1);
            return t;
        };

        java.util.List<String> medias = java
                .util
                .Arrays
                .asList(
                        norm.apply(imgVideo),
                        norm.apply(img1),
                        norm.apply(img2),
                        norm.apply(img3),
                        norm.apply(imgCover)
                );

        g.setImageLinks(String.join("||", medias));

        java.util.List<String> parts = new java.util.ArrayList<>();
        for (String s : new String[]{dec1
                , dec2
                , dec3
                , dec4
                , dec5})
            if (s != null && !s.trim().isEmpty()) parts.add(s.trim());

        g.setDeception(String.join("||", parts));

        gameSevice.save(g);
        ra.addFlashAttribute("ok", "Saved");
        return "redirect:/welcomeAdmin/editgame/" + id;
    }


    @PostMapping("/deletegame")
    public String deleteGame(@RequestParam("id") UUID id) {
        Game g = gameSevice.findById(id);
        userGameService.DeleteByGame(g);
        gameSevice.deleteGame(id);
        return "redirect:/welcomeAdmin/listgame";
    }


    @GetMapping("/upload")
    public String upload(Model model) {
        return "ADMIN/UploadGame";
    }


    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> uploadGame(
            @RequestParam String gameName,
            @RequestParam double price,
            @RequestParam String category,
            @RequestParam(required = false) String version,

            @RequestParam(value = "description"
                    , required = false) String description,
            @RequestParam(value = "dec_1"
                    , required = false) String dec1,
            @RequestParam(value = "dec_2"
                    , required = false) String dec2,
            @RequestParam(value = "dec_3"
                    , required = false) String dec3,
            @RequestParam(value = "dec_4"
                    , required = false) String dec4,
            @RequestParam(value = "dec_5"
                    , required = false) String dec5,

            @RequestParam(value = "img_video"
                    , required = false) MultipartFile imgVideo,
            @RequestParam(value = "img_1"
                    , required = false) MultipartFile img1,
            @RequestParam(value = "img_2"
                    , required = false) MultipartFile img2,
            @RequestParam(value = "img_3"
                    , required = false) MultipartFile img3,
            @RequestParam(value = "img_cover"
                    , required = false) MultipartFile imgCover,

            @RequestParam("packageFile") MultipartFile packageFile
    ) {
        try {
            if (packageFile == null
                    || packageFile.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("Vui lòng chọn file game");
            }

            String mainDesc = (description != null
                    && !description.isBlank())
                    ? description
                    : (dec1 != null ? dec1 : "");

            StringBuilder deception = new StringBuilder();
            if (mainDesc != null) deception
                    .append(mainDesc.trim());
            if (dec2 != null && !dec2.isBlank()) deception
                    .append("||")
                    .append(dec2.trim());
            if (dec3 != null && !dec3.isBlank()) deception
                    .append("||")
                    .append(dec3.trim());
            if (dec4 != null && !dec4.isBlank()) deception
                    .append("||")
                    .append(dec4.trim());
            if (dec5 != null && !dec5.isBlank()) deception
                    .append("||")
                    .append(dec5.trim());

            String packagePath = gameCore.saveGamePackage(packageFile
                    , category
                    , gameName);

            String video = (imgVideo != null
                    && !imgVideo.isEmpty())
                    ? gameCore.saveToFolderKeepName(imgVideo, "videos") : "";

            String i1 = (img1 != null
                    && !img1.isEmpty())
                    ? gameCore.saveToFolderKeepName(img1, "static/img") : "";

            String i2 = (img2 != null
                    && !img2.isEmpty())
                    ? gameCore.saveToFolderKeepName(img2, "static/img") : "";

            String i3 = (img3 != null
                    && !img3.isEmpty())
                    ? gameCore.saveToFolderKeepName(img3, "static/img") : "";

            String cover = (imgCover != null
                    && !imgCover.isEmpty())
                    ? gameCore.saveToFolderKeepName(imgCover, "static/img") : "";

            String imageLinks = String.join("||"
                    , video
                    , i1
                    , i2
                    , i3
                    , cover);

            Game g = new Game();
            g.setGameName(gameName);
            g.setPrice(price);
            g.setGameCategory(category);
            g.setGame_version(version);
            g.setStatus("coming soon");
            g.setLocate_game(packagePath);
            g.setDeception(deception.toString());
            g.setImageLinks(imageLinks);

            gameSevice.save(g);
            return ResponseEntity
                    .ok("Upload thành công!");
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body("Server error: "
                            + e.getMessage());
        }
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
                , admin.getAdmin_id());
        session.setAttribute("adminName"
                , admin.getAdminName());
        return ResponseEntity.ok(Map.of("success"
                , true));
    }


    @GetMapping("/listvoucher")
    public String listVoucher(Model model) {
        model.addAttribute("vouncherList"
                , vouncherService.findAll());
        return "ADMIN/listVoucher";
    }

    @GetMapping("/voucher/form")
    public String form(@RequestParam(required = false) UUID id,
                       Model model) {
        if (id != null) {
            Vouncher v = vouncherService.findByUuid(id);
            if (v == null) return "redirect:/welcomeAdmin/listvoucher";
            model.addAttribute("voucher"
                    , v);
            model.addAttribute("mode"
                    , "edit");
        } else {
            model.addAttribute("voucher"
                    , new Vouncher());
            model.addAttribute("mode"
                    , "create");
        }
        return "ADMIN/CUVouncher";
    }

    @GetMapping("/voucher/delete")
    public String delete(@RequestParam UUID id
            , RedirectAttributes ra) {
        vouncherService.deleteById(id);
        ra.addFlashAttribute("ok"
                , "Xóa thành công");
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
                       RedirectAttributes ra) {

        Vouncher v = (voucherId != null)
                ? vouncherService.findByUuid(voucherId)
                : new Vouncher();
        if (v == null) v = new Vouncher();

        v.setName(name);
        v.setSale(sale);
        v.setType(type);
        v.setDate_start(LocalDateTime.of(dateStart
                , LocalTime.MIN));
        v.setDate_end(LocalDateTime.of(dateEnd
                , LocalTime.of(23
                        , 59
                        , 59)));

        vouncherService.save(v);

        ra.addFlashAttribute("ok"
                , voucherId == null
                        ? "Tạo thành công"
                        : "Cập nhật thành công");
        return "redirect:/welcomeAdmin/listvoucher";
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
