package com.example.vocal_vote.pojo.dto;

import com.example.vocal_vote.pojo.VoteEntry;
import lombok.Data;

import java.util.List;

@Data
public class VoteCommitDto {
    List<VoteEntry> voteResult;
}
