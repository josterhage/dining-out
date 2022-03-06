package com.system559.diningout.controller.cancel;

import com.stripe.exception.StripeException;
import com.system559.diningout.service.CancellationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cancel")
public class CancelController {
    private final CancellationService cancellationService;
    @Value("${APPLICATION_HOST}")
    private String appHost;

    @Autowired
    public CancelController(CancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    @GetMapping("/{ticketId}")
    public String startCancel(@PathVariable String ticketId, Model model) {
        model.addAttribute("token", cancellationService.startCancellation(ticketId));
        model.addAttribute("appHost",appHost);
        return "cancel";
    }

    @PostMapping("/confirmed/{cancelToken}")
    public String completeCancel(@PathVariable String cancelToken) throws StripeException {
        cancellationService.completeCancellation(cancelToken);
        return "cancel-confirmed";
    }
}
