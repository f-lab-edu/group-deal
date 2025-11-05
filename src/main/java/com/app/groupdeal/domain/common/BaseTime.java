package com.app.groupdeal.domain.common;

import ch.qos.logback.core.util.Loader;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BaseTime {

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
