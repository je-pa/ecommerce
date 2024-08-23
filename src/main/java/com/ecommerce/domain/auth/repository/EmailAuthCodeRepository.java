/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.domain.auth.repository;

import com.ecommerce.global.repository.RedisRepository;
import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmailAuthCodeRepository extends RedisRepository<String, String> {
  private static final String PREFIX = "email-auth-code:";
  private static final Duration TIMEOUT = Duration.ofMinutes(1);

  public EmailAuthCodeRepository(RedisTemplate<String, String> redisTemplate) {
    super(PREFIX, redisTemplate);
  }

  public void setWithDurationByKey(String key, String value) {
    super.set(key, value, TIMEOUT);
  }
}
