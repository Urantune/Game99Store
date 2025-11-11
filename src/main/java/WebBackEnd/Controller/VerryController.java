package WebBackEnd.Controller;


import WebBackEnd.Entity.User;
import WebBackEnd.service.DetailService;
import WebBackEnd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.MessageDigest;

import java.util.UUID;

@Controller
@RequestMapping("/veryAccount")
public class VerryController {


    @Autowired
    private DetailService  detailService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        if(user.getStatus().equalsIgnoreCase("wait"))
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
        if(user.getStatus().equalsIgnoreCase("changePass"))
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




}
