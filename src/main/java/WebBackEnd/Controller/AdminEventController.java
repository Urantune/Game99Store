package WebBackEnd.Controller;

import WebBackEnd.Entity.*;
import WebBackEnd.SucDat.GameCore;
import WebBackEnd.SucDat.SendMailTest;
import WebBackEnd.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Controller
@RequestMapping(value = "/welcomeAdmin")
public class AdminEventController {


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


    @GetMapping("/editevent")
    public String editEvent(Model model) {

        model.addAttribute("gameCore"
                , gameCore);
        model.addAttribute("eventMain"
                , eventService.findEventByType("event_main"));
        model.addAttribute("eventNext"
                , eventService.findEventByType("event_next"));
        model.addAttribute("events"
                , eventService.findEventsByType("event_small"));

        return "ADMIN/EditEvent";
    }


    @PostMapping("/events/save")
    public String saveEvent(
            @RequestParam("id") UUID id,
            @RequestParam(value = "title"
                    , required = false) String mainTitle,
            @RequestParam(value = "description"
                    , required = false) String mainDesc,
            @RequestParam(value = "nextTitle"
                    , required = false) String nextTitle,
            @RequestParam(value = "descTitles"
                    , required = false) String[] descTitles,
            @RequestParam(value = "descriptions"
                    , required = false) String[] descriptions,
            @RequestParam(value = "release"
                    , required = false) String release,
            @RequestParam(value = "imageFile"
                    , required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl"
                    , required = false) String imageUrl
    ) {
        Event e = eventService.findById(id);
        if (e == null) return "redirect:/welcomeAdmin/editevent";

        String type = e.getType();

        if ("event_main".equalsIgnoreCase(type)) {
            String t = mainTitle == null
                    ? ""
                    : mainTitle.trim();
            String d = mainDesc == null
                    ? ""
                    : mainDesc.trim();
            e.setInfo(t + "||" + d);
            e.setImageLinks(gameCore.handleMediaReplace(e.getImageLinks()
                    , imageFile
                    , imageUrl
                    , "videos"));
            eventService.save(e);
            return "redirect:/welcomeAdmin/editevent";
        }

        if ("event_next".equalsIgnoreCase(type)) {
            String[] old = e.getInfo() != null
                    ? GameCore.deceptionGame(e.getInfo())
                    : new String[0];

            String title = nextTitle != null
                    ? nextTitle.trim()
                    : "";

            String[] T = descTitles != null
                    ? descTitles
                    : new String[0];

            String[] D = descriptions != null
                    ? descriptions
                    : new String[0];

            String t1 = (T.length > 0
                    && T[0] != null)
                    ? T[0].trim()
                    : "";

            String d1 = (D.length > 0
                    && D[0] != null)
                    ? D[0].trim()
                    : "";

            String t2 = (T.length > 1
                    && T[1] != null)
                    ? T[1].trim()
                    : "";

            String d2 = (D.length > 1
                    && D[1] != null)
                    ? D[1].trim()
                    : "";

            String t3 = (T.length > 2
                    && T[2] != null)
                    ? T[2].trim()
                    : "";

            String d3 = (D.length > 2
                    && D[2] != null)
                    ? D[2].trim()
                    : "";

            String t4 = (T.length > 3
                    && T[3] != null)
                    ? T[3].trim()
                    : "";

            String d4 = (D.length > 3
                    && D[3] != null)
                    ? D[3].trim()
                    : "";


            String oTitle = old.length > 0
                    ? (old[0] == null
                    ? "" : old[0])
                    : "";

            String oT1 = old.length > 1
                    ? (old[1] == null
                    ? "" : old[1])
                    : "";

            String oD1 = old.length > 2
                    ? (old[2] == null
                    ? "" : old[2])
                    : "";

            String oT2 = old.length > 3
                    ? (old[3] == null
                    ? "" : old[3])
                    : "";

            String oD2 = old.length > 4
                    ? (old[4] == null
                    ? "" : old[4])
                    : "";

            String oT3 = old.length > 5
                    ? (old[5] == null
                    ? "" : old[5])
                    : "";

            String oD3 = old.length > 6
                    ? (old[6] == null
                    ? "" : old[6])
                    : "";

            String oT4 = old.length > 7
                    ? (old[7] == null
                    ? "" : old[7])
                    : "";
            String oD4 = old.length > 8
                    ? (old[8] == null
                    ? "" : old[8])
                    : "";

            String bTitle = !title.isEmpty()
                    ? title
                    : oTitle;

            String bT1 = !t1.isEmpty()
                    ? t1
                    : oT1;

            String bD1 = !d1.isEmpty()
                    ? d1
                    : oD1;

            String bT2 = !t2.isEmpty()
                    ? t2
                    : oT2;

            String bD2 = !d2.isEmpty()
                    ? d2
                    : oD2;

            String bT3 = !t3.isEmpty()
                    ? t3
                    : oT3;

            String bD3 = !d3.isEmpty()
                    ? d3
                    : oD3;

            String bT4 = !t4.isEmpty()
                    ? t4
                    : oT4;

            String bD4 = !d4.isEmpty()
                    ? d4
                    : oD4;

            StringBuilder sb = new StringBuilder();

            sb.append(bTitle)
                    .append("||").append(bT1)
                    .append("||").append(bD1)
                    .append("||").append(bT2)
                    .append("||").append(bD2)
                    .append("||").append(bT3)
                    .append("||").append(bD3)
                    .append("||").append(bT4)
                    .append("||").append(bD4);

            if (old.length > 9) {
                for (int i = 9; i < old.length; i++) {
                    sb.append("||")
                            .append(old[i] == null
                                    ? ""
                                    : old[i]);
                }
            }

            e.setInfo(sb.toString());
            e.setImageLinks(gameCore.handleMediaReplace(e.getImageLinks()
                    , imageFile
                    , imageUrl
                    , "static/img"));

            eventService.save(e);
            return "redirect:/welcomeAdmin/editevent";
        }

        if ("event_small".equalsIgnoreCase(type)) {
            String t = mainTitle == null
                    ? ""
                    : mainTitle.trim();

            String d = mainDesc == null
                    ? ""
                    : mainDesc.trim();

            String r = release == null
                    ? ""
                    : release.trim();

            e.setInfo(!r.isEmpty()
                    ? (t + "||" + d + "||" + r)
                    : (t + "||" + d));

            e.setImageLinks(gameCore.handleMediaReplace(e.getImageLinks()
                    , imageFile
                    , imageUrl
                    , "static/img"));

            eventService.save(e);
            return "redirect:/welcomeAdmin/editevent";
        }

        return "redirect:/welcomeAdmin/editevent";
    }


}
