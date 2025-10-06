package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LogoutController {
    @GetMapping("/logoutServlet")
    public String logoutGet(Model model) {
        // TODO: Port logic from LogoutServlet.doGet(...)
        // Populate model attributes here
        return "redirect:/Index"; // change to actual template
    }
    @PostMapping("/logoutServlet")
    public String logoutPost(Model model) {
        // TODO: Port logic from LogoutServlet.doPost(...)
        // Read form params via @RequestParam
        return "redirect:/Index"; // change to actual template
    }
}
