package WebBackEnd.Controller;

import WebBackEnd.SucDat.GameCore;
import WebBackEnd.model.Entity.Game;
import WebBackEnd.model.Entity.User;
import WebBackEnd.model.Entity.UserGame;
import WebBackEnd.repository.UserRepository;
import WebBackEnd.service.GameSevice;
import WebBackEnd.service.UserGameService;
import WebBackEnd.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/welcome")
public class HomeController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GameSevice gameSevice;
    @Autowired
    private UserGameService userGameService;


    @GetMapping
    public String homepage(Model model) {
        if (!model.containsAttribute("showForm")) {
            model.addAttribute("showForm", "");
        }
        model.addAttribute("gameMain", gameSevice.findGameByStatus("main"));

        model.addAttribute("listGame", gameSevice.list20GameIntoGame());
        model.addAttribute("linkimage", GameCore.imageLinkGame(gameSevice.findGameByStatus("main").getImageLinks()));


        //        for(Game a : gameSevice.list20GameIntoGame()){
//            System.out.println(a.getDeceptions()[4]);
//        }
//        UUID uid = UUID.fromString("6CE0FCF6-B584-4A63-AEDF-FAED48E78665");
//        for (Game a : userGameService.showGameInProfile(uid)) {
//            System.out.println(a.getGameName());
//        }


        return "HTML/Index";
    }

    @GetMapping("/test")
    public String homepage2(Model model) {
        return "HTML/seat.html";
    }



        @GetMapping("/Cart/{id}")
        public String payMent(@PathVariable UUID id, Model model) {
            model.addAttribute("listGame", userGameService.showGameInCart(id));
            model.addAttribute("user", userService.findById(id));
            for (Game a : userGameService.showGameInProfile(id)) {
                System.out.println(a.getGameName());
            }

            return "HTML/Cart";
        }




    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> registerAjax(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        String username = user.getUsername();
        String email = user.getEmail();

        // Rỗng hoặc khoảng trắng
        if (username == null || username.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được để trống");
            return response;
        }

        // Ký tự chữ, số, dấu chấm, gạch dưới
        if (!username.matches("^[a-zA-Z0-9._]+$")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản chỉ được chứa chữ, số, dấu chấm hoặc gạch dưới");
            return response;
        }

        // Không bắt đầu kết thúc bằng '.' hoặc '_'
        if (username.startsWith(".") || username.startsWith("_") ||
                username.endsWith(".") || username.endsWith("_")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được bắt đầu hoặc kết thúc bằng dấu chấm hoặc gạch dưới");
            return response;
        }

        // Không có ký tự đặc biệt liền nhau
        if (username.contains("..") || username.contains("__") ||
                username.contains("._") || username.contains("_.")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được chứa ký tự đặc biệt liên tiếp");
            return response;
        }

        // Độ dài
        if (username.length() < 3 || username.length() > 20) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản phải có độ dài từ 3 đến 20 ký tự");
            return response;
        }

        // Trùng username
        if (userRepository.existsByUsername(username)) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản đã tồn tại");
            return response;
        }


        user.setUsername(username);
        user.setEmail(email);
        user.setScore(0);
        user.setStatus("active");
        user.setDateCreateAccount(LocalDateTime.now());
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "Đăng ký thành công! Hãy đăng nhập.");
        return response;
    }







    @PostMapping("/login")
    public String login(@RequestParam String username,
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

        session.setAttribute("id", user.getId());
        session.setAttribute("username", user.getUsername());

        return "redirect:/welcome";
    }


    @GetMapping("/about")
    public String controllAbout(Model model) {
        return "HTML/About";
    }

    @GetMapping("/buyguide")
    public String buyguide() {
        return "HTML/BuyGuide";
    }

    @GetMapping("/gamedetail/{game_id}")
    public String gameDetail( @PathVariable(value = "game_id") UUID game_id, Model model) {
        Game game = gameSevice.findGameById(game_id);
        model.addAttribute("game", game);
//        System.out.println(userService.findById(id));
//            model.addAttribute("user", userService.findById(id));

        return "HTML/GameDetail";
    }

    @GetMapping("/Newgame")
    public String newgame(Model model) {
        return "HTML/NewGame";
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

    @GetMapping("/profile/{id}")
    public String userDetail(@PathVariable(value = "id") UUID id,
                             Model model) {
        model.addAttribute("listGame", userGameService.showGameInProfile(id));
        model.addAttribute("user", userService.findById(id));

        return "HTML/ProfileUser";
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
