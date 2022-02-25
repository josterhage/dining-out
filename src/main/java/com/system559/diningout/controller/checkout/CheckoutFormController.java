package com.system559.diningout.controller.checkout;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/checkout")
public class CheckoutFormController {
    @GetMapping("/checkout-form/{guestId}")
    public String getCheckoutForm(@PathVariable String guestId, Model model) {
        model.addAttribute("guestId",guestId);
        return "checkout-form";
    }
}
