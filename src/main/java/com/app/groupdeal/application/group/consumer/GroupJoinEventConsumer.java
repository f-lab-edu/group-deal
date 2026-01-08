package com.app.groupdeal.application.group.consumer;

import com.app.groupdeal.application.group.service.GroupMemberService;
import com.app.groupdeal.application.group.service.GroupService;
import com.app.groupdeal.domain.group.model.GroupMember;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class GroupJoinEventConsumer {

    private final GroupMemberService groupMemberService;
    private final GroupService groupService;
    private final StringRedisTemplate redisTemplate;

    private static final String STREAM_KEY = "group:join:stream";
    private static final String CONSUMER_GROUP = "group-join-processor";
    private static final String CONSUMER_NAME = "worker-1";

    @PostConstruct
    public void init() {
        createConsumerGroupIfNotExists();

        Thread consumerThread = new Thread(() -> startConsuming(), "event-consumer-worker-1");
        consumerThread.setDaemon(false);
        consumerThread.start();

        log.info("✅ Consumer 스레드 시작 완료");
    }

    private void createConsumerGroupIfNotExists() {
        try {
            redisTemplate.opsForStream().createGroup(STREAM_KEY, CONSUMER_GROUP);
            log.info("✅ Consumer Group 생성: {}", CONSUMER_GROUP);
        } catch (Exception e) {
            log.info("ℹ️ Consumer Group 이미 존재: {}", CONSUMER_GROUP);
        }
    }

    private void startConsuming() {
        log.info("🚀 이벤트 소비 시작: {}", CONSUMER_NAME);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                consumeBatch();
            } catch (Exception e) {
                log.error("❌ 이벤트 소비 중 오류", e);
                sleep(1000);
            }
        }
    }

    private void consumeBatch() {
        List<MapRecord<String, Object, Object>> records =
                redisTemplate.opsForStream().read(
                        Consumer.from(CONSUMER_GROUP, CONSUMER_NAME),
                        StreamReadOptions.empty()
                                .count(10)
                                .block(Duration.ofSeconds(2)),
                        StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed())
                );

        if (records != null && !records.isEmpty()) {
            log.info("📥 이벤트 {} 건 수신", records.size());

            for (MapRecord<String, Object, Object> record : records) {
                processEvent(record);
            }
        }
    }

    @Transactional
    protected void processEvent(MapRecord<String, Object, Object> record) {
        String eventId = record.getId().getValue();

        try {
            Map<Object, Object> event = record.getValue();

            Long groupId = Long.parseLong((String) event.get("groupId"));
            Long userId = Long.parseLong((String) event.get("userId"));
            String nickname = (String) event.get("nickname");
            Integer queueNumber = Integer.parseInt((String) event.get("queueNumber"));

            log.info("⚙️ [이벤트 처리 시작] eventId={}, groupId={}, userId={}, queue={}",
                    eventId, groupId, userId, queueNumber);

            GroupMember member = groupMemberService.joinGroup(groupId, userId, nickname, queueNumber);
            groupService.increaseParticipant(groupId);

            log.info("✅ [이벤트 처리 완료] eventId={}, memberId={}",
                    eventId, member.getGroupMemberId());

            redisTemplate.opsForStream().acknowledge(STREAM_KEY, CONSUMER_GROUP, record.getId());

        } catch (Exception e) {
            log.error("❌ [이벤트 처리 실패] eventId={}", eventId, e);
            handleFailedEvent(record, e);
        }
    }

    private void handleFailedEvent(MapRecord<String, Object, Object> record, Exception e) {
        log.warn("⚠️ 실패 이벤트를 DLQ로 이동: {}", record.getId().getValue());

        redisTemplate.opsForStream().add(
                "group:join:dlq",
                Map.of(
                        "originalEventId", record.getId().getValue(),
                        "error", e.getMessage(),
                        "data", record.getValue().toString()
                )
        );

        redisTemplate.opsForStream().acknowledge(STREAM_KEY, CONSUMER_GROUP, record.getId());
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("⚠️ Consumer 중단됨");
        }
    }
}

