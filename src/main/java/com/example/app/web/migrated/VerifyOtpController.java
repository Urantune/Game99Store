package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VerifyOtpController {
    @PostMapping("/verify-otp")
    public String verifyotpPost(Model model, @RequestParam(value="otp", required=false) String otp, @RequestParam(value="username", required=false) String username, @RequestParam(value="password", required=false) String password) {
        // TODO: Port logic from VerifyOtpServlet.doPost(...)
        // Read form params via @RequestParam
        return "index"; // change to actual template
    }
}
