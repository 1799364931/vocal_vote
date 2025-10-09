package com.example.vocal_vote.pojo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class VoteEntry {
    @Min(1)
    @Max(100)
    private Integer key;

    @Min(0)
    @Max(10)
    private Integer value;

    // getters/setters
}
