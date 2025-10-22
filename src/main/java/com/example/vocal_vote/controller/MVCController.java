package com.example.vocal_vote.controller;

import com.example.vocal_vote.service.RankVoteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class MVCController {
    private final RankVoteService rankVoteService;

    @Autowired
    public MVCController(RankVoteService rankVoteService){
        this.rankVoteService = rankVoteService;
    }

    @GetMapping("/")
    public String ShowHomePage(HttpServletRequest httpServletRequest){
        if(rankVoteService.isVote(httpServletRequest)){
            return "rankResult";
        }
        return "rankVote";
    }

}
