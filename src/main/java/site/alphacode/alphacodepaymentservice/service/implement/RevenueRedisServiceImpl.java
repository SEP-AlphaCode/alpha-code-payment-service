package site.alphacode.alphacodepaymentservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RevenueRedisServiceImpl {

    private final RedisTemplate<String, Long> redisTemplate;

    private String buildKey(int year, int month) {
        return String.format("revenue:%d:%d", year, month);
    }

    // changed amount type to long to avoid overflow and allow setting absolute values
    public void increaseRevenue(int year, int month, long amount) {
        String key = buildKey(year, month);
        redisTemplate.opsForValue().increment(key, amount);
    }

    // new: set absolute revenue value (used by initializer to avoid double-counting on restart)
    public void setRevenue(int year, int month, long amount) {
        String key = buildKey(year, month);
        redisTemplate.opsForValue().set(key, amount);
    }

    public Long getRevenue(int year, int month) {
        String key = buildKey(year, month);
        Long value = redisTemplate.opsForValue().get(key);
        return value != null ? value : 0L;
    }

    // set revenue value and apply a TTL
    public void setRevenueWithTTL(int year, int month, long amount, Duration ttl) {
        String key = buildKey(year, month);
        redisTemplate.opsForValue().set(key, amount, ttl);
    }

    // refresh/extend TTL for a key by year/month
    public boolean expireRevenue(int year, int month, Duration ttl) {
        String key = buildKey(year, month);
        return Boolean.TRUE.equals(redisTemplate.expire(key, ttl));
    }

    // delete a revenue key by year/month
    public void deleteRevenue(int year, int month) {
        String key = buildKey(year, month);
        redisTemplate.delete(key);
    }

    // list keys matching pattern (useful for deletion on shutdown)
    public Set<String> listRevenueKeys() {
        return redisTemplate.keys("revenue:*");
    }

    // delete by full key string
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    // expire by full key string
    public boolean expireKey(String key, Duration ttl) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, ttl));
    }


}
