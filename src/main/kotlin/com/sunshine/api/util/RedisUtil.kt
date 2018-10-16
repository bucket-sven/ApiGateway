package com.sunshine.api.util

import com.alibaba.fastjson.JSON
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.concurrent.TimeUnit

//inline fun <reified T>StringRedisTemplate.fetch(key: String, expireSeconds: Long = 300L, expireSecondsWhenNull: Long = 10L, callback: () -> T): T? {
//    val client = this.opsForValue()
//    val str = client.get(key)
//    if (str == null) {
//        val result = callback()
//        val expires = if (result == null) expireSecondsWhenNull else expireSeconds
//        client.set(key, JSON.toJSONString(result), expires, TimeUnit.SECONDS)
//        return result
//    }
//    return JSON.parseObject(str, T::class.java)
//}

class RedisUtil(val redisTemplate: StringRedisTemplate) : RedisOperations<String, String> by redisTemplate {
    /**
     * @param key 缓存的key
     * @param expireSeconds 过期时间
     * @param expireSecondsWhenNull 当值为空时，写入空值的缓存时间, 用于防止空穿透
     * @param callback 回调方法
     */
    inline fun <reified T>fetch(key: String, expireSeconds: Long = 300L, expireSecondsWhenNull: Long = 10L, callback: () -> T): T? {
        val client = redisTemplate.opsForValue()
        val str = client.get(key)
        if (str == null) {
            val result = callback()
            val expires = if (result == null) expireSecondsWhenNull else expireSeconds
            client.set(key, JSON.toJSONString(result), expires, TimeUnit.SECONDS)
            return result
        }
        return JSON.parseObject(str, T::class.java)
    }
}