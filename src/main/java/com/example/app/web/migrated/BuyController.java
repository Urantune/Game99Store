package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BuyController {
    @PostMapping("/buy")
    public String buyPost(Model model, @RequestParam(value="gameId", required=false) String gameId) {
        // TODO: Port logic from BuyServlet.doPost(...)
        // Read form params via @RequestParam
        return "Index"; // change to actual template
    }
}
