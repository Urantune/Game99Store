package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @PostMapping("/login")
    public String loginPost(Model model, @RequestParam(value="username", required=false) String username, @RequestParam(value="password", required=false) String password) {
        // TODO: Port logic from LoginServlet.doPost(...)
        // Read form params via @RequestParam
        return "Index"; // change to actual template
    }
}
