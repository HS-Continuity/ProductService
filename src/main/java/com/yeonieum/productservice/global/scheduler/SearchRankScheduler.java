package com.yeonieum.productservice.global.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

@Component
public class SearchRankScheduler {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 매 분마다 실행되어 현재 검색어 순위를 업데이트하는 스케줄러 메서드
     * 'currentSearchRank'에서 현재 순위를 읽어 'previousSearchRank'에 복사한 후,
     * 'tempCurrentSearchRank'의 점수를 'currentSearchRank'에 누적
     */
    @Scheduled(cron = "0 * * * * ?")
    public void updateAndCacheSearchRank() {

        Set<ZSetOperations.TypedTuple<Object>> currentRankings = redisTemplate.opsForZSet().rangeWithScores("currentSearchRank", 0, -1);

        if (currentRankings != null && !currentRankings.isEmpty()) {
            // 이전 순위를 현재 순위로 업데이트
            redisTemplate.delete("previousSearchRank");
            currentRankings.forEach(entry ->
                    redisTemplate.opsForZSet().add("previousSearchRank", entry.getValue(), entry.getScore())
            );

            // 새로운 순위 점수 추가 (누적)
            redisTemplate.opsForZSet().rangeWithScores("tempCurrentSearchRank", 0, -1).forEach(entry ->
                    redisTemplate.opsForZSet().incrementScore("currentSearchRank", entry.getValue(), entry.getScore())
            );

        } else {
            // 최초 실행 시, tempCurrentSearchRank에서 currentSearchRank로 초기화
            redisTemplate.opsForZSet().rangeWithScores("tempCurrentSearchRank", 0, -1).forEach(entry ->
                    redisTemplate.opsForZSet().add("currentSearchRank", entry.getValue(), entry.getScore())
            );
        }

        // tempCurrentSearchRank 초기화
        redisTemplate.delete("tempCurrentSearchRank");
    }

    /**
     * 매일 자정에 실행되어 일일 검색어 순위를 리셋하는 스케줄러 메서드
     * 'currentSearchRank'와 'tempCurrentSearchRank'를 삭제하여 순위를 초기화
     * 'previousSearchRank'는 삭제하지 않고 유지하여 사용자에게 계속 보여줌
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailySearchRank() {
        redisTemplate.delete("currentSearchRank");
        redisTemplate.delete("tempCurrentSearchRank");
    }
}


