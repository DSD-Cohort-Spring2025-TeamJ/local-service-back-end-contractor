package com.localservice.localservice_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @GetMapping("/appointment")
    public String bookAppointment(){
        return "appointment";
    }
}
