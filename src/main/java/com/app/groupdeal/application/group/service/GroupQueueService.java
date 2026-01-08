package com.app.groupdeal.application.group.service;

import com.app.groupdeal.application.group.dto.QueueResult;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.repository.GroupRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupQueueService {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<List> queueAndPublishScript;


    @PostConstruct
    public void init() {
    }

    public QueueResult issueQueueNumberWithEvent(
            Long groupId,
            Long userId,
            String nickname,
            Integer maxParticipants) {

        String queueKey = "group:" + groupId + ":queue";
        String streamKey = "group:join:stream";

        List<Object> result = redisTemplate.execute(
                queueAndPublishScript,
                List.of(queueKey, streamKey),
                groupId.toString(),
                userId.toString(),
                nickname,
                maxParticipants.toString()
        );

        String status = (String) result.get(0);
        Long queueNumber = Long.parseLong(result.get(1).toString());

        if ("FULL".equals(status)) {
            log.warn("❌ [그룹 {}] [유저 {}] 정원 초과 (순번: {})",
                    groupId, userId, queueNumber);
            return QueueResult.full(queueNumber);
        }

        String eventId = (String) result.get(2);
        log.info("✅ [그룹 {}] [유저 {}] 순번 발급 + 이벤트 발행: {} (eventId: {})",
                groupId, userId, queueNumber, eventId);

        return QueueResult.success(queueNumber, eventId);
    }


    public Long issueQueueNumber(Long groupId) {
        String key = "group:" + groupId + ":queue";
        return redisTemplate.opsForValue().increment(key);
    }

    public void initializeQueue(Long groupId, Integer currentParticipants) {
        String key = "group:" + groupId + ":queue";
        redisTemplate.opsForValue().set(key, String.valueOf(currentParticipants));
        redisTemplate.expire(key, Duration.ofDays(7));
        log.info("✅ [그룹 {}] Redis 초기화: {}", groupId, currentParticipants);
    }

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
