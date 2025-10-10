package com.example.vocal_vote.utils;

import com.example.vocal_vote.pojo.RandomList;
import com.example.vocal_vote.pojo.SongInfo;
import com.example.vocal_vote.repository.SongInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisVoteUtil {

    public static final String LOCK = "REDIS_LOCK";

    public static final Long LOCK_TIMEOUT_MS = 500L;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SongInfoRepository songInfoRepository;

    // IP 投票次数键前缀
    private static final String IP_VOTE_PREFIX = "vote:ip:";

    // IP 所对应的随机列表
    private static final String RANDOM_LIST_PREFIX = "vote:random:ip";

    /** 获取某个 IP 的随机列表 **/
    public RandomList getIpRandomList(String ip) {
        String key = RANDOM_LIST_PREFIX + ip;
        String json = redisTemplate.opsForValue().get(key);
        RandomList randomList = null;
        try{
            randomList = objectMapper.readValue(json,RandomList.class);
        }catch (Exception ignored){

        }
        return randomList;
    }

    /** 获取某个 IP 的投票次数 */
    public int getIpVoteCount(String ip) {
        String key = IP_VOTE_PREFIX + ip;
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? -1 : Integer.parseInt(value);
    }

    /** 增加某个 IP 的投票次数 */
    public void incrementIpVote(String ip) {
        String key = IP_VOTE_PREFIX + ip;
        redisTemplate.opsForValue().increment(key);
    }

    /** 获取某个选项的得票数 */
    public int getOptionVoteCount(Integer optionId) {
        String value = (String) redisTemplate.opsForHash().get("SongVote", optionId.toString());
        return value.equals("") ? -1 : Integer.parseInt(value);
    }

    /** 增加某个选项的得票数 */
    public void incrementOptionVote(Integer optionId) {
        if(getOptionVoteCount(optionId) == -1){
            this.setOptionVoteCount(optionId,0);
        }
        redisTemplate.opsForHash().increment("SongVote",optionId.toString(),1);
    }

    /** 设置 IP 投票次数（可用于初始化或重置） */
    public void setIpVoteCount(String ip, int count) {
        redisTemplate.opsForValue().set(IP_VOTE_PREFIX + ip, String.valueOf(count),1, TimeUnit.DAYS);
    }

    /** 设置选项得票数（可用于初始化或重置） */
    public void setOptionVoteCount(Integer optionId, int count) {
        redisTemplate.opsForHash().put("SongVote", optionId.toString(), count);
    }

    /** 设置 IP 随机列表 **/
    public void setIpRandomList(String ip) {
        try{
            redisTemplate.opsForValue().set(RANDOM_LIST_PREFIX + ip,objectMapper.writeValueAsString(new RandomList(287)) ,1,TimeUnit.DAYS);
        }catch (Exception e){
            System.out.println("序列化失败");
        }

    }

    public boolean IpExist(String ip){
        return this.getIpVoteCount(ip) != -1;
    }

    public boolean OptionIdExist(Integer OptionId){
        return this.getOptionVoteCount(OptionId) != -1;
    }

    public boolean acquireLock(String key) {
        return redisTemplate.opsForValue().setIfAbsent(key, "locked", LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }

    // 每十分钟同步一次
    @Scheduled(fixedRate = 60*1000*10)
    public void syncVotesToDatabase() {
        acquireLock(RedisVoteUtil.LOCK);
        Map<Object, Object> voteMap = redisTemplate.opsForHash().entries("SongVote");
        voteMap.forEach((optionId, voteCount) -> {
            songInfoRepository.incrementVoteCount(Integer.parseInt(optionId.toString()), (Integer) voteCount);
        });
        redisTemplate.delete("SongVote"); // 清空 Redis 中的缓存
        releaseLock(RedisVoteUtil.LOCK);
    }
}
