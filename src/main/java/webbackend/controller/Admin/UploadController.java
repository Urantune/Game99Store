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
public class UploadController {

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




    @GetMapping("/upload")
    public String upload(Model model) {
        return "ADMIN/UploadGame";
    }


    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> uploadGame(
            @RequestParam String gameName,
            @RequestParam double price,
            @RequestParam String category,
            @RequestParam(required = false) String version,

            @RequestParam(value = "description"
                    , required = false) String description,
            @RequestParam(value = "dec_1"
                    , required = false) String dec1,
            @RequestParam(value = "dec_2"
                    , required = false) String dec2,
            @RequestParam(value = "dec_3"
                    , required = false) String dec3,
            @RequestParam(value = "dec_4"
                    , required = false) String dec4,
            @RequestParam(value = "dec_5"
                    , required = false) String dec5,

            @RequestParam(value = "img_video"
                    , required = false) MultipartFile imgVideo,
            @RequestParam(value = "img_1"
                    , required = false) MultipartFile img1,
            @RequestParam(value = "img_2"
                    , required = false) MultipartFile img2,
            @RequestParam(value = "img_3"
                    , required = false) MultipartFile img3,
            @RequestParam(value = "img_cover"
                    , required = false) MultipartFile imgCover,

            @RequestParam("packageFile") MultipartFile packageFile
    ) {
        try {
            if (packageFile == null
                    || packageFile.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("Vui lòng chọn file game");
            }

            String mainDesc = (description != null
                    && !description.isBlank())
                    ? description
                    : (dec1 != null ? dec1 : "");

            StringBuilder deception = new StringBuilder();
            if (mainDesc != null) deception
                    .append(mainDesc.trim());
            if (dec2 != null && !dec2.isBlank()) deception
                    .append("||")
                    .append(dec2.trim());
            if (dec3 != null && !dec3.isBlank()) deception
                    .append("||")
                    .append(dec3.trim());
            if (dec4 != null && !dec4.isBlank()) deception
                    .append("||")
                    .append(dec4.trim());
            if (dec5 != null && !dec5.isBlank()) deception
                    .append("||")
                    .append(dec5.trim());

            String packagePath = gameCore.saveGamePackage(packageFile
                    , category
                    , gameName);

            String video = (imgVideo != null
                    && !imgVideo.isEmpty())
                    ? gameCore.saveToFolderKeepName(imgVideo, "videos") : "";

            String i1 = (img1 != null
                    && !img1.isEmpty())
                    ? gameCore.saveToFolderKeepName(img1, "static/img") : "";

            String i2 = (img2 != null
                    && !img2.isEmpty())
                    ? gameCore.saveToFolderKeepName(img2, "static/img") : "";

            String i3 = (img3 != null
                    && !img3.isEmpty())
                    ? gameCore.saveToFolderKeepName(img3, "static/img") : "";

            String cover = (imgCover != null
                    && !imgCover.isEmpty())
                    ? gameCore.saveToFolderKeepName(imgCover, "static/img") : "";



            Game g = new Game();
            g.setGameName(gameName);
            g.setPrice(price);
            g.setGameCategory(category);
            g.setGame_version(version);
            g.setStatus("coming soon");
            g.setLocate_game(packagePath);
            g.setDeception(deception.toString());


            gameSevice.save(g);


            ImageGame imageGame = new ImageGame();
            imageGame.setGame(g);
            imageGame.setVideo(video);
            imageGame.setImageOne(i1);
            imageGame.setImageTwo(i2);
            imageGame.setImageThree(i3);
            imageGame.setMainImage(cover);

            imageGameService.save(imageGame);


            return ResponseEntity
                    .ok("Upload thành công!");
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body("Server error: "
                            + e.getMessage());
        }
    }



}
