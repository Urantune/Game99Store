package com.example.app.web.migrated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RemoveFromCartController {
    @PostMapping("/RemoveFromCart")
    public String removefromcartPost(Model model, @RequestParam(value="gameId", required=false) String gameId) {
        // TODO: Port logic from RemoveFromCart.doPost(...)
        // Read form params via @RequestParam
        return "redirect:/Index"; // change to actual template
    }
}
