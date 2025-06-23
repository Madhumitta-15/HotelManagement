package com.hotel.Booking_System.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
public class HomeController {

    @GetMapping("/home")
    public String getHomePage(){
        return "Welcome to Home Page";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(){
        return "welcome Admin";
    }
    @GetMapping("/guest/dashboard")
    public String guestDashboard(){
        return "welcome User";
    }
    @GetMapping("/manager/dashboard")
    public String managerDashboard(){
        return "welcome manager";
    }
}

