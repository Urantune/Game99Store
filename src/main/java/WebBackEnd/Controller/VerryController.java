package WebBackEnd.Controller;


import WebBackEnd.Entity.User;
import WebBackEnd.service.DetailService;
import WebBackEnd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.MessageDigest;

import java.util.UUID;

@Controller
@RequestMapping("/veryAccount")
public class VerryController {


    @Autowired
    private DetailService  detailService;
    @Autowired
    private UserService userService;

    public VerryController(DetailService detailService, UserService userService) {
        this.detailService = detailService;
        this.userService = userService;
    }




    @GetMapping("/done/{id}/{code}")
    public String veryAccount(@PathVariable UUID id, @PathVariable String code, Model model)
    {

        String input = "wait"+ id;
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

        System.out.println(fi);



        User user = userService.findById(id);
        if(user.getStatus().equalsIgnoreCase("wait"))
        {
            if(user.getId().toString().equalsIgnoreCase(id.toString()))
            {
                if(code.equalsIgnoreCase(fi)){
                    model.addAttribute("user",user);
                    model.addAttribute("idUser",id);
                    model.addAttribute("codeUser",fi);
                    return "HTML/VeryFileSend";
                }

            }



        }


            return "HTML/About";

    }
    @PostMapping("/done/{id}/{code}")
    public String doneAccount(@PathVariable(value = "id")UUID id, @PathVariable(value = "code")String code,Model model)
    {
        User user=userService.findById(id);
        user.setStatus("active");
        userService.save(user);

        return "HTML/About";
    }




}
