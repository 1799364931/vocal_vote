package com.example.vocal_vote.controller;

import com.example.vocal_vote.pojo.ResponseMessage;
import com.example.vocal_vote.pojo.dto.RankVoteCommitDto;
import com.example.vocal_vote.pojo.dto.VoteOptionalDto;
import com.example.vocal_vote.service.RankVoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rank-vote")
@Tag(name = "排名投票" , description = "排名投票接口")
public class RankVoteController {
    private final RankVoteService rankVoteService;

    @Autowired
    public RankVoteController(RankVoteService rankVoteService){
        this.rankVoteService = rankVoteService;
    }

    @GetMapping("/api/get-vote-optionals")
    @Operation(summary = "获取排名投票选项数据")
    public ResponseMessage<List<VoteOptionalDto>> getVoteOptionals(){
        return ResponseMessage.success(rankVoteService.getVoteOptional(),"success");
    }

    @PostMapping("/api/commit-rank-vote")
    @Operation(summary = "提交排名投票")
    public ResponseMessage<Boolean> commitVote(@RequestBody RankVoteCommitDto rankVoteCommitDto,HttpServletRequest httpServletRequest){
        if(rankVoteService.rankVoteCommit(rankVoteCommitDto,httpServletRequest)){
            return ResponseMessage.success(Boolean.TRUE,"success");
        }
        return ResponseMessage.fail(Boolean.FALSE,"fail");
    }

    @GetMapping("/api/get-top-list")
    public ResponseMessage<List<VoteOptionalDto>> getTopLimitOptionals(@RequestParam("n") Integer limit){
        return ResponseMessage.success(rankVoteService.getRankVoteTopLimitOptionals(limit),"success");
    }
}
