package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileUserController {
    @GetMapping("/ProfileUser")
    public String profileuserGet(Model model) {
        // TODO: Port logic from ProfileUserServlet.doGet(...)
        // Populate model attributes here
        return "ProfileUser"; // change to actual template
    }
}
