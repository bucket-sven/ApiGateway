package com.sunshine.api.util

import com.alibaba.fastjson.JSON
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.RedisTemplate
import java.lang.reflect.ParameterizedType
import java.util.*
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

class RedisUtil(val redisTemplate: RedisTemplate<String, String>) : RedisOperations<String, String> by redisTemplate {
    /**
     * 缓存普通对象
     *
     * @param key 缓存的key
     * @param expireSeconds 过期时间
     * @param expireSecondsWhenNull 当值为空时，写入空值的缓存时间, 用于防止空穿透
     * @param callback 回调方法
     */
    inline fun <reified T>fetchObject(key: String, expireSeconds: Long = 300L, expireSecondsWhenNull: Long = 10L, callback: () -> T?): T? {
        val client = redisTemplate.opsForValue()
        val str = client.get(key)
        if (str == null) {
            val result = callback()
            val expires = if (result == null) expireSecondsWhenNull else expireSeconds
            client.set(key, JSON.toJSONString(result), expires, TimeUnit.SECONDS)
            return result!!
        }
        return JSON.parseObject(str, T::class.java)
    }

    /**
     * 缓存Optional对象
     *
     * @param key 缓存的key
     * @param expireSeconds 过期时间
     * @param expireSecondsWhenNull 当值为空时，写入空值的缓存时间, 用于防止空穿透
     * @param callback 回调方法
     */
    inline fun <reified T>fetchOptional(key: String, expireSeconds: Long = 300L, expireSecondsWhenNull: Long = 10L, callback: () -> Optional<T>): T? {
        val client = redisTemplate.opsForValue()
        val str = client.get(key)
        if (str == null) {
            val result = callback()
            val expires = if (!result.isPresent) expireSecondsWhenNull else expireSeconds
            client.set(key, JSON.toJSONString(result), expires, TimeUnit.SECONDS)
            if (result.isPresent) {
                return result.get()
            }
            return null
        }
        return JSON.parseObject(str, T::class.java)
    }

    /**
     * 缓存集合类型的数据
     *
     * @param key 缓存的key
     * @param expireSeconds 过期时间
     * @param expireSecondsWhenNull 当值为空时，写入空值的缓存时间, 用于防止空穿透
     * @param callback 回调方法
     */
    inline fun <reified T>fetchArray(key: String, expireSeconds: Long = 300L, expireSecondsWhenNull: Long = 10L, callback: () -> Iterable<T>?): Iterable<T>? {
        val client = redisTemplate.opsForValue()
        val str = client.get(key)
        if (str == null) {
            val result = callback()
            val expires = if (result == null || result.count() == 0) expireSecondsWhenNull else expireSeconds
            client.set(key, JSON.toJSONString(result), expires, TimeUnit.SECONDS)
            return result
        }
        return JSON.parseArray(str, T::class.java)
    }
}