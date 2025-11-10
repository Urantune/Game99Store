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
import java.util.UUID;

@Controller
@RequestMapping(value = "/welcomeAdmin")
public class AdminController {

    @Autowired private AdminSevice adminSevice;
    @Autowired private UserService userService;
    @Autowired private GameSevice gameSevice;


    @GetMapping({"", "/"})
    public String homeAdmin(Model model) {
        return "ADMIN/IndexAdmin";
    }


    @GetMapping("/listuser")
    public String editUser(Model model) {
        model.addAttribute("listUser", userService.findAll());
        return "ADMIN/ListUser";
    }


    @PostMapping("/edituser/{id}")
    public String editUser(@PathVariable("id") UUID id,
                           @ModelAttribute User user,
                           Model model) {

        user.setId(id);
        userService.save(user);
        return "ADMIN/EditUser";
    }


    @GetMapping("/dunglai")
    public String check(Model model) {
        return "HTML/hehe";
    }


    @GetMapping("/editgame")
    public String editGame(Model model) {
        model.addAttribute("listGame", gameSevice.findAllGame());
        return "ADMIN/ListGame";
    }

    @PostMapping("/editgame/{id}")
    public String editGame(@PathVariable("id") UUID id,
                           @ModelAttribute Game game,
                           Model model) {
        game.setGameId(id);
        gameSevice.saveGame(game);
        return "ADMIN/EditUser";
    }



    @GetMapping("/upload")
    public String upload(Model model) {
        return "ADMIN/UploadGame";
    }


    @GetMapping("/editevent")
    public String editEvent(Model model) {
        return "ADMIN/EditEvent";
    }


    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session) {

        var admin = adminSevice.findByUsername(username);
        if (admin == null)
            return ResponseEntity.badRequest().body(Map.of("error","Tài khoản không tồn tại!"));
        if (!password.equals(admin.getPassword()))
            return ResponseEntity.badRequest().body(Map.of("error","Sai mật khẩu!"));

        session.setAttribute("id", admin.getAdmin_id());
        session.setAttribute("username", admin.getAdminName());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
