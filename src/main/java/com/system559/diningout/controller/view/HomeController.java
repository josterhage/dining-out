package com.system559.diningout.controller.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public String homePage() {
        return System.currentTimeMillis() >= 1648105199000L ? "eventOver" : "index";
    }

    @GetMapping("/view/{view}")
    public String getView(@PathVariable String view) {
        return view;
    }

}
