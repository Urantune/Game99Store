package webbackend.controller.User;

import jakarta.servlet.http.HttpSession;
import webbackend.entity.Users;
import webbackend.entity.UserGame;
import webbackend.service.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/veryAccount")
public class VerryController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserGameService userGameService;
    @Autowired
    private GameSevice gameSevice;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Map<String, Boolean> PAY_STATE = new ConcurrentHashMap<>();

    @GetMapping("/done/{id}/{code}")
    public String veryAccount(@PathVariable UUID id, @PathVariable String code, Model model) {

        String input = "wait" + id;
        String fi;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            fi = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Users user = userService.findById(id);
        if (user == null) {
            System.out.println("OK");
            return "HTML/About";
        }

        if (!"wait".equalsIgnoreCase(user.getStatus())) {
            System.out.println("OK1");
            return "HTML/About";
        }

        if (user.getExpirationDate() == null ||
                user.getExpirationDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Link kích hoạt tài khoản đã hết hạn. Vui lòng đăng ký lại.");
            System.out.println("OK2");
            return "HTML/About";
        }

        if (!code.equalsIgnoreCase(fi)) {
            System.out.println("OK3");
            return "HTML/About";
        }

        model.addAttribute("user", user);
        model.addAttribute("idUser", id);
        model.addAttribute("codeUser", fi);
        return "HTML/VeryFileSend";
    }

    @PostMapping("/done/{id}/{code}")
    public String doneAccount(@PathVariable UUID id,
                              @PathVariable String code,
                              Model model) {
        Users user = userService.findById(id);
        if (user == null) {
            return "HTML/About";
        }
        user.setStatus("active");
        user.setExpirationDate(null);
        userService.save(user);
        return "HTML/About";
    }

    @GetMapping("/donePass/{id}/{code}")
    public String veryPassAccount(@PathVariable UUID id,
                                  @PathVariable String code,
                                  Model model) {

        String input = "wait" + id;
        String fi;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            fi = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Users user = userService.findById(id);
        if (user == null) {
            return "HTML/About";
        }

        if (!"changePass".equalsIgnoreCase(user.getStatus())) {
            return "HTML/About";
        }

        if (user.getExpirationDate() == null ||
                user.getExpirationDate().isBefore(LocalDateTime.now())) {
            user.setStatus("active");
            user.setExpirationDate(null);
            userService.save(user);
            model.addAttribute("error", "Link đổi mật khẩu đã hết hạn. Vui lòng yêu cầu lại.");
            return "HTML/About";
        }

        if (!code.equalsIgnoreCase(fi)) {
            return "HTML/About";
        }

        model.addAttribute("user", user);
        model.addAttribute("idUser", id);
        model.addAttribute("codeUser", fi);
        return "HTML/ForgotPasword";
    }

    @PostMapping("/donePass")
    public String donePassAccount(@RequestParam UUID id,
                                  @RequestParam String code,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  RedirectAttributes ra) {

        if (newPassword == null || newPassword.length() < 8) {
            ra.addFlashAttribute("error", "Mật khẩu phải ≥ 8 ký tự");
            return "redirect:/veryAccount/donePass/" + id + "/" + code;
        }
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Mật khẩu nhập lại không khớp");
            return "redirect:/veryAccount/donePass/" + id + "/" + code;
        }

        Users user = userService.findById(id);
        if (user == null) {
            ra.addFlashAttribute("error", "Tài khoản không tồn tại");
            return "redirect:/welcome";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setStatus("active");
        user.setExpirationDate(null);
        userService.save(user);

        ra.addFlashAttribute("success", "Đổi mật khẩu thành công");
        return "redirect:/welcome";
    }

    @PostMapping("/checkaccount")
    @ResponseBody
    public String checkAccount(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam UUID gameUuid) {
        Users u = userService.findByUsername(username);
        if (u != null
                && "active".equalsIgnoreCase(u.getStatus())
                && passwordEncoder.matches(password, u.getPassword())) {
            UserGame userGame = userGameService.findByGameAndUser(gameSevice.findGameById(gameUuid), u);
            if (userGame != null && userGame.getStatus().equalsIgnoreCase("owned")) {
                return "OK";
            }
        }
        return "FAIL";
    }

    @RestController
    @RequestMapping("/qr")
    public static class QrController {

        private QrService qrService;

        @PostConstruct
        void init() {
            this.qrService = new QrService();
        }

        @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
        public @ResponseBody byte[] qr(
                @RequestParam String idpayment,
                @RequestParam(defaultValue = "256") int size) {

            int s = Math.max(64, Math.min(size, 1024));
            return qrService.generatePng(idpayment, s);
        }

        @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
        public void download(
                @RequestParam String idpayment,
                @RequestParam(defaultValue = "256") int size,
                HttpServletResponse resp) throws IOException {

            byte[] png = qr(idpayment, size);
            resp.setHeader("Content-Disposition", "attachment; filename=\"qr.png\"");
            resp.getOutputStream().write(png);
        }
    }

    @GetMapping("/test")
    public String test() {
        return "HTML/TestVery";
    }

    @GetMapping("/success")
    public String success() {
        return "HTML/Success";
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/pay/confirm", produces = "application/json")
    @ResponseBody
    public Map<String, Object> confirm(@RequestParam String pid) {
        PAY_STATE.put(pid, true);
        return Map.of("status", "ok");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/pay/status")
    @ResponseBody
    public Map<String, Object> status(@RequestParam String pid) {
        boolean paid = PAY_STATE.getOrDefault(pid, false);
        return Map.of("paid", paid);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/pay/reset")
    @ResponseBody
    public Map<String, Object> reset(@RequestParam String pid) {
        PAY_STATE.remove(pid);
        return Map.of("status", "reset");
    }


    @GetMapping("/doneDelete/{id}/{code}")
    public String veryDeleteAccount(@PathVariable UUID id,
                                    @PathVariable String code,
                                    Model model) {

        String input = "deleting" + id;
        String fi;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            fi = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Users user = userService.findById(id);
        System.out.println("OK");
        if (user == null) return "HTML/About";

        if (!"deleting".equalsIgnoreCase(user.getStatus())) {
            System.out.println("OK2");
            return "HTML/About";
        }

        if (user.getExpirationDate() == null ||
                user.getExpirationDate().isBefore(LocalDateTime.now())) {

            user.setStatus("active");
            user.setExpirationDate(null);
            userService.save(user);
            System.out.println("OK4");
            model.addAttribute("error", "Link xoá tài khoản đã hết hạn.");
            return "HTML/About";
        }

        if (!code.equalsIgnoreCase(fi)) {
            System.out.println("OK3");
            return "HTML/About";}

        model.addAttribute("user", user);
        model.addAttribute("idUser", id);
        model.addAttribute("codeUser", fi);
        return "HTML/DeleteProfile";
    }


    @PostMapping("/doneDelete")
    public String doneDeleteAccount(
            @RequestParam UUID id,
            @RequestParam String code,
            RedirectAttributes ra,
            HttpSession session) {

        Users user = userService.findById(id);
        if (user == null) return "redirect:/welcome";

        user.setStatus("deleted");
        user.setExpirationDate(null);

        String deletedUsername = user.getUsername() + "(deleted)";
        user.setUsername(deletedUsername);
        user.setEmail(String.format("deleted+%s@gamestore.local", user.getId()));




        userService.save(user);


        ra.addFlashAttribute("success", "Tài khoản đã được xoá vĩnh viễn.");
        return "redirect:/logout";
    }

}
