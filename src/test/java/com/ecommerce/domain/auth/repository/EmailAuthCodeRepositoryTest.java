package com.ecommerce.domain.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.global.security.util.MyEncoder;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

class EmailAuthCodeRepositoryTest extends IntegrationTestSupport {
  @Autowired
  private EmailAuthCodeRepository emailAuthCodeRepository;

  @Autowired
  private MyEncoder myEncoder;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @AfterEach
  void tearDown() {
    Set<String> keys = redisTemplate.keys("*"); // 모든 키를 가져옴
    if (keys != null && !keys.isEmpty()) {
      redisTemplate.delete(keys); // 키들 삭제
    }
  }

  @DisplayName("이메일 인증 코드가 redis에 저장된다.")
  @Test
  void setWithDurationByKey(){
    // given
    String email = "test@email.com";
    String encrypted = myEncoder.encrypt(email);
    String authCode = "123456";

    // when
    emailAuthCodeRepository.setWithDurationByKey(encrypted, authCode);

    // then
    assertThat(emailAuthCodeRepository.getByKey(encrypted)).isEqualTo(authCode);
  }
}