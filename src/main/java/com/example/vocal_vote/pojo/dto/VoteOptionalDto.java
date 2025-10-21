package com.example.vocal_vote.pojo.dto;

import com.example.vocal_vote.pojo.SongInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteOptionalDto {
    String game_name;
    String song_name;
    Integer id;
    Double score;
    Integer vote_count;
    Integer year;
    String iframe_url;

    public VoteOptionalDto(SongInfo songInfo){
        this.game_name = songInfo.getGameName();
        this.song_name = songInfo.getSongName();
        this.id = songInfo.getId();
        this.score = songInfo.getScore();
        this.vote_count = songInfo.getVote_count();
        this.year = songInfo.getYear();
    }
}
