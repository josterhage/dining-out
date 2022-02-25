package com.system559.diningout.controller.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    @Value("${APPLICATION_HOST}")
    private String appHost;

    @GetMapping
    public String homePage(Model model) {
        model.addAttribute("appHost",appHost);
        return "index";
    }
}
