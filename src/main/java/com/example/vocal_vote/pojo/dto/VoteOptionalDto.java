package com.example.vocal_vote.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteOptionalDto {
    String game_name;
    String song_name;
    Integer id;
    Integer score;
}
