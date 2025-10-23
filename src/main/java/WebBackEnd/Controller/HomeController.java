package WebBackEnd.Controller;

import WebBackEnd.SucDat.GameCore;
import WebBackEnd.SucDat.SendMailTest;
import WebBackEnd.model.Entity.Game;
import WebBackEnd.model.Entity.User;
import WebBackEnd.model.Entity.UserGame;
import WebBackEnd.repository.UserRepository;
import WebBackEnd.service.GameSevice;
import WebBackEnd.service.MailService;
import WebBackEnd.service.UserGameService;
import WebBackEnd.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private SendMailTest  sendMailTest;


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

    @GetMapping("/buy")
    public String buy(Model model) {
        List<Game> games = gameSevice.findAllGame();
        model.addAttribute("games", games); // KHÔNG chuyển thành chuỗi JSON
        return "HTML/Buy";
    }





    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> registerAjax(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        String username = user.getUsername();
        String email = user.getEmail();


        if (username == null || username.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được để trống");
            return response;
        }


        if (!username.matches("^[a-zA-Z0-9._]+$")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản chỉ được chứa chữ, số, dấu chấm hoặc gạch dưới");
            return response;
        }


        if (username.startsWith(".") || username.startsWith("_") ||
                username.endsWith(".") || username.endsWith("_")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được bắt đầu hoặc kết thúc bằng dấu chấm hoặc gạch dưới");
            return response;
        }


        if (username.contains("..") || username.contains("__") ||
                username.contains("._") || username.contains("_.")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được chứa ký tự đặc biệt liên tiếp");
            return response;
        }


        if (username.length() < 3 || username.length() > 20) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản phải có độ dài từ 3 đến 20 ký tự");
            return response;
        }


        if (userRepository.existsByUsername(username)) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản đã tồn tại");
            return response;
        }



        user.setUsername(username);
        user.setEmail(email);
        user.setScore(0);
        user.setStatus("wait");
        user.setDateCreateAccount(LocalDateTime.now());
        userRepository.save(user);


        String input = "wait"+ user.getId();
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


        String title = "Xác nhận tài khoản của bạn";

        String link = "https://a7c804c1ed63.ngrok-free.app/veryAccount/done/"
                + user.getId() + "/" + fi;

        String content =
                "<p>Hãy nhấp vào liên kết dưới đây để kích hoạt tài khoản của bạn:</p>"
                        + "<p><a href=\"" + link + "\">Nhấn vào đây để kích hoạt</a></p>"
                        + "<p>Nếu không bấm được, copy link sau dán vào trình duyệt:<br>"
                        + link + "</p>";


        sendMailTest.testSend(user.getEmail(), title, content);



        response.put("status", "success");
        response.put("message", "Đăng ký thành công! Một đường link xác thực tài khoảng đã được gửi vào email của bạn .");
        return response;
    }









    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session) {

        var user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tài khoản không tồn tại!"));
        }

        if (!password.equals(user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Sai mật khẩu!"));
        }
        if(user.getStatus().equals("wait")){
            return ResponseEntity.badRequest().body(Map.of("error","Tài khoảng chưa được kích hoạt"));
        }

        session.setAttribute("id", user.getId());
        session.setAttribute("username", user.getUsername());

        return ResponseEntity.ok(Map.of("success", true));
    }



    @GetMapping("/about")
    public String controllAbout(Model model) {
        return "HTML/About";
    }

    @GetMapping("/buyguide")
    public String buyguide() {
        return "HTML/BuyGuide";
    }

    @GetMapping("/category/{product}")
    public String category(@PathVariable("product") String product, Model model) {
        List<Game> games = gameSevice.findGamesByCetagory(product);
        model.addAttribute("listGame", games);
        model.addAttribute("currentCategory", product);
        model.addAttribute("tieude",product);
        return "HTML/Category";
    }



    @GetMapping("/gamedetail/{game_id}")
    public String gameDetail( @PathVariable(value = "game_id") UUID game_id, Model model,HttpSession session) {
        Game game = gameSevice.findGameById(game_id);
        UUID user_id = (UUID) session.getAttribute("id");
        model.addAttribute("game", game);
        model.addAttribute("UserGame",userGameService.findUserGameByUserAndGame(userService.findById(user_id),gameSevice.findGameById(game_id)));
        System.out.println(userGameService.findUserGameByUserAndGame(userService.findById(user_id),gameSevice.findGameById(game_id)));
//        System.out.println(userService.findById(id));
//            model.addAttribute("user", userService.findById(id));
//        System.out.println(userService.findById(user_id).getUsername());

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

    @PostMapping("/addGameToCard")
    public String addGameToCard(){
        return "redirect:welcome/gamedetail/{game_id}";
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
