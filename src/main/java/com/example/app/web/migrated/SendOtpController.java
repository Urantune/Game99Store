package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SendOtpController {
    @PostMapping("/sendOtp")
    public String sendotpPost(Model model, @RequestParam(value="email", required=false) String email) {
        // TODO: Port logic from SendOtpServlet.doPost(...)
        // Read form params via @RequestParam
        return "index"; // change to actual template
    }
}
