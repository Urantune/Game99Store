package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {
    @PostMapping("/register")
    public String registerPost(Model model, @RequestParam(value="username", required=false) String username, @RequestParam(value="password", required=false) String password, @RequestParam(value="confirmPassword", required=false) String confirmPassword, @RequestParam(value="email", required=false) String email, @RequestParam(value="otp", required=false) String otp) {
        // TODO: Port logic from RegisterServlet.doPost(...)
        // Read form params via @RequestParam
        return "Index"; // change to actual template
    }
}
