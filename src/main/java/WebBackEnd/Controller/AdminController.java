package WebBackEnd.Controller;


import WebBackEnd.Entity.Game;
import WebBackEnd.Entity.User;
import WebBackEnd.service.AdminSevice;
import WebBackEnd.service.GameSevice;
import WebBackEnd.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(value="/welcomeAdmin")
public class AdminController {


    @Autowired
    private AdminSevice adminSevice;

    @Autowired
    private UserService userService;

    @Autowired
    private GameSevice  gameSevice;

    public String homeAdmin(Model model) {
        return "admin";
    }

    @GetMapping("/edituser")
    public String editUser(Model model) {
        model.addAttribute("listUser",userService.findAll());
        return "login";
    }

    @PostMapping("/edituser/{id}")
    public String editUser(@PathVariable(value="user") User user, Model model) {
        userService.save(user);
        return "login";
    }

    @GetMapping("/dunglai")
    public String check(Model model) {
        return "HTML/hehe";
    }

    @GetMapping("/editgame")
    public String editGame(Model model) {
        model.addAttribute("listGame",gameSevice.findAllGame());
        return "editgame";
    }

    public String editGame(@PathVariable(value="game") Game game,Model model){
        gameSevice.saveGame(game);
        return "editgame";
    }

    @GetMapping
    public String welcomeAmin(Model model){
        return "ADMIN/IndexAdmin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session) {
        var user = userService.findByUsername(username);
        if (user == null) return ResponseEntity.badRequest().body(Map.of("error","Tài khoản không tồn tại!"));
        if (!password.equals(user.getPassword())) return ResponseEntity.badRequest().body(Map.of("error","Sai mật khẩu!"));
        if ("wait".equals(user.getStatus())) return ResponseEntity.badRequest().body(Map.of("error","Tài khoảng chưa được kích hoạt"));

        session.setAttribute("id", user.getId());
        session.setAttribute("username", user.getUsername());
        // Nếu muốn check đúng admin mới vào tool, thêm:
        // session.setAttribute("role", "ADMIN");
        return ResponseEntity.ok(Map.of("success", true));
    }



}
