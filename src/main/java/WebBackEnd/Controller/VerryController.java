package WebBackEnd.Controller;


import WebBackEnd.Entity.User;
import WebBackEnd.Entity.UserGame;
import WebBackEnd.service.*;
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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/veryAccount")
public class VerryController {


    @Autowired
    private DetailService  detailService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGameService userGameService;
    @Autowired
    private GameSevice gameSevice;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Map<String, Boolean> PAY_STATE = new ConcurrentHashMap<>();


    public VerryController(DetailService detailService, UserService userService) {
        this.detailService = detailService;
        this.userService = userService;
    }

    @GetMapping("/done/{id}/{code}")
    public String veryAccount(@PathVariable UUID id, @PathVariable String code, Model model)
    {

        String input = "wait"+ id;
        String fi;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            fi= sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println(fi);

        User user = userService.findById(id);

        String[] w = user.getStatus().split("\\|\\|");
        if(w[0].equalsIgnoreCase("wait"))
        {
            if(user.getId().toString().equalsIgnoreCase(id.toString()))
            {
                if(code.equalsIgnoreCase(fi)){
                    model.addAttribute("user",user);
                    model.addAttribute("idUser",id);
                    model.addAttribute("codeUser",fi);
                    return "HTML/VeryFileSend";
                }
            }
        }
            return "HTML/About";
    }
    @PostMapping("/done/{id}/{code}")
    public String doneAccount(@PathVariable(value = "id")UUID id, @PathVariable(value = "code")String code,Model model)
    {
        User user=userService.findById(id);
        user.setStatus("active");
        userService.save(user);

        return "HTML/About";
    }










    @GetMapping("/donePass/{id}/{code}")
    public String veryPassAccount(@PathVariable UUID id, @PathVariable String code, Model model)
    {

        String input = "wait"+ id;
        String fi;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            fi= sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println(fi);

        User user = userService.findById(id);
        String[] w = user.getStatus().split("\\|\\|");
        if(w[0].equalsIgnoreCase("changePass"))
        {
            if(user.getId().toString().equalsIgnoreCase(id.toString()))
            {
                if(code.equalsIgnoreCase(fi)){
                    model.addAttribute("user",user);
                    model.addAttribute("idUser",id);
                    model.addAttribute("codeUser",fi);
                    return "HTML/ForgotPasword";
                }
            }
        }
        return "HTML/About";
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

        User user = userService.findById(id);

        if (user == null) {
            ra.addFlashAttribute("error", "Tài khoản không tồn tại");
            return "redirect:/welcome";
        }


        user.setPassword(passwordEncoder.encode(newPassword));
        user.setStatus("active");

        userService.save(user);

        ra.addFlashAttribute("success", "Đổi mật khẩu thành công");
        return "redirect:/welcome";
    }



    @PostMapping("/checkaccount")
    @ResponseBody
    public String checkAccount(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam UUID gameUuid) {
        User u = userService.findByUsername(username);
        if (u != null
                && "active".equalsIgnoreCase(u.getStatus())
                && passwordEncoder.matches(password, u.getPassword())) {
            UserGame userGame = userGameService.findByGameAndUser(gameSevice.findGameById(gameUuid), u);

            if(userGame != null&&userGame.getStatus()==1){
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



    // (tuỳ chọn) trang success
    @GetMapping("/success")
    public String success() {
        return "HTML/Success"; // file bạn đã đưa
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/pay/confirm", produces = "application/json")
    @ResponseBody
    public Map<String, Object> confirm(@RequestParam String pid) {
        PAY_STATE.put(pid, true);
        return Map.of("status", "ok");
    }


    // Desktop poll -> hỏi theo pid
    @CrossOrigin(origins = "*")
    @GetMapping("/pay/status")
    @ResponseBody
    public Map<String, Object> status(@RequestParam String pid) {
        boolean paid = PAY_STATE.getOrDefault(pid, false);
        return Map.of("paid", paid);
    }

    // Reset trước khi bắt đầu 1 lần quét mới (tránh dính trạng thái cũ)
    @CrossOrigin(origins = "*")
    @GetMapping("/pay/reset")
    @ResponseBody
    public Map<String, Object> reset(@RequestParam String pid) {
        PAY_STATE.remove(pid);
        return Map.of("status", "reset");
    }

}
