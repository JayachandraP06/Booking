package com.ridetogether.booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentController {

    @GetMapping("/")
    public String index()
    {
        return "index";
    }

}
