package com.example.vocal_vote.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class MVCController {

    @GetMapping("/")
    public String ShowHomePage(){return "rankVote";}

}
