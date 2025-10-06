package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterGameController {
    @PostMapping("/RegisterGame")
    public String registergamePost(Model model, @RequestParam(value="gameId", required=false) String gameId) {
        // TODO: Port logic from RegisterGameServlet.doPost(...)
        // Read form params via @RequestParam
        return "index"; // change to actual template
    }
}
