package webbackend.controller.Admin;

import webbackend.entity.*;
import webbackend.SucDat.GameCore;
import webbackend.SucDat.SendMailTest;
import webbackend.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


@Controller
@RequestMapping(value = "/welcomeAdmin")
public class GameManageController {

    @Autowired
    private AdminSevice
            adminSevice;

    @Autowired
    private UserService
            userService;

    @Autowired
    private GameSevice
            gameSevice;

    @Autowired
    private GameCore
            gameCore;

    @Autowired
    private EventService
            eventService;

    @Autowired
    private PasswordEncoder
            passwordEncoder;

    @Autowired
    private VouncherService
            vouncherService;

    @Autowired
    private UserGameService
            userGameService;

    @Autowired
    private SendMailTest
            sendMailTest;

    @Autowired
    private ImageGameService imageGameService;

    @Autowired
    private VoucherUserService voucherUserService;

    @Autowired
    private VoucherGameService voucherGameService;


    @GetMapping("/listgame")
    public String editGame(Model model) {

        List<Game> games = gameSevice.findAllGame();


        Map<UUID, ImageGame> imageMap = games.stream()
                .map(g -> imageGameService.findByGameId(g.getGameId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        img -> img.getGame().getGameId(),
                        Function.identity()
                ));

        model.addAttribute("listGame", games);
        model.addAttribute("imageMap", imageMap);

        return "ADMIN/ListGame";
    }

    @GetMapping("/editgame/{id}")
    public String editGame(@PathVariable UUID id, Model model) {
        Game game = gameSevice.findById(id);
        ImageGame imageGame = imageGameService.findByGameId(id);

        model.addAttribute("game", game);
        model.addAttribute("imageGame", imageGame);

        return "ADMIN/EditGame";
    }



    @PostMapping("/game/{id}/set-main")
    public String setMainGame(@PathVariable
                              UUID id) {

        List<Game> all = gameSevice.findAllGame();

        for (Game g : all) {
            if (g.getGameId()
                    .equals(id)) {
                g.setStatus("main");
            } else {
                if (!g.getStatus()
                        .equals("DELISTED")) {
                    g.setStatus("activate");
                }
            }
            gameSevice.save(g);
        }

        return "redirect:/welcomeAdmin/listgame";
    }

    @PostMapping("/editgame/{id}")
    public String updateGame(@PathVariable UUID id,
                             @RequestParam String gameName,
                             @RequestParam String price,
                             @RequestParam String version,
                             @RequestParam String category,
                             @RequestParam String status,
                             @RequestParam(required = false
                                     , name = "img_video") String imgVideo,
                             @RequestParam(required = false
                                     , name = "img_1") String img1,
                             @RequestParam(required = false
                                     , name = "img_2") String img2,
                             @RequestParam(required = false
                                     , name = "img_3") String img3,
                             @RequestParam(required = false
                                     , name = "img_cover") String imgCover,
                             @RequestParam(required = false
                                     , name = "dec_1") String dec1,
                             @RequestParam(required = false
                                     , name = "dec_2") String dec2,
                             @RequestParam(required = false
                                     , name = "dec_3") String dec3,
                             @RequestParam(required = false
                                     , name = "dec_4") String dec4,
                             @RequestParam(required = false
                                     , name = "dec_5") String dec5,
                             RedirectAttributes ra) {

        Game g = gameSevice.findById(id);

        g.setGameName(gameName);

        String p = price == null ? "" : price
                .replace(","
                        , "")
                .trim();
        if (!p.isEmpty()) g.setPrice(Double.parseDouble(p));

        g.setGame_version(version);
        g.setStatus(status);
        g.setGameCategory(category);

        java.util.function.Function<String, String> norm = s -> {
            if (s == null) return "";
            String t = s.trim();
            if (t.startsWith("/")) t = t.substring(1);
            return t;
        };

        ImageGame imageGame = imageGameService.findByGameId(g.getGameId());
        imageGame.setVideo(imgVideo);
        imageGame.setImageOne(img1);
        imageGame.setImageTwo(img2);
        imageGame.setImageThree(img3);
        imageGame.setMainImage(imgCover);
        imageGameService.save(imageGame);


        gameSevice.save(g);
        ra.addFlashAttribute("ok", "Saved");
        return "redirect:/welcomeAdmin/editgame/" + id;
    }


    @PostMapping("/deletegame")
    public String deleteGame(@RequestParam("id") UUID id) {
        Game g = gameSevice.findById(id);
        userGameService.DeleteByGame(g);
        gameSevice.deleteGame(id);
        return "redirect:/welcomeAdmin/listgame";
    }

}
