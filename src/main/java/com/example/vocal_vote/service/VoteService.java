package com.example.vocal_vote.service;

import com.example.vocal_vote.pojo.dto.VoteOptionalDto;
import com.example.vocal_vote.repository.SongInfoRepository;
import com.example.vocal_vote.utils.IpParser;
import com.example.vocal_vote.utils.RedisVoteUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoteService {

    private final SongInfoRepository songInfoRepository;
    private final RedisVoteUtil redisVoteUtil;

    @Autowired
    public VoteService(RedisVoteUtil redisVoteUtil,SongInfoRepository songInfoRepository){
        this.redisVoteUtil = redisVoteUtil;
        this.songInfoRepository = songInfoRepository;
    }

    public List<VoteOptionalDto> getVoteOptional(HttpServletRequest httpServletRequest){
        var ip = IpParser.parse(httpServletRequest);
        if(!redisVoteUtil.IpExist(ip)){
            redisVoteUtil.setIpVoteCount(ip,0);
            redisVoteUtil.setIpRandomList(ip);
        }
        var voteCount = redisVoteUtil.getIpVoteCount(ip);
        // 选择起始歌
        var startIdx = voteCount * 10;
        var randomList = redisVoteUtil.getIpRandomList(ip);
        List<VoteOptionalDto> voteOptionalDtos = new ArrayList<>();
        for (int i = startIdx; i < Math.min(startIdx+10,randomList.getListSize()) ; i++) {
            var songInfo = songInfoRepository.findById(randomList.getRandomList().get(i));
            songInfo.ifPresent(info -> voteOptionalDtos.add(new VoteOptionalDto(info.getGameName(), info.getSongName(),
                    info.getId(), info.getScore())));
        }
        return voteOptionalDtos;
    }

//    public boolean voteForOptionals(HttpServletRequest httpServletRequest){
//
//    }
}
