package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {
    @GetMapping("/Cart")
    public String cartGet(Model model, @RequestParam(value="action", required=false) String action, @RequestParam(value="gameId", required=false) String gameId) {
        // TODO: Port logic from CartServlet.doGet(...)
        // Populate model attributes here
        return "Cart"; // change to actual template
    }
    @PostMapping("/Cart")
    public String cartPost(Model model, @RequestParam(value="action", required=false) String action, @RequestParam(value="gameId", required=false) String gameId) {
        // TODO: Port logic from CartServlet.doPost(...)
        // Read form params via @RequestParam
        return "Cart"; // change to actual template
    }
}
