package com.example.vocal_vote.repository;

import com.example.vocal_vote.pojo.SongInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SongInfoRepository extends JpaRepository<SongInfo,Integer> {

    public default void incrementVoteCount(Integer songId,Double score,Integer voteCount){
        var songInfo = findById(songId);
        if(songInfo.isPresent()){
            songInfo.get().setVote_count(voteCount + songInfo.get().getVote_count());
            songInfo.get().setScore(score + songInfo.get().getScore());
            save(songInfo.get());
        }
    }
}
