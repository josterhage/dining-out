package com.system559.diningout.controller.checkout;

import com.google.zxing.WriterException;
import com.system559.diningout.model.Guest;
import com.system559.diningout.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/checkout")
public class CheckoutViewController {
    private final CheckoutService checkoutService;

    @Autowired
    public CheckoutViewController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping("/success")
    public String success(Model model,
                          @RequestParam(name="payment_intent") String paymentIntent,
                          @RequestParam(name="payment_intent_client_secret") String clientSecret) throws IOException, WriterException, MessagingException {
        Guest guest = checkoutService.confirmPayment(paymentIntent,clientSecret);
        model.addAttribute("paymentSuccess",true);
        model.addAttribute("grade",guest.getGrade().getName());
        model.addAttribute("firstName",guest.getFirstName());
        model.addAttribute("lastName", guest.getLastName());
        model.addAttribute("email",guest.getEmail());
        return "index";
    }
}
