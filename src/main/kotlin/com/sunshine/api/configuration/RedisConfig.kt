package com.sunshine.api.configuration

import com.sunshine.api.util.RedisUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class RedisConfig {
    @Bean
    fun redisUtil(redisTemplate: StringRedisTemplate): RedisUtil {
        return RedisUtil(redisTemplate)
    }
}