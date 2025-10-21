package com.example.vocal_vote.service;


import com.example.vocal_vote.pojo.dto.RankVoteCommitDto;
import com.example.vocal_vote.pojo.dto.VoteOptionalDto;
import com.example.vocal_vote.repository.SongInfoRepository;
import com.example.vocal_vote.utils.RedisVoteUtil;
import com.example.vocal_vote.utils.ScoreCalculater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RankVoteService {

    private final SongInfoRepository songInfoRepository;
    private final RedisVoteUtil redisVoteUtil;
    private final ScoreCalculater scoreCalculater;

    @Autowired
    public RankVoteService(SongInfoRepository songInfoRepository,RedisVoteUtil redisVoteUtil, ScoreCalculater scoreCalculater){
        this.songInfoRepository = songInfoRepository;
        this.redisVoteUtil = redisVoteUtil;
        this.scoreCalculater = scoreCalculater;
    }

    public List<VoteOptionalDto> getVoteOptional(){
        List<VoteOptionalDto> voteOptionalDtos = new ArrayList<>();
        songInfoRepository.findAll().forEach(
            info -> voteOptionalDtos.add(new VoteOptionalDto(info.getGameName(), info.getSongName(),
                    info.getId(), info.getScore(),info.getVote_count(), info.getYear(),info.getIframeUrl()))
        );
        return voteOptionalDtos;
    }


    public Boolean rankVoteCommit(RankVoteCommitDto rankVoteCommitDto){
        // 加入用户到缓冲池
        // 如果这个用户已经投过票了
        redisVoteUtil.acquireLock(RedisVoteUtil.LOCK);
//        if(redisVoteUtil.UserExist(rankVoteCommitDto.getUserId())){
//            redisVoteUtil.releaseLock(RedisVoteUtil.LOCK);
//           return Boolean.FALSE;
//        }
        var scores = scoreCalculater.computeUserScores(rankVoteCommitDto.getRankVoteDto());
        scores.forEach((songId,score) ->{
            redisVoteUtil.incrementOptionVoteScore(songId,score);
            redisVoteUtil.incrementOptionVoteCount(songId);
        });
        redisVoteUtil.releaseLock(RedisVoteUtil.LOCK);
        return Boolean.TRUE;
    }
}
