package com.example.vocal_vote.service;


import com.example.vocal_vote.pojo.User;
import com.example.vocal_vote.pojo.dto.RankVoteCommitDto;
import com.example.vocal_vote.pojo.dto.VoteOptionalDto;
import com.example.vocal_vote.repository.SongInfoRepository;
import com.example.vocal_vote.repository.UserRepository;
import com.example.vocal_vote.utils.IpParser;
import com.example.vocal_vote.utils.RedisVoteUtil;
import com.example.vocal_vote.utils.ScoreCalculater;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RankVoteService {

    private final SongInfoRepository songInfoRepository;
    private final RedisVoteUtil redisVoteUtil;
    private final ScoreCalculater scoreCalculater;
    private final UserRepository userRepository;

    @Autowired
    public RankVoteService(UserRepository userRepository,SongInfoRepository songInfoRepository,RedisVoteUtil redisVoteUtil, ScoreCalculater scoreCalculater){
        this.songInfoRepository = songInfoRepository;
        this.redisVoteUtil = redisVoteUtil;
        this.scoreCalculater = scoreCalculater;
        this.userRepository = userRepository;
    }

    public List<VoteOptionalDto> getVoteOptional(){
        List<VoteOptionalDto> voteOptionalDtos = new ArrayList<>();
        songInfoRepository.findAll().forEach(
            info -> voteOptionalDtos.add(new VoteOptionalDto(info.getGameName(), info.getSongName(),
                    info.getId(), info.getScore(),info.getVote_count(), info.getYear(),info.getIframeUrl()))
        );
        return voteOptionalDtos;
    }


    public Boolean rankVoteCommit(RankVoteCommitDto rankVoteCommitDto, HttpServletRequest httpServletRequest){
        // 加入用户到缓冲池
        // 如果这个用户已经投过票了
        String ip = IpParser.parse(httpServletRequest);
        redisVoteUtil.acquireLock(RedisVoteUtil.LOCK);
        if(!redisVoteUtil.IpNotExist(ip)){
            redisVoteUtil.releaseLock(RedisVoteUtil.LOCK);
            return Boolean.FALSE;
        }
        redisVoteUtil.setIpVoteCount(ip,1,1024L);
        // 双重判断

        // 用户投票后
        if(redisVoteUtil.UserExist(rankVoteCommitDto.getUserId())){
            redisVoteUtil.releaseLock(RedisVoteUtil.LOCK);
           return Boolean.FALSE;
        }

        var scores = scoreCalculater.computeUserScores(rankVoteCommitDto.getRankVoteDto());
        scores.forEach((songId,score) ->{
            redisVoteUtil.incrementOptionVoteScore(songId,score);
            redisVoteUtil.incrementOptionVoteCount(songId);

        });


        redisVoteUtil.releaseLock(RedisVoteUtil.LOCK);
        //投票后强制进行一次同步
        redisVoteUtil.syncVotesToDatabase();

        //默认该用户不存在
        User user = new User();
        user.setVotedSongs(new ArrayList<>());
        scores.forEach((songId,score) ->{
            user.getVotedSongs().add(songInfoRepository.findById(songId).get());
        });
        user.setNickName(rankVoteCommitDto.getUserId());
        userRepository.save(user);

        return Boolean.TRUE;
    }

    public List<VoteOptionalDto> getRankVoteTopLimitOptionals(Integer limit){
        var queryRes = songInfoRepository.findAll(Sort.by("score").descending());
        List<VoteOptionalDto> voteOptionalDtoList =new ArrayList<>();
        for(int i = 0; i<limit;i++){
            voteOptionalDtoList.add(new VoteOptionalDto(queryRes.get(i)));
        }
        return voteOptionalDtoList;
    }

    public Boolean isVote(HttpServletRequest httpServletRequest){
        return !redisVoteUtil.IpNotExist(IpParser.parse(httpServletRequest));
    }
}
