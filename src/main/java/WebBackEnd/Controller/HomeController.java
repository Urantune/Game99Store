package WebBackEnd.Controller;

import WebBackEnd.model.Entity.User;
import WebBackEnd.repository.UserRepository;
import WebBackEnd.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping(value = "/welcome")
public class HomeController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    @GetMapping
    public String homepage(Model model) {
        if (!model.containsAttribute("showForm")) {
            model.addAttribute("showForm", "");
        }
        return "HTML/Index";
    }

    @GetMapping("/test")
    public String homepage2(Model model) {
        return "HTML/seat.html";
    }


    @GetMapping("/Cart")
    public String payMent(Model model) {
        return "HTML/Cart.html";
    }



//    @PostMapping("/register")
//    public String register(User user, Model model) {
//
//        user.setId(UUID.randomUUID().toString());
//
//        user.setScore(0);
//        user.setStatus(0);
//        user.setDateCreateAccount(LocalDate.now());
//
//
//        userRepository.save(user);
//
//
//        model.addAttribute("registerSuccess", "Đăng ký thành công! Hãy đăng nhập.");
//        return "redirect:/welcome";
//    }


    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes ra,
            HttpSession session) {

        var user = userService.findByUsername(username);

        if (user == null) {
            ra.addFlashAttribute("showForm", "login");
            ra.addFlashAttribute("loginError", "Tài khoản không tồn tại!");
            return "redirect:/welcome";
        }

        if (!password.equals(user.getPassword())) {
            ra.addFlashAttribute("showForm", "login");
            ra.addFlashAttribute("loginError", "Sai mật khẩu!");
            ra.addFlashAttribute("enteredUsername", username);
            return "redirect:/welcome";
        }


        session.setAttribute("username", username);
        return "redirect:/welcome";
    }





    @GetMapping("/about")
    public String controllAbout(Model model) {
        return "HTML/About";
    }

    @GetMapping("/Newgame")
    public String newgame(Model model) {
        return "HTML/NewGame";
    }

    @GetMapping("/buyguide")
    public String buyguide() {
        return "HTML/BuyGuide";
    }

    @GetMapping("/privacypolicy")
    public String privacypolicy() {
        return "HTML/PrivacyPolicy";
    }

    @GetMapping("/termsofservice")
    public String termsofservice() {
        return "HTML/TermsOfService";
    }


//    @PostMapping("/home")
//    public String doLogin(@RequestParam("username") String username,
//                          @RequestParam("password") String password,
//                          Model model) {
//        if ("admin".equals(username) && "123".equals(password)) {
//            model.addAttribute("title", "Xin chào " + username) ;
//            return "home/welcome";
//        } else {
//            model.addAttribute("title", "Sai username hoặc password");
//            return "home/welcome";
//        }
//    }





}
