package com.lamduck2005.linkshortener.service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.lamduck2005.linkshortener.entity.Snippet;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Lưu dữ liệu vĩnh viễn
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // Lưu dữ liệu có thời gian sống (TTL) - Ví dụ: lưu OTP 5 phút
    public void setTimeToLive(String key, Object value, long timeoutInMinutes) {
        redisTemplate.opsForValue().set(key, value, timeoutInMinutes, TimeUnit.MINUTES);
    }

    // Lấy dữ liệu
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Xóa dữ liệu
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // Kiểm tra key có tồn tại không
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // Set nếu key chưa tồn tại (tránh ghi đè)
    public Boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    // Tăng giá trị số (cho counter)
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    // Set với expire theo giây
    public void setExpireSeconds(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }





    //Các hàm thao tác cache snippet
    public void cacheSnippet(String shortCode, Snippet snippet) {
        String key = "snippet:" + shortCode;

        if(snippet.getExpiresAt() != null){
            // Tính thời gian còn lại đến khi expire
            long ttlSeconds = snippet.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond();
            if(ttlSeconds > 0) setExpireSeconds(key, snippet, ttlSeconds);
        } else {
            set(key, snippet);
        }
    }

    public Snippet getCachedSnippet(String shortCode) {
        String key = "snippet:" + shortCode;
        return (Snippet) get(key);
    }

    public void invalidateSnippetCache(String shortCode) {
        String key = "snippet:" + shortCode;
        delete(key);
    }

}
