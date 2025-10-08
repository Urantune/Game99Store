package WebBackEnd.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/welcome")
public class    HomeController {


    @GetMapping
    public String homepage(Model model) {
     //   model.addAttribute("title", "Hehehe");a
        return "HTML/Index";
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
