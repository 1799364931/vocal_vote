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
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RedisVoteUtil {

    private AtomicReference<String> currentBuffer = new AtomicReference<>("A");

    public static final String LOCK = "REDIS_LOCK";

    public static final Long LOCK_TIMEOUT_MS = 500L;

    public static final String VoteOptionalHashCount = "voteOptionalCount";

    public static final String USER_ID_PREFIX = "user:id:";

    public static final String VoteOptionHashScore = "voteOptionalScore";

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

    /** 增加某个选项的得票数 */
    public void incrementOptionVoteScore(Integer optionId, Double value) {
        String key = optionId.toString();
        redisTemplate.opsForHash().putIfAbsent(VoteOptionHashScore,key, "0");
        redisTemplate.opsForHash().increment(VoteOptionHashScore,key,value);
    }

    public void incrementOptionVoteCount(Integer optionId) {
        String key = optionId.toString();
        redisTemplate.opsForHash().putIfAbsent(VoteOptionalHashCount,key, "0");
        redisTemplate.opsForHash().increment(VoteOptionalHashCount,key,1);
    }

    /** 设置 IP 投票次数（可用于初始化或重置） */
    public void setIpVoteCount(String ip, int count ,Long timeout) {
        redisTemplate.opsForValue().set(IP_VOTE_PREFIX + ip, String.valueOf(count),timeout, TimeUnit.HOURS);
    }

    /** 设置 IP 随机列表 **/
    public void setIpRandomList(String ip) {
        try{
            String json = objectMapper.writeValueAsString(new RandomList(287));
            redisTemplate.opsForValue().set(RANDOM_LIST_PREFIX + ip, json,12,TimeUnit.HOURS);
        }catch (Exception e){
            System.out.println("序列化失败");
        }
    }

    public boolean IpNotExist(String ip){
        String key = IP_VOTE_PREFIX + ip;
        return !redisTemplate.hasKey(key);
    }

    public void acquireLock(String key) {
        redisTemplate.opsForValue().setIfAbsent(key, "locked", LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }

    public void addUser(String userId){
        redisTemplate.opsForValue().setIfAbsent(USER_ID_PREFIX+userId,"1");
    }

    public boolean UserExist(String userId){
        return redisTemplate.hasKey(USER_ID_PREFIX+userId);
    }

    // 每分钟同步一次
    @Scheduled(fixedRate = 60000)
    public void syncVotesToDatabase() {

        System.out.println("[INFO] 开始redis持久化同步");
//        String processingBuffer = currentBuffer.get();
//        String bufferKey = currentBuffer.get().equals("A")?VoteOptionalHash: VoteOptionalBufferHashScore;
//        String nextBuffer = processingBuffer.equals("A") ? "B" : "A";
//        currentBuffer.set(nextBuffer);
        acquireLock(RedisVoteUtil.LOCK);
        // 切换缓冲区
        // 等待一会后进行同步
//        try {
//            Thread.sleep(500); // 等待 0.5 秒，确保写入完成
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            System.err.println("[ERROR] 同步等待被中断");
//            return;
//        }

        Map<Object, Object> voteMap = redisTemplate.opsForHash().entries(VoteOptionalHashCount);
        Map<Object,Object> scoreMap = redisTemplate.opsForHash().entries(VoteOptionHashScore);

        for (Object field : voteMap.keySet()) {
            redisTemplate.opsForHash().put(VoteOptionalHashCount, field, "0");
            redisTemplate.opsForHash().put(VoteOptionHashScore, field, "0");
        }

        releaseLock(RedisVoteUtil.LOCK);

        voteMap.forEach((optionId, voteCount) -> {
            songInfoRepository.incrementVoteCount(Integer.parseInt(optionId.toString()),Double.parseDouble((String)scoreMap.get(optionId)) ,Integer.parseInt((String) voteCount));
        });

        System.out.println("[INFO] 结束redis持久化同步");
    }
}
