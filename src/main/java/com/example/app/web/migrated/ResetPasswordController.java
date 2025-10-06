package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResetPasswordController {
    @GetMapping("/ResetPassword")
    public String resetpasswordGet(Model model, @RequestParam(value="token", required=false) String token, @RequestParam(value="password", required=false) String password, @RequestParam(value="confirm", required=false) String confirm) {
        // TODO: Port logic from ResetPasswordServlet.doGet(...)
        // Populate model attributes here
        return "ResetPassword"; // change to actual template
    }
    @PostMapping("/ResetPassword")
    public String resetpasswordPost(Model model, @RequestParam(value="token", required=false) String token, @RequestParam(value="password", required=false) String password, @RequestParam(value="confirm", required=false) String confirm) {
        // TODO: Port logic from ResetPasswordServlet.doPost(...)
        // Read form params via @RequestParam
        return "ResetPassword"; // change to actual template
    }
}
