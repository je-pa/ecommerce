/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.global.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;


@RequiredArgsConstructor
public abstract class RedisRepository<K, V> {
  private final String PREFIX;
  private final RedisTemplate<String, V> redisTemplate;

  public void set(K key, V value, Duration expiration) {
    redisTemplate.opsForValue().set(PREFIX + key, value, expiration);
  }

  public V getByKey(K key) {
    return redisTemplate.opsForValue().get(PREFIX + key);
  }

  public Boolean deleteByKey(K key) {
    return redisTemplate.delete(PREFIX + key);
  }

  public Boolean hasKey(K key) {
    return redisTemplate.hasKey(PREFIX + key);
  }

  public Duration getRemainingExpirationByKey(K key) {
    Long expire = redisTemplate.getExpire(PREFIX + key);
    if (expire != null && expire >= 0) {
      return Duration.ofSeconds(expire);
    } else {
      return null; // 유효 기간이 없거나 키가 존재하지 않는 경우
    }
  }
}