package WebBackEnd.SucDat;

import WebBackEnd.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendMailTest {

    @Autowired
    MailService mailService;

    @GetMapping("/hackmail")
    public String testSend(@RequestParam(defaultValue = "thieutrongvkl09@gmail.com") String to) {
        try {
            mailService.sendTest(to);
            return "Khang đẹp trai";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi gửi mail: " + e.getMessage();
        }
    }
}
