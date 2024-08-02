package com.example.ocpspring.control.usersControl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RenderController {
    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
