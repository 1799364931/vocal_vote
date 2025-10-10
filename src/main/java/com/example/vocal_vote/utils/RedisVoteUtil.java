package com.example.vocal_vote.utils;

import com.example.vocal_vote.pojo.RandomList;
import com.example.vocal_vote.repository.SongInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisVoteUtil {

    private volatile boolean isSyncing = false;

    public static final String LOCK = "REDIS_LOCK";

    public static final Long LOCK_TIMEOUT_MS = 500L;

    public static final String VoteOptionalHash = "voteOptional";

    public static final String VoteOptionalBufferHash = "voteOptional:buffer";

    // IP 投票次数键前缀
    private static final String IP_VOTE_PREFIX = "vote:ip:";

    // IP 所对应的随机列表
    private static final String RANDOM_LIST_PREFIX = "vote:random:ip:";

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private final SongInfoRepository songInfoRepository;

    @Autowired
    public RedisVoteUtil(StringRedisTemplate redisTemplate,ObjectMapper objectMapper,SongInfoRepository songInfoRepository){
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.songInfoRepository = songInfoRepository;
    }

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
        String value = (String) redisTemplate.opsForHash().get(VoteOptionalHash, optionId.toString());
        return value.equals("") ? -1 : Integer.parseInt(value);
    }

    /** 增加某个选项的得票数 */
    public void incrementOptionVote(Integer optionId) {
        String key = optionId.toString();

        if (!redisTemplate.opsForHash().hasKey(VoteOptionalHash, key)) {
            redisTemplate.opsForHash().put(VoteOptionalHash, key, "0");
        }

        if (isSyncing) {
            redisTemplate.opsForHash().increment(VoteOptionalBufferHash, key, 1L);
        } else {
            redisTemplate.opsForHash().increment(VoteOptionalHash, key, 1L);
        }
    }

    /** 设置 IP 投票次数（可用于初始化或重置） */
    public void setIpVoteCount(String ip, int count) {
        redisTemplate.opsForValue().set(IP_VOTE_PREFIX + ip, String.valueOf(count),1, TimeUnit.DAYS);
    }

    /** 设置选项得票数（可用于初始化或重置） */
    public void setOptionVoteCount(Integer optionId, int count) {
        redisTemplate.opsForHash().put(VoteOptionalHash, optionId.toString(), count);
    }

    /** 设置 IP 随机列表 **/
    public void setIpRandomList(String ip) {
        try{
            String json = objectMapper.writeValueAsString(new RandomList(287));
            redisTemplate.opsForValue().set(RANDOM_LIST_PREFIX + ip, json,1,TimeUnit.DAYS);
        }catch (Exception e){
            System.out.println("序列化失败");
        }

    }

    public boolean IpNotExist(String ip){
        String key = IP_VOTE_PREFIX + ip;
        return !redisTemplate.hasKey(key);
    }

    public boolean OptionIdExist(Integer OptionId){
        return this.getOptionVoteCount(OptionId) != -1;
    }

    public void acquireLock(String key) {
        redisTemplate.opsForValue().setIfAbsent(key, "locked", LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }

    // 每分钟同步一次
    @Scheduled(fixedRate = 60000)
    public void syncVotesToDatabase() {

        System.out.println("[INFO] 开始redis持久化同步");
        acquireLock(RedisVoteUtil.LOCK);

        isSyncing = true;
        Map<Object, Object> voteMap = redisTemplate.opsForHash().entries(VoteOptionalHash);
        voteMap.forEach((optionId, voteCount) -> {
            songInfoRepository.incrementVoteCount(Integer.parseInt(optionId.toString()), Integer.parseInt((String) voteCount));
        });
        for (Object field : voteMap.keySet()) {
            redisTemplate.opsForHash().put(VoteOptionalHash, field, "0");
        }

        isSyncing = false;

        voteMap = redisTemplate.opsForHash().entries(VoteOptionalBufferHash);
        voteMap.forEach((optionId, voteCount) -> {
            songInfoRepository.incrementVoteCount(Integer.parseInt(optionId.toString()), (Integer) voteCount);
        });
        for (Object field : voteMap.keySet()) {
            redisTemplate.opsForHash().put(VoteOptionalBufferHash, field, "0");
        }

        releaseLock(RedisVoteUtil.LOCK);
        System.out.println("[INFO] 结束redis持久化同步");
    }
}
