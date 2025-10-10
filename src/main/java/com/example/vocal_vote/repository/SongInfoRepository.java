package com.example.vocal_vote.repository;

import com.example.vocal_vote.pojo.SongInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongInfoRepository extends JpaRepository<SongInfo,Integer> {

    public default void incrementVoteCount(Integer songId,Integer voteCount){
        var songInfo = findById(songId);
        if(songInfo.isPresent()){
            songInfo.get().setVote_count(voteCount + songInfo.get().getVote_count());
            songInfo.get().setScore(voteCount + songInfo.get().getScore());
            save(songInfo.get());
        }
    }
}
