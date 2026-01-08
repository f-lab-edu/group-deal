package com.app.groupdeal.application.group.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueResult {
    private boolean success;
    private Long queueNumber;
    private String eventId;

    public static QueueResult success(Long queueNumber, String eventId) {
        return new QueueResult(true, queueNumber, eventId);
    }

    public static QueueResult full(Long queueNumber) {
        return new QueueResult(false, queueNumber, null);
    }
}
