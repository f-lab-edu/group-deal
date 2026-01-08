package com.app.groupdeal.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<List> queueAndPublishScript() {
        String script = """
            local queue_key = KEYS[1]
            local stream_key = KEYS[2]
            local group_id = ARGV[1]
            local user_id = ARGV[2]
            local nickname = ARGV[3]
            local max_participants = tonumber(ARGV[4])
            
            local queue_num = redis.call('INCR', queue_key)
            
            if queue_num > max_participants then
                redis.call('DECR', queue_key)
                return { 'FULL', queue_num - 1 }
            end
            
            local event_id = redis.call('XADD', stream_key, '*',
                'groupId', group_id,
                'userId', user_id,
                'nickname', nickname,
                'queueNumber', queue_num,
                'timestamp', redis.call('TIME')[1]
            )
            
            return { 'SUCCESS', queue_num, event_id }
            """;

        return RedisScript.of(script, List.class);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
