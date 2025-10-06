package com.example.app.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
public class PageController {
    // GET /view/index -> templates/index.html
    @GetMapping({"", "/", "/index"})
    public String index(Model model) {
        return "index";
    }

    // GET /view/{name} -> templates/{name}.html
    @GetMapping("/welcome.html")
    public String view(@PathVariable String name, Model model) {
        return name;
    }
}
