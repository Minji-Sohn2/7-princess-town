package com.example.princesstown.controller.comment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class viewcontroller {
    @GetMapping("/view")
    public String getview() {
        return "index";
    }
}
