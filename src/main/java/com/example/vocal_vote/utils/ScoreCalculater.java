package com.example.vocal_vote.utils;


import com.example.vocal_vote.pojo.RankVoteEntry;
import com.example.vocal_vote.pojo.dto.RankVoteDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScoreCalculater {
    // α 是指数衰减因子
    private static final double ALPHA = 0.9;
    private static final int MAX_VOTES = 20;

    /**
     * 计算某个用户对每首歌的得分
     * @param rankVoteDto 用户投票的歌曲列表，按排名顺序（第1名在前）
     * @return Map<歌曲ID, 得分>
     */
    public Map<Integer, Double> computeUserScores(RankVoteDto rankVoteDto) {
        int k = rankVoteDto.getSongIdRanks().size(); // 用户投了多少首歌
        Map<Integer, Double> scores = new HashMap<>();

        if (k == 0 || k > MAX_VOTES) return scores;

        // Step 1: 计算原始权重总和
        double weightSum = 0.0;
        for (int i = 0; i < k; i++) {
            weightSum += Math.pow(ALPHA, i);
        }

        // Step 2: 计算惩罚因子
        double penalty = (double) k / MAX_VOTES;

        // Step 3: 计算每首歌的得分
        for (int i = 0; i < k; i++) {
            Integer songId = rankVoteDto.getSongIdRanks().get(i).getSongId();
            double rawWeight = Math.pow(ALPHA, rankVoteDto.getSongIdRanks().get(i).getRank());
            double normalizedWeight = rawWeight / weightSum;
            double finalScore = penalty * normalizedWeight;
            scores.put(songId, finalScore);
        }

        return scores;
    }

}
