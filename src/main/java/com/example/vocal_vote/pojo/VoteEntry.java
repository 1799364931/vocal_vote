package com.example.vocal_vote.pojo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class VoteEntry {
    @Min(1)
    @Max(300)
    private Integer key;

    @Min(-1)
    @Max(1)
    private Integer value;

    // getters/setters
}
