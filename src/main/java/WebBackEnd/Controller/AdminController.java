package WebBackEnd.Controller;


import WebBackEnd.model.Entity.Game;
import WebBackEnd.model.Entity.User;
import WebBackEnd.service.AdminSevice;
import WebBackEnd.service.GameSevice;
import WebBackEnd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/controllthing")
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
}
