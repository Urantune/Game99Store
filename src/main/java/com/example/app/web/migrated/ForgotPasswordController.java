package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {
    @GetMapping("/ForgotPassword")
    public String forgotpasswordGet(Model model, @RequestParam(value="username", required=false) String username, @RequestParam(value="email", required=false) String email) {
        // TODO: Port logic from ForgotPasswordServlet.doGet(...)
        // Populate model attributes here
        return "ForgotPassword"; // change to actual template
    }
    @PostMapping("/ForgotPassword")
    public String forgotpasswordPost(Model model, @RequestParam(value="username", required=false) String username, @RequestParam(value="email", required=false) String email) {
        // TODO: Port logic from ForgotPasswordServlet.doPost(...)
        // Read form params via @RequestParam
        return "ForgotPassword"; // change to actual template
    }
}
