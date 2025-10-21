package com.example.vocal_vote.pojo.dto;

import com.example.vocal_vote.pojo.RankVoteEntry;
import lombok.Data;

import java.util.List;

@Data
public class RankVoteDto {
    List<RankVoteEntry> songIdRanks;
}
