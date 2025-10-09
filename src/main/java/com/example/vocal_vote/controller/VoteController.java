package com.example.vocal_vote.controller;

import com.example.vocal_vote.pojo.ResponseMessage;
import com.example.vocal_vote.pojo.dto.VoteOptionalDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("/vote")

public class VoteController {

    @GetMapping("/api/get-vote-optionals")
    public ResponseMessage<List<VoteOptionalDto>> getVoteOptionals(HttpServletRequest httpServletRequest){

    }

}
