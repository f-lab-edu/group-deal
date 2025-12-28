package com.app.groupdeal.application.group.service;

import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.repository.GroupRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupQueueService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 서버 시작 시 Redis 초기화 (테스트용)
     */
    @PostConstruct
    public void init() {
        initializeQueue(1L, 2);
        log.info("✅ Redis 초기화 완료");
    }

    /**
     * Redis INCR로 순번 발급 (원자적 연산, Thread-Safe)
     */
    public Long issueQueueNumber(Long groupId) {
        String key = "group:" + groupId + ":queue";
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * Redis 초기화
     */
    public void initializeQueue(Long groupId, Integer currentParticipants) {
        String key = "group:" + groupId + ":queue";
        redisTemplate.opsForValue().set(key, String.valueOf(currentParticipants));
        redisTemplate.expire(key, Duration.ofDays(7));
        log.info("✅ [그룹 {}] Redis 초기화: {}", groupId, currentParticipants);
    }

    /**
     * Redis 키 존재 여부 확인
     */
    public boolean hasQueue(Long groupId) {
        String key = "group:" + groupId + ":queue";
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Long getCurrentQueueNumber(Long groupId) {
        String key = "group:" + groupId + ":queue";
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }

    public void resetQueue(Long groupId) {
        String key = "group:" + groupId + ":queue";
        redisTemplate.delete(key);
        log.info("🗑️ [그룹 {}] Redis 순번 삭제", groupId);
    }
}