package WebBackEnd.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/welcome")
public class WebInforController {
    @GetMapping("/news")
    public String news(Model model) {
        return "HTML/news";
    }

    @GetMapping("/test")
    public String homepage2(Model model) {
        return "HTML/seat.html";
    }

    @GetMapping("/about")
    public String controllAbout(Model model) {
        return "HTML/About";
    }

//    @GetMapping("/refundGame")
//    public String refundGame() {
//        return "HTML/RefundGame";
//    }

    @GetMapping("/buyguide")
    public String buyguide() {
        return "HTML/BuyGuide";
    }

    @GetMapping("/privacypolicy")
    public String privacypolicy() {
        return "HTML/PrivacyPolicy";
    }

    @GetMapping("/support")
    public String support(Model model) {
        return "HTML/Support";
    }

    @GetMapping("/supporttransaction")
    public String supporttransaction(Model model) {
        return "HTML/SupportTransaction";
    }

    @GetMapping("/termsofservice")
    public String termsofservice() {
        return "HTML/TermsOfService";
    }
}
