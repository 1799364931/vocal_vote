package com.example.vocal_vote.pojo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RankVoteEntry {
    @Min(1)
    @Max(300)
    private Integer songId;

    @Min(1)
    @Max(20)
    private Integer rank;

    // getters/setters
}
