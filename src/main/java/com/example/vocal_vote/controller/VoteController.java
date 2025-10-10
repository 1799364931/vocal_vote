package com.example.vocal_vote.controller;

import com.example.vocal_vote.pojo.ResponseMessage;
import com.example.vocal_vote.pojo.dto.VoteCommitDto;
import com.example.vocal_vote.pojo.dto.VoteOptionalDto;
import com.example.vocal_vote.service.VoteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/vote")

public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService){
        this.voteService = voteService;
    }

    @GetMapping("/api/get-vote-optionals")
    public ResponseMessage<List<VoteOptionalDto>> getVoteOptionals(HttpServletRequest httpServletRequest){
        return ResponseMessage.success(voteService.getVoteOptional(httpServletRequest),"success");
    }

    @PostMapping("/api/vote-for-optionals")
    public ResponseMessage<Boolean> voteForOptionals(@RequestBody VoteCommitDto voteCommitDto, HttpServletRequest httpServletRequest){
        if(voteService.voteForOptionals(httpServletRequest, voteCommitDto)){
            return ResponseMessage.success(Boolean.TRUE,"success");
        }
        return ResponseMessage.fail(Boolean.FALSE,"fail");
    }

}
